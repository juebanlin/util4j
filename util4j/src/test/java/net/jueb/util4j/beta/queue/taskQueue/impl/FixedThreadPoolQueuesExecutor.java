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
package net.jueb.util4j.beta.queue.taskQueue.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.mina.filter.executor.UnorderedThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.beta.queue.taskQueue.Task;
import net.jueb.util4j.beta.queue.taskQueue.TaskQueueExecutor;
import net.jueb.util4j.beta.queue.taskQueue.TaskQueueGroupExecutor;

/**
 * A {@link ThreadPoolExecutor} that maintains the order of {@link QueueTask}s.
 * <p>
 * If you don't need to maintain the order of events per session, please use
 * {@link UnorderedThreadPoolExecutor}.

 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * @org.apache.xbean.XBean
 */
public class FixedThreadPoolQueuesExecutor extends ThreadPoolExecutor implements TaskQueueGroupExecutor{
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
     *等待处理的队列
     */
    private final BlockingQueue<String> waitingQueues = new LinkedBlockingQueue<String>();

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
    public FixedThreadPoolQueuesExecutor() {
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
    public FixedThreadPoolQueuesExecutor(int maximumPoolSize) {
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
    public FixedThreadPoolQueuesExecutor(int corePoolSize, int maximumPoolSize) {
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
    public FixedThreadPoolQueuesExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
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
    public FixedThreadPoolQueuesExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
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
    	synchronized (queues) {
    		TaskQueueImpl queue = queues.get(queueName);
            if (queue == null) 
            {
                queue = new TaskQueueImpl(queueName);
                queues.put(queueName, queue);
            }
            return queue;
		}
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
    private void addWorker() {
        synchronized (workers) {
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
    }

    /**
     * Add a new Worker only if there are no idle worker.
     */
    private void addWorkerIfNecessary() {
        if (idleWorkers.get() == 0) {
            synchronized (workers) {
                if (workers.isEmpty() || (idleWorkers.get() == 0)) {
                    addWorker();
                }
            }
        }
    }

    private void removeWorker() {
        synchronized (workers) {
            if (workers.size() <= super.getCorePoolSize()) {
                return;
            }
            waitingQueues.offer(QUEUE_NAME_EXIT_SIGNAL);
        }
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
                waitingQueues.offer(QUEUE_NAME_EXIT_SIGNAL);
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
        while ((queueName = waitingQueues.poll()) != null) 
        {
            if (queueName == QUEUE_NAME_EXIT_SIGNAL) {
                waitingQueues.offer(QUEUE_NAME_EXIT_SIGNAL);
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
    	execute(queueName,new RunnableTaskAdapter(task));
    }

    @Override
	public TaskQueueExecutor execute(String queueName, Task task) {
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
                waitingQueues.offer(queueName);
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
                addWorker();
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
                addWorker();
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
                    if (queueName == null) 
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
                    if (queueName == QUEUE_NAME_EXIT_SIGNAL) {
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
                    FixedThreadPoolQueuesExecutor.this.completedTaskCount += completedTaskCount.get();
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
        	String queueName = null;
            long currentTime = System.currentTimeMillis();
            long deadline = currentTime + getKeepAliveTime(TimeUnit.MILLISECONDS);
            for (;;) {
                try {
                    long waitTime = deadline - currentTime;
                    if (waitTime <= 0) 
                    {
                        break;
                    }
                    try {
                        queueName = waitingQueues.poll(waitTime, TimeUnit.MILLISECONDS);
                        break;
                    } finally {
                        if (queueName == null) {
                            currentTime = System.currentTimeMillis();
                        }
                    }
                } catch (InterruptedException e) {
                    // Ignore.
                    continue;
                }
            }
            return queueName;
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
			offer(new RunnableTaskAdapter(command));
		}
		@Override
		public void execute(Task task) {
			offer(task);
		}
    }
    
    private static class Test{
    	 public static volatile int a;
    	 public static volatile int b;
    	 final FixedThreadPoolQueuesExecutor o=new FixedThreadPoolQueuesExecutor(4,4);
    	 /**
    	  * 测试有序和队列任务互不影响
    	  */
    	 public void test1()
    	 {
    			new Thread(new Runnable() {
    				@Override
    				public void run() {
    					for(int i=0;i<1000000;i++)
    					{
    						final int x=i;
    						o.execute("a",new Runnable() {
    							@Override
    							public void run() {
    								try {
    									Thread.sleep(1000);
    								} catch (InterruptedException e) {
    									e.printStackTrace();
    								}
    								if(x==0)
    								{
    									System.err.println("x:"+x);
    								}
    								if(x%2==0)
    								{
    									Test.a++;
    								}else
    								{
    									Test.a--;
    								}
    								if(x==1000000)
    								{
    									System.err.println(Test.a);
    								}
    								System.out.println("x:"+x+",a:"+a);
    							}
    						});
    					}
    				}
    			}).start();
    			new Thread(new Runnable() {
    				@Override
    				public void run() {
    					for(int i=0;i<1000000;i++)
    					{
    						final int y=i;
    						o.execute("b",new Runnable() {
    							@Override
    							public void run() {
    								if(y==0)
    								{
    									System.err.println("y:"+y);
    								}
    								if(y%2==0)
    								{
    									Test.b++;
    								}else
    								{
    									Test.b--;
    								}
    								if(y==1000000)
    								{
    									System.err.println(Test.b);
    								}
    								System.out.println("y:"+y+",b:"+b);
    							}
    						});
    					}
    				}
    			}).start();
    	 }
    	 
    	 public Task buildTask()
    	 {
    		 return new Task() {
				@Override
				public void run() {
					
				}
				
				@Override
				public String name() {
					return UUID.randomUUID().toString();
				}
			};
    	 }
    	 
    	 
    	 Map<Integer,Long> startTime=new HashMap<>();
    	 /**
    	  * 测试速度
    	  */
    	 public void test2()
    	 {
    		 new Thread(new Runnable() {
 				@Override
 				public void run() {
 					int taskCount=10000000;
 					int index=4;
 					//开始时间标记任务
 					for(int i=1;i<=index;i++)
 					{
 						Task t=buildTask();
 						final int queueName=i;
 						o.execute(queueName+"", new Task() {
							@Override
							public void run() {
								startTime.put(queueName, System.currentTimeMillis());
							}
							@Override
							public String name() {
								return "";
							}
						});
 					}
 					//各个队列随机插入影响任务
 					for(int i=1;i<=taskCount;i++)
 					{
 						Task t=buildTask();
 						int queue=RandomUtils.nextInt(index)+1;
 						o.execute(queue+"",t);
 					}
 					//结束时间标记任务
 					for(int i=1;i<=index;i++)
 					{
 						Task t=buildTask();
 						final int queueName=i;
 						o.execute(queueName+"", new Task() {
							@Override
							public void run() {
								long time= System.currentTimeMillis()-startTime.get(queueName);
								System.err.println("队列："+queueName+",最后一个任务完成,队列耗时:"+time);
							}
							@Override
							public String name() {
								return "";
							}
						});
 					}
 				}
 			}).start();
    	 }
    }
   
    public static void main(String[] args) {
		new Test().test2();
	}
}
