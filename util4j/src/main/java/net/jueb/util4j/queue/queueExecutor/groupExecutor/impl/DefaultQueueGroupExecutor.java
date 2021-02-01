package net.jueb.util4j.queue.queueExecutor.groupExecutor.impl;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import net.jueb.util4j.lock.waiteStrategy.BlockingWaitConditionStrategy;
import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutorService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.lock.waiteStrategy.WaitCondition;
import net.jueb.util4j.lock.waiteStrategy.WaitConditionStrategy;
import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupManager.KeyGroupEventListener;
import net.jueb.util4j.queue.queueExecutor.queue.RunnableQueueEventWrapper;
import net.jueb.util4j.thread.NamedThreadFactory;

public class DefaultQueueGroupExecutor extends AbstractExecutorService implements QueueGroupExecutorService {
    
	protected Logger log=LoggerFactory.getLogger(getClass());
	
    private static final int DEFAULT_INITIAL_THREAD_POOL_SIZE = 0;

    private static final int DEFAULT_MAX_THREAD_POOL = 8;

    private static final int DEFAULT_KEEP_ALIVE_SEC = 30;
   
	private volatile ThreadFactory threadFactory;
    
    /**
	 * 核心线程数，核心线程会一直存活，即使没有任务需要处理。
	 * 当线程数小于核心线程数时，即使现有的线程空闲，线程池也会优先创建新线程来处理任务，而不是直接交给现有的线程处理。 
	 * 核心线程在allowCoreThreadTimeout被设置为true时会超时退出，默认情况下不会退出。
	 */
	private volatile int corePoolSize;
	/**
	 *当线程数大于或等于核心线程，且任务队列已满时，线程池会创建新的线程，直到线程数量达到maxPoolSize。
	 *如果线程数已等于maxPoolSize，且任务队列已满，则已超出线程池的处理能力，线程池会拒绝处理任务而抛出异常。
	 */
    private volatile int maximumPoolSize;
    /**
     * 默认都是0纳秒，当线程没有任务处理后，保持多长时间，cachedPoolSize是默认60s，不推荐使用。
     */
    private volatile long keepAliveNanoTime;
    /**
     * 是否允许核心线程空闲退出，默认值为false。
     */
    private volatile boolean allowCoreThreadTimeOut;
    /**
     * 记录了线程池在整个生命周期中曾经出现的最大线程个数。
     */
    private volatile int largestPoolSize;
    
    /**
     * 系统队列
     */
    private final SystemQueue systemQueue;
    
    /**
     * 队列处理线程
     */
    private final Set<Worker> workers = new HashSet<Worker>();

    /**
     * 待命线程数量
     */
    private final AtomicInteger idleWorkers = new AtomicInteger();

    private volatile boolean shutdown;

    private final WaitConditionStrategy waitConditionStrategy;
    private final QueueGroupManager queueMananger;
    
    /**
     * 辅助执行器
     * 用于启动工作线程或者其它逻辑处理
     */
    private Executor assistExecutor;
    /**
      * 设置worker线程上下文classloder为null
     */
    private boolean nullContextClassLoader=false;


	private static DefaultQueueManager default_QueueGroupManager(){
		return new DefaultQueueManager(QueueFactory.MPSC_QUEUE_FACTORY);
	}

	private static final Queue<Runnable> default_BossQueue(){
		return new ConcurrentLinkedQueue<>();
	}

	private static final WaitConditionStrategy default_waitConditionStrategy(){
		return new BlockingWaitConditionStrategy();
	}
	private static final ThreadFactory default_ThreadFactory(){
		return new NamedThreadFactory("queueGroup",true);
	}
    
    public DefaultQueueGroupExecutor() {
        this(DEFAULT_INITIAL_THREAD_POOL_SIZE, DEFAULT_MAX_THREAD_POOL);
    }

    public DefaultQueueGroupExecutor(int corePoolSize, int maximumPoolSize) {
        this(corePoolSize, maximumPoolSize,default_BossQueue());
    }
    
    protected DefaultQueueGroupExecutor(int corePoolSize, int maximumPoolSize,Queue<Runnable> bossQueue) {
            this(corePoolSize, maximumPoolSize, DEFAULT_KEEP_ALIVE_SEC, TimeUnit.SECONDS, 
            		Executors.defaultThreadFactory(),default_waitConditionStrategy(),
            		bossQueue,default_QueueGroupManager(),null);
        }
    
