/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package net.jueb.util4j.queue.taskQueue.impl.order.queueExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.mina.filter.executor.UnorderedThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.queue.taskQueue.Task;
import net.jueb.util4j.queue.taskQueue.TaskQueueExecutor;
import net.jueb.util4j.queue.taskQueue.TaskQueuesExecutor;

/**
 * A {@link ThreadPoolExecutor} that maintains the order of {@link QueueTask}s.
 * <p>
 * If you don't need to maintain the order of events per session, please use
 * {@link UnorderedThreadPoolExecutor}.

 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * @org.apache.xbean.XBean
 */
public class FixedThreadPoolQueuesExecutor_Condition extends ThreadPoolExecutor implements TaskQueuesExecutor{
    /** A logger for this class (commented as it breaks MDCFlter tests) */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /** A default value for the initial pool size */
    private static final int DEFAULT_INITIAL_THREAD_POOL_SIZE = 0;

    /** A default value for the maximum pool size */
    private static final int DEFAULT_MAX_THREAD_POOL = 16;

    /** A default value for the KeepAlive delay */
    private static final int DEFAULT_KEEP_ALIVE = 30;

    private static final String QUEUE_NAME_EXIT_SIGNAL = "EXIT_SIGNAL";

    public static final String QUEUE_NAME_DEFAULT_QUEUE="DEFAULT_QUEUE";
    /**
     * 让线程自旋
     */
    public static final String QUEUE_NAME_NOOP="QUEUE_NOOP";

	/**
     *等待处理的队列
     */
    private final Queue<String> waitingQueues = new ConcurrentLinkedQueue<String>();

    private final Set<Worker> workers = new HashSet<Worker>();

    private volatile int largestPoolSize;

    /**
     * 活动线程数量
     */
    private final AtomicInteger idleWorkers = new AtomicInteger();

    private long completedTaskCount;

    private volatile boolean shutdown;

    /**
     * Creates a default ThreadPool, with default values :
     * - minimum pool size is 0
     * - maximum pool size is 16
     * - keepAlive set to 30 seconds
     * - A default ThreadFactory
     * - All events are accepted
     */
    public FixedThreadPoolQueuesExecutor_Condition() {
        this(DEFAULT_INITIAL_THREAD_POOL_SIZE, DEFAULT_MAX_THREAD_POOL, DEFAULT_KEEP_ALIVE, TimeUnit.SECONDS, Executors
                .defaultThreadFactory());
    }

    /**
     * Creates a default ThreadPool, with default values :
     * - minimum pool size is 0
     * - keepAlive set to 30 seconds
     * - A default ThreadFactory
     * - All events are accepted
     * 
     * @param maximumPoolSize The maximum pool size
     */
    public FixedThreadPoolQueuesExecutor_Condition(int maximumPoolSize) {
        this(DEFAULT_INITIAL_THREAD_POOL_SIZE, maximumPoolSize, DEFAULT_KEEP_ALIVE, TimeUnit.SECONDS, Executors
                .defaultThreadFactory());
    }

    /**
     * Creates a default ThreadPool, with default values :
     * - keepAlive set to 30 seconds
     * - A default ThreadFactory
     * - All events are accepted
     *
     * @param corePoolSize The initial pool sizePoolSize
     * @param maximumPoolSize The maximum pool size
     */
    public FixedThreadPoolQueuesExecutor_Condition(int corePoolSize, int maximumPoolSize) {
        this(corePoolSize, maximumPoolSize, DEFAULT_KEEP_ALIVE, TimeUnit.SECONDS, Executors.defaultThreadFactory());
    }

