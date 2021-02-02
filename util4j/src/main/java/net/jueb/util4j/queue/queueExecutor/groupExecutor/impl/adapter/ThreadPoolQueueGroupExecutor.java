package net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.adapter;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutorService;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupManager.KeyGroupEventListener;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultQueueManager;

public class ThreadPoolQueueGroupExecutor extends ThreadPoolExecutor implements QueueGroupExecutorService{
    
	public static final int DEFAULT_KEEP_ALIVE = 30;
    
	private final QueueGroupManager kqm;
	
	protected void init()
    {
    	this.kqm.setGroupEventListener((key,task)->{
			//当sqm有可以处理某队列的任务产生时,丢到系统队列,当系统队列
			execute(task);
		});
    }

	public ThreadPoolQueueGroupExecutor(){
		this(1,Runtime.getRuntime().availableProcessors(),new LinkedBlockingQueue<>(),new DefaultQueueManager());
	}

    public ThreadPoolQueueGroupExecutor(int corePoolSize, int maximumPoolSize,BlockingQueue<Runnable> workQueue,
    		QueueGroupManager kqm) {
    	super(corePoolSize, maximumPoolSize, DEFAULT_KEEP_ALIVE, TimeUnit.SECONDS,workQueue);
    	if (kqm==null)
		{
			throw new IllegalArgumentException();
		}
		this.kqm=kqm;
		init();
    }

	public ThreadPoolQueueGroupExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler,
			QueueGroupManager kqm) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		if (kqm==null)
		{
			throw new IllegalArgumentException();
		}
		this.kqm=kqm;
		init();
	}

	@Override
	public QueueGroupManager getQueueGroupManager() {
		return kqm;
	}

	@Override
	public void execute(String key, Runnable task) {
		kqm.getQueueExecutor(key).execute(task);
	}

	@Override
	public void execute(String key, List<Runnable> tasks) {
		kqm.getQueueExecutor(key).execute(tasks);
	}

	@Override
	public boolean hasQueueExecutor(String key) {
		return kqm.hasQueueExecutor(key);
	}
	
	@Override
	public QueueExecutor getQueueExecutor(String key) {
		return kqm.getQueueExecutor(key);
	}

	@Override
	public Iterator<KeyElement<QueueExecutor>> keyIterator() {
		return kqm.keyIterator();
	}
}