    protected DefaultQueueGroupExecutor(int corePoolSize, int maximumPoolSize,
        	Queue<Runnable> bossQueue,QueueGroupManager queueMananger) {
            this(corePoolSize, maximumPoolSize, DEFAULT_KEEP_ALIVE_SEC, TimeUnit.SECONDS, 
            		Executors.defaultThreadFactory(),default_waitConditionStrategy(),
            		bossQueue,queueMananger,new DirectExecutor());
        }
    
    protected DefaultQueueGroupExecutor(int corePoolSize, int maximumPoolSize,
    	Queue<Runnable> bossQueue,QueueGroupManager queueMananger,WaitConditionStrategy waitConditionStrategy) {
        this(corePoolSize, maximumPoolSize, DEFAULT_KEEP_ALIVE_SEC, TimeUnit.SECONDS, 
        		Executors.defaultThreadFactory(),waitConditionStrategy,
        		bossQueue,queueMananger,new DirectExecutor());
    }
    
    /**
     * 
     * @param corePoolSize 核心线程池数量
     * @param maximumPoolSize 最大线程数量
     * @param keepAliveTime 非核心线程活跃时长
     * @param unit 单位
     * @param threadFactory 线程工厂
     * @param waitConditionStrategy 线程等待机制
     * @param bossQueue 主队列
     * @param queueMananger 键值队列管理器
     * @param assistExecutor optional 辅助执行器,用于启动工作线程或处理其它逻辑
     */
    public DefaultQueueGroupExecutor(int corePoolSize, int maximumPoolSize, 
    		long keepAliveTime, TimeUnit unit,ThreadFactory threadFactory,
            WaitConditionStrategy waitConditionStrategy,Queue<Runnable> bossQueue,
            QueueGroupManager queueMananger,Executor assistExecutor) {
		if (corePoolSize < 0 
				||maximumPoolSize <= 0 
				||maximumPoolSize < corePoolSize 
				||keepAliveTime < 0 )
		{
			throw new IllegalArgumentException();
		}
		Objects.requireNonNull(threadFactory);
		Objects.requireNonNull(waitConditionStrategy);
		Objects.requireNonNull(queueMananger);
		Objects.requireNonNull(bossQueue);
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = maximumPoolSize;
		this.keepAliveNanoTime = unit.toNanos(keepAliveTime);
		this.threadFactory=threadFactory;
        this.waitConditionStrategy=waitConditionStrategy;
        this.assistExecutor=assistExecutor;
        this.queueMananger=queueMananger;
		this.queueMananger.setGroupEventListener((key,task)->{
			//当sqm有可以处理某队列的任务产生时,丢到系统队列,当系统队列
			systemExecute(task);
		});
        this.systemQueue=new SystemQueue(bossQueue);
    }
    
    public final ThreadFactory getThreadFactory() {
		return threadFactory;
	}

	public final void setThreadFactory(ThreadFactory threadFactory) {
		if(threadFactory==null)
		{
			throw new IllegalArgumentException("threadFactory is null");
		}
		this.threadFactory = threadFactory;
	}
	
	public Executor getAssistExecutor() {
		return assistExecutor;
	}

	public long getKeepAliveTime(TimeUnit unit) {
    	return unit.convert(keepAliveNanoTime,TimeUnit.NANOSECONDS);
	}

	public void setKeepAliveTime(long keepAliveTime,TimeUnit unit) {
		if (keepAliveTime < 0 || unit==null )
		{
			throw new IllegalArgumentException();
		}
		this.keepAliveNanoTime = unit.toNanos(keepAliveTime);
	}

	public boolean isAllowCoreThreadTimeOut() {
		return allowCoreThreadTimeOut;
	}