    /**
     * Creates a default ThreadPool, with default values :
     * - A default ThreadFactory
     * - All events are accepted
     * 
     * @param corePoolSize The initial pool sizePoolSize
     * @param maximumPoolSize The maximum pool size
     * @param keepAliveTime Default duration for a thread
     * @param unit Time unit used for the keepAlive value
     */
    public FixedThreadPoolQueuesExecutor_Condition(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory());
    }

    /**
     * Creates a new instance of a OrderedThreadPoolExecutor.
     * 
     * @param corePoolSize The initial pool sizePoolSize
     * @param maximumPoolSize The maximum pool size
     * @param keepAliveTime Default duration for a thread
     * @param unit Time unit used for the keepAlive value
     * @param threadFactory The factory used to create threads
     * @param eventQueueHandler The queue used to store events
     */
    public FixedThreadPoolQueuesExecutor_Condition(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            ThreadFactory threadFactory) {
        // We have to initialize the pool with default values (0 and 1) in order to
        // handle the exception in a better way. We can't add a try {} catch() {}
        // around the super() call.
        super(DEFAULT_INITIAL_THREAD_POOL_SIZE, 1, keepAliveTime, unit, new SynchronousQueue<Runnable>(),
                threadFactory, new AbortPolicy());

        if (corePoolSize < DEFAULT_INITIAL_THREAD_POOL_SIZE) {
            throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
        }

        if ((maximumPoolSize == 0) || (maximumPoolSize < corePoolSize)) {
            throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
        }

        // Now, we can setup the pool sizes
        super.setCorePoolSize(corePoolSize);
        super.setMaximumPoolSize(maximumPoolSize);
    }

    
    Map<String,TaskQueueImpl> queues=new HashMap<>();
   
    /**
     * 取队列
     * @param queueName
     * @return
     */
    private TaskQueueImpl getTaskQueue(String queueName) {
        return queues.get(queueName);
    }
    
    private TaskQueueImpl getTaskQueueOrCreate(String queueName) {
    	TaskQueueImpl queue = queues.get(queueName);
        if (queue == null) 
        {
           synchronized (queues) {
        	   queue = queues.get(queueName);
               if (queue == null) 
               {
                   queue = new TaskQueueImpl(queueName);
                   queues.put(queueName, queue);
               }
               return queue;
           }
        }
        return queue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        // Ignore the request.  It must always be AbortPolicy.
    }

    /**
     * Add a new thread to execute a task, if needed and possible.
     * It depends on the current pool size. If it's full, we do nothing.
     */
    private void addWorkerUnsafe() {
    	 if (workers.size() >= super.getMaximumPoolSize()) {
             return;
         }

         // Create a new worker, and add it to the thread pool
         Worker worker = new Worker();
         Thread thread = getThreadFactory().newThread(worker);

         // As we have added a new thread, it's considered as idle.
         idleWorkers.incrementAndGet();

         // Now, we can start it.
         thread.start();
         workers.add(worker);

         if (workers.size() > largestPoolSize) {
             largestPoolSize = workers.size();
         }
    }

    /**
     * 如果活动的线程数量=0则添加线程
     */
    private void addWorkerIfNecessary() {
        if (idleWorkers.get() == 0) {
            synchronized (workers) {
                if (workers.isEmpty() || (idleWorkers.get() == 0)) {
                    addWorkerUnsafe();
                }
            }
        }
    }

    private void removeWorker() {
        synchronized (workers) {
            if (workers.size() <= super.getCorePoolSize()) {
                return;
            }
            waitingQueuesOffer(QUEUE_NAME_EXIT_SIGNAL);
        }
    }
    
    final ReentrantLock lock=new ReentrantLock();
    final Condition waitingQueuesNotEmpty = lock.newCondition();//读线程条件
    
    private void waitingQueuesOffer(String queueName)
    {
    	boolean offer=waitingQueues.offer(queueName);
    	if(offer)
    	{
    		lock.lock();
        	try {
        		 waitingQueuesNotEmpty.signalAll();//唤醒读线程 
    		} finally {
    			lock.unlock();
    		}
    	}
    }
    
    private String waitingQueuesPoll()
    {
    	 return waitingQueues.poll();
    }
    
    private String waitingQueuesTake()
    {
    	String queue=null;
    	for(;;)
		{
    		queue=waitingQueuesPoll();
    		lock.lock();
    		try {
    			if(queue!=null)
    			{
    				break;
    			}
    			waitingQueuesNotEmpty.awaitUninterruptibly();//等待不为空信号		
			} finally {
				lock.unlock();
			}
		}
    	return queue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaximumPoolSize() {
        return super.getMaximumPoolSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaximumPoolSize(int maximumPoolSize) {
        if ((maximumPoolSize <= 0) || (maximumPoolSize < super.getCorePoolSize())) {
            throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
        }

        synchronized (workers) {
            super.setMaximumPoolSize(maximumPoolSize);
            int difference = workers.size() - maximumPoolSize;
            while (difference > 0) {
                removeWorker();
                --difference;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {

        long deadline = System.currentTimeMillis() + unit.toMillis(timeout);

        synchronized (workers) {
            while (!isTerminated()) {
                long waitTime = deadline - System.currentTimeMillis();
                if (waitTime <= 0) {
                    break;
                }

                workers.wait(waitTime);
            }
        }
        return isTerminated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTerminated() {
        if (!shutdown) {
            return false;
        }

        synchronized (workers) {
            return workers.isEmpty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        if (shutdown) 
        {
            return;
        }
        shutdown = true;
        synchronized (workers) 
        {
            for (int i = workers.size(); i > 0; i--) 
            {
            	waitingQueuesOffer(QUEUE_NAME_EXIT_SIGNAL);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Runnable> shutdownNow() {
        shutdown();
        List<Runnable> answer = new ArrayList<Runnable>();
        String queueName;
        while ((queueName = waitingQueuesPoll()) != null) 
        {
            if (queueName == QUEUE_NAME_EXIT_SIGNAL) {
            	waitingQueuesOffer(QUEUE_NAME_EXIT_SIGNAL);
                Thread.yield(); // Let others take the signal.
                continue;
            }
            TaskQueueImpl tasksQueue = getTaskQueue(queueName);
            synchronized (tasksQueue) {
                for (Runnable task :tasksQueue) 
                {
                    answer.add(task);
                }
                tasksQueue.clear();
            }
        }
        return answer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute(Runnable task) {
    	execute(QUEUE_NAME_DEFAULT_QUEUE,task);
    }
    
    public final void execute(String queueName,Runnable task) {
    	execute(queueName,TaskQueueUtil.convert(task));
    }
    
    @Override
	public TaskQueueExecutor execute(String queueName, List<Task> tasks) {
    	if(shutdown)
    	{
    		for(Task t:tasks)
    		{
    			rejectTask(t);
    		}
    	}
    	if(queueName==null || queueName.isEmpty())
    	{
    		throw new RuntimeException("queueName isEmpty");
    	}
    	TaskQueueImpl tasksQueue = getTaskQueueOrCreate(queueName);
        // propose the new event to the event queue handler. If we
        // use a throttle queue handler, the message may be rejected
        // if the maximum size has been reached.
    	// Ok, the message has been accepted
        synchronized (tasksQueue) 
        {
            //加入任务队列
       	 	tasksQueue.addAll(tasks);
            if (tasksQueue.processingCompleted) 
            {//如果该队列没有线程占用
           	 	tasksQueue.processingCompleted = false;
           	 	waitingQueuesOffer(queueName);
            }
        }
        addWorkerIfNecessary();
		return tasksQueue;
	
	}
    
    
    @Override
	public TaskQueueExecutor execute(String queueName,Task task) {
    	if(shutdown)
    	{
    		rejectTask(task);
    	}
    	if(queueName==null || queueName.isEmpty())
    	{
    		throw new RuntimeException("queueName isEmpty");
    	}
    	TaskQueueImpl tasksQueue = getTaskQueueOrCreate(queueName);
        // propose the new event to the event queue handler. If we
        // use a throttle queue handler, the message may be rejected
        // if the maximum size has been reached.
    	// Ok, the message has been accepted
        synchronized (tasksQueue) 
        {
            //加入任务队列
       	 	tasksQueue.offer(task);
            if (tasksQueue.processingCompleted) 
            {//如果该队列没有线程占用
           	 	tasksQueue.processingCompleted = false;
           	 	waitingQueuesOffer(queueName);
            }
        }
        addWorkerIfNecessary();
		return tasksQueue;
	}

	@Override
	public TaskQueueExecutor getQueue(String queueName) {
		return getTaskQueue(queueName);
	}

	@Override
	public TaskQueueExecutor getQueueOrCreate(String queueName) {
		return getTaskQueueOrCreate(queueName);
	}

	private void rejectTask(Runnable task) {
        getRejectedExecutionHandler().rejectedExecution(task, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getActiveCount() {
        synchronized (workers) {
            return workers.size() - idleWorkers.get();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCompletedTaskCount() {
        synchronized (workers) {
            long answer = completedTaskCount;
            for (Worker w : workers) {
                answer += w.completedTaskCount.get();
            }
            return answer;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLargestPoolSize() {
        return largestPoolSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPoolSize() {
        synchronized (workers) {
            return workers.size();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTaskCount() {
        return getCompletedTaskCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTerminating() {
        synchronized (workers) {
            return isShutdown() && !isTerminated();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int prestartAllCoreThreads() {
        int answer = 0;
        synchronized (workers) {
            for (int i = super.getCorePoolSize() - workers.size(); i > 0; i--) {
                addWorkerUnsafe();
                answer++;
            }
        }
        return answer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean prestartCoreThread() {
        synchronized (workers) {
            if (workers.size() < super.getCorePoolSize()) {
                addWorkerUnsafe();
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlockingQueue<Runnable> getQueue() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void purge() {
        // Nothing to purge in this implementation.
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean remove(Runnable task) {
        String queueName=QUEUE_NAME_DEFAULT_QUEUE;
        TaskQueueImpl taskQueue = getTaskQueue(queueName);
        if (taskQueue == null) {
            return false;
        }
        boolean removed;
        synchronized (taskQueue) {
            removed = taskQueue.remove(task);
        }
        if (removed) {
//        	getQueueEventListener().polled(this, task);
        }
        return removed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCorePoolSize() {
        return super.getCorePoolSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0) {
            throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
        }
        if (corePoolSize > super.getMaximumPoolSize()) {
            throw new IllegalArgumentException("corePoolSize exceeds maximumPoolSize");
        }

        synchronized (workers) {
            if (super.getCorePoolSize() > corePoolSize) {
                for (int i = super.getCorePoolSize() - corePoolSize; i > 0; i--) {
                    removeWorker();
                }
            }
            super.setCorePoolSize(corePoolSize);
        }
    }

    private class Worker implements Runnable {

        private AtomicLong completedTaskCount = new AtomicLong(0);

        private Thread thread;

        public void run() {
            thread = Thread.currentThread();
            try {
                for (;;) 
                {
                	//获取一个队列
                	String queueName = fetchQueueName();
					idleWorkers.decrementAndGet();
					if(queueName==null)
					{
						synchronized (workers) 
                        {
                            if (workers.size() > getCorePoolSize()) 
                            {
                                // Remove now to prevent duplicate exit.
                                workers.remove(this);
                                break;
                            }
                        }
					}
                    if (queueName == QUEUE_NAME_EXIT_SIGNAL) 
                    {
                        break;
                    }
                    try {
                        if (queueName != null) 
                        {
                            runTasks(getTaskQueue(queueName));
                        }
                    } finally {
                        idleWorkers.incrementAndGet();
                    }
                }
            } finally {
                synchronized (workers) {
                    workers.remove(this);
                    FixedThreadPoolQueuesExecutor_Condition.this.completedTaskCount += completedTaskCount.get();
                    workers.notifyAll();
                }
            }
        }
        
        /**
         * 取出一个队列
         * 当某个队列有任务添加时,此队列会被加入线程处理队列
         * @return
         */
        private String fetchQueueName() {
            return waitingQueuesTake();
        }

        private void runTasks(TaskQueueImpl taskQueue) {
            for (;;) 
            {
                Runnable task;
                synchronized (taskQueue) 
                {
                    task = taskQueue.poll();
                    if (task == null) 
                    {//标记队列已经处理完成
                    	taskQueue.processingCompleted = true;
                        break;
                    }
                }
                runTask(task);
            }
        }

        private void runTask(Runnable task) {
            beforeExecute(thread, task);
            boolean ran = false;
            try {
                task.run();
                ran = true;
                afterExecute(task, null);
                completedTaskCount.incrementAndGet();
            } catch (RuntimeException e) {
                if (!ran) {
                    afterExecute(task, e);
                }
                throw e;
            }
        }
    }

    private class TaskQueueImpl extends DefaultTaskQueue implements TaskQueueExecutor{
        /**
		 * 
		 */
		private static final long serialVersionUID = -741373262667864219L;
		public TaskQueueImpl(String name) {
			super(name);
		}
		/**The current task state 
         * 此队列是否处理完成
         */
        private volatile boolean processingCompleted = true;
        
		@Override
		public void execute(Runnable command) {
			offer(TaskQueueUtil.convert(command));
		}
		@Override
		public void execute(Task task) {
			offer(task);
		}
		
		@Override
		public void execute(List<Task> tasks) {
			addAll(tasks);
		}
    }
}