	public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
		this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
	}

	public boolean isNullContextClassLoader() {
		return nullContextClassLoader;
	}

	public void setNullContextClassLoader(boolean nullContextClassLoader) {
		this.nullContextClassLoader = nullContextClassLoader;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
	    if (corePoolSize < 0) {
	        throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
	    }
	    if (corePoolSize > getMaximumPoolSize()) {
	        throw new IllegalArgumentException("corePoolSize exceeds maximumPoolSize");
	    }
	    synchronized (workers) {
	        if (getCorePoolSize() > corePoolSize) {
	            for (int i = getCorePoolSize() - corePoolSize; i > 0; i--) {
	                removeWorker();
	            }
	        }
	        setCorePoolSize(corePoolSize);
	    }
	}

	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
	    if ((maximumPoolSize <= 0) || (maximumPoolSize < getCorePoolSize())) {
	        throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
	    }
	    synchronized (workers) {
	        setMaximumPoolSize(maximumPoolSize);
	        int difference = workers.size() - maximumPoolSize;
	        while (difference > 0) {
	            removeWorker();
	            --difference;
	        }
	    }
	}

	public int getPoolSize() {
	    synchronized (workers) {
	        return workers.size();
	    }
	}

	public int getActiveCount() {
	    synchronized (workers) {
	        return workers.size() - idleWorkers.get();
	    }
	}

	public int getLargestPoolSize() {
		return largestPoolSize;
	}
	
	protected void setLargestPoolSize(int size)
	{
		this.largestPoolSize=size;
	}

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

	public boolean isShutdown() {
	    return shutdown;
	}

	public boolean isTerminated() {
	    if (!shutdown) {
	        return false;
	    }
	    synchronized (workers) {
	        return workers.isEmpty();
	    }
	}

	/**
	 * 使用WorkerExitTask结束任务,防止之前的任务因停止而丢失
	 */
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
	        	systemExecute(exitTask);
	        }
	        waitConditionStrategy.signalAllWhenBlocking();
	    }
	}

	@NotNull
	@Override
	public List<Runnable> shutdownNow() {
		shutdown();
		List<Runnable> tasks=new ArrayList<>(systemQueue);
		systemQueue.clear();
		return tasks;
	}

	/**
	 * 同步关闭
	 */
	public void shutdownSync() {
		 shutdown();
	     for(;;)
	     {
	    	 if(workers.isEmpty())
	    	 {
	    		 break;
	    	 }
	    	 systemExecute(exitTask);
	         Thread.yield(); // Let others take the signal.
	         continue;
	     }
	}

	public boolean isTerminating() {
	    synchronized (workers) {
	        return isShutdown() && !isTerminated();
	    }
	}

	/**
	 * 唤醒工作线程(如果还没超过最大工作线程)
	 */
	public void addWorkerIfNecessary(){
		if (idleWorkers.get() > 0) {
			return;
		}
		if(assistExecutor!=null)
		{
			assistExecutor.execute(this::doAddWorker);
		}else
		{
			doAddWorker();
		}
	}
	
	/**
     * 如果活动的线程数量=0则添加线程
     */
    private void doAddWorker() {
        if (idleWorkers.get() == 0) {
            synchronized (workers) {
            	if (workers.size() >= getMaximumPoolSize()) {
            		//超过最大值
                    return;
                }
                if (workers.isEmpty() || (idleWorkers.get() == 0)) {
                	addWorkerUnsafe();
                }
            }
        }
    }
	
	private void addWorkerUnsafe() {
         Worker worker = new Worker();
         Thread thread = getThreadFactory().newThread(worker);
         if(nullContextClassLoader)
         {//设置线程不会引用其它classloader
             AccessController.doPrivileged(new PrivilegedAction<Void>() {
                 @Override
                 public Void run() {
                	 // Set to null to ensure we not create classloader leaks by holds a strong reference to the inherited
                     // classloader.
                     // See:
                     // - https://github.com/netty/netty/issues/7290
                     // - https://bugs.openjdk.java.net/browse/JDK-7008595
                	 thread.setContextClassLoader(null);
                     return null;
                 }
             });
         }
         idleWorkers.incrementAndGet();
         thread.start();
         workers.add(worker);
         if (workers.size() > getLargestPoolSize()) {
             setLargestPoolSize(workers.size());
         }
    }

    private void removeWorker() {
        synchronized (workers) {
            if (workers.size() <= getCorePoolSize()) {
                return;
            }
            systemExecute(exitTask);
        }
    }

    /**
     * 插槽队列处理任务
     * @author juebanlin
     */
    final Runnable exitTask=new WorkerExitTask() {
		@Override
		public void run() {
			log.info("WorkerExitTask Run");
		}
	};

	protected interface WorkerExitTask extends Runnable{
		
	}
    
	static class DirectExecutor implements Executor{
		public DirectExecutor() {
		}
		@Override
		public void execute(Runnable command) {
			command.run();
		}
	}
	
    /**
     * 线程处理逻辑
     * @author juebanlin
     */
    class Worker implements Runnable {
    	/**
		 * 查找任务
		 * @return
		 */
		private Runnable findTask()
		{
        	return systemQueue.poll();//执行系统任务
		}
        WorkerWaitCondition workerWaitCondition=new WorkerWaitCondition();
        /**
                  * 等待条件
         * @author juebanlin
         */
        private class WorkerWaitCondition implements WaitCondition<Runnable>
        {
        	private volatile Runnable task;
        	
        	public void init()
        	{
        		task=null;
        	}
        	
        	@Override
			public Runnable getAttach() {
				return task;
			}

			@Override
			public boolean isComplete() {
				if(task==null)
				{
		        	task= findTask();
				}
				return task!=null;
			}
        }
        
        /**
		 * 等待任务
         * @param time 最大超时时间
         * @param unit
         * @return
         * @throws InterruptedException 
         * @throws IOException 
         */
        private Runnable waitTask(long time,TimeUnit unit) throws InterruptedException, IOException {
        	workerWaitCondition.init();
    		return waitConditionStrategy.waitFor(workerWaitCondition, time,unit);	
        }
        
        /**
		 * 线程结束
         */
        void runEnd(){
        	
        }
        
		public void run() {
            long lastRunTaskTime=System.currentTimeMillis();
            try {
                for (;;) 
                {
                	Runnable task=null;
                	try {//等待任务
                		task=waitTask(getKeepAliveTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
					} catch (Exception e) {
					}
                	if(task==null)
                	{//如果没有任务
                		long freeTime=System.currentTimeMillis()-lastRunTaskTime;//空闲时间
                		if(freeTime>getKeepAliveTime(TimeUnit.MILLISECONDS))
                		{//空闲时间达标,如果不是核心线程则退出
                			synchronized (workers) 
                            {
                                if (workers.size() > getCorePoolSize()) 
                                {
                                    workers.remove(this);
                                    break;//退出线程
                                }
                            }
                		}
                		continue;//继续寻找任务
                	}
                	idleWorkers.decrementAndGet();//待命线程-1
					try {
						addWorkerIfNecessary();//预备一个线程,如果有新任务则可立马执行
						lastRunTaskTime=System.currentTimeMillis();
						task.run();
					} finally {
						//不管异常与否都+1,如果有异常导致退出循环,则循环外会待命线程-1
						idleWorkers.incrementAndGet();//待命线程+1
						if(task ==exitTask || task instanceof WorkerExitTask) 
						{//如果是退出任务则退出,不管执行是否异常
							log.debug("退出线程,from WorkerExitTask:"+task);
							break;
						}
					}
                }
            } finally {
                synchronized (workers) {
                    workers.remove(this);
                    workers.notifyAll();
                    idleWorkers.decrementAndGet();//异常或者正常退出都会待命线程-1
                }
                runEnd();
            }
        }
    }
    
    /**
     * 基于事件的系统队列
     * @author juebanlin
     */
    class SystemQueue extends RunnableQueueEventWrapper{
		
		public SystemQueue(Queue<Runnable> queue) {
			super(queue);
		}

		@Override
		protected void onAddBefore() {
			systemTaskOfferBefore(this);
		}

		@Override
		protected void onAddAfter(boolean offeredSucceed) {
			if(offeredSucceed)
			{
				systemTaskOfferAfter(this);
			}
		}
    }
    
    /**
     * 队列添加事件执行之前
     * @param queue
     */
    protected void systemTaskOfferBefore(SystemQueue queue)
    {//如果关机则可以抛出异常
    	
    }
    
    /**
     * 队列添加事件执行之后
     * @param queue
     */
    protected void systemTaskOfferAfter(SystemQueue queue)
    {
    	addWorkerIfNecessary();
	    //如果有线程阻塞等待,则释放阻塞去处理任务
	    waitConditionStrategy.signalAllWhenBlocking();
    }
    
    protected void singallAllWhenBlockingWorker() {
    	waitConditionStrategy.signalAllWhenBlocking();
    }
    
	public long getCompletedTaskCount() {
	    return queueMananger.getToalCompletedTaskCount();
	}
	
	protected void systemExecute(Runnable task)
	{
		if(task !=null)
    	{
			systemQueue.add(task);
    	}
	}
	
	protected void systemExecute(List<Runnable> tasks)
	{
		if(tasks !=null)
    	{
			systemQueue.addAll(tasks);
    	}
	}

	@Override
	public void execute(@NotNull Runnable command) {
		systemExecute(command);
	}

	public QueueGroupManager getQueueGroupManager() {
		return queueMananger;
	}

	@Override
	public void execute(String key, Runnable task) {
		queueMananger.getQueueExecutor(key).execute(task);
	}

	@Override
	public void execute(String key, List<Runnable> tasks) {
		queueMananger.getQueueExecutor(key).execute(tasks);
	}

	@Override
	public boolean hasQueueExecutor(String key) {
		return queueMananger.hasQueueExecutor(key);
	}
	
	@Override
	public QueueExecutor getQueueExecutor(String key) {
		return queueMananger.getQueueExecutor(key);
	}

	@Override
	public Iterator<KeyElement<QueueExecutor>> keyIterator() {
		return queueMananger.keyIterator();
	}	


	public static class Builder{
		int corePoolSize=DEFAULT_INITIAL_THREAD_POOL_SIZE;
		int maximumPoolSize=DEFAULT_MAX_THREAD_POOL;
		long keepAliveTime=DEFAULT_KEEP_ALIVE_SEC;
		TimeUnit unit=TimeUnit.SECONDS;
		ThreadFactory threadFactory=default_ThreadFactory();
        WaitConditionStrategy waitConditionStrategy=default_waitConditionStrategy();
        Queue<Runnable> bossQueue=default_BossQueue();
        QueueGroupManager queueMananger=default_QueueGroupManager();
        boolean nullContextClassLoader;
        Executor assistExecutor=new DirectExecutor();
		
        public Builder setCorePoolSize(int corePoolSize)
        {
        	this.corePoolSize=corePoolSize;
        	return this;
        }
        
        public Builder setMaxPoolSize(int maximumPoolSize)
        {
        	this.maximumPoolSize=maximumPoolSize;
        	return this;
        }
        /**
         * 可选执行器,用于启动工作线程
         * @param assistExecutor optional
         * @return
         */
        public Builder setAssistExecutor(Executor assistExecutor)
        {
        	this.assistExecutor=assistExecutor;
        	return this;
        }
        
        public Builder setKeepAliveTime(long keepAliveTime,TimeUnit unit)
        {
        	this.keepAliveTime=keepAliveTime;
        	this.unit=unit;
        	return this;
        }
        
        public Builder setThreadFactory(ThreadFactory threadFactory)
        {
        	this.threadFactory=threadFactory;
        	return this;
        }
        
        public Builder setWaitConditionStrategy(WaitConditionStrategy waitConditionStrategy)
        {
        	this.waitConditionStrategy=waitConditionStrategy;
        	return this;
        }
        
        public Builder setBossQueue(Queue<Runnable> bossQueue)
        {
        	this.bossQueue=bossQueue;
        	return this;
        }
        
        public Builder setQueueGroupManagerr(QueueGroupManager queueMananger)
        {
        	this.queueMananger=queueMananger;
        	return this;
        }
        
        /**
         *  是否设置工作线程：thread.setContextClassLoader(null);
         * @param nullContextClassLoader
         * @return 
         */
		public Builder setNullContextClassLoader(boolean nullContextClassLoader) {
			this.nullContextClassLoader = nullContextClassLoader;
			return this;
		}

		public DefaultQueueGroupExecutor build()
		{
        	DefaultQueueGroupExecutor qe=new DefaultQueueGroupExecutor(corePoolSize, 
					maximumPoolSize, 
					keepAliveTime, 
					unit, 
					threadFactory, 
					waitConditionStrategy, 
					bossQueue, 
					queueMananger,assistExecutor);
        	qe.setNullContextClassLoader(nullContextClassLoader);
			return qe;
		}
	}
}
