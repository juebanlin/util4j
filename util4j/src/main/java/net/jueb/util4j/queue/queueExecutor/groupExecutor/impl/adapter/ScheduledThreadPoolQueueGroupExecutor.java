package net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.adapter;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutorService;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupManager.KeyGroupEventListener;

public class ScheduledThreadPoolQueueGroupExecutor extends ScheduledThreadPoolExecutor implements QueueGroupExecutorService{

    private final QueueGroupManager kqm;
    
    protected void init()
    {
    	this.kqm.setGroupEventListener(new KeyGroupEventListener() {
			@Override
			public void onQueueHandleTask(String key, Runnable handleTask) {
				//当sqm有可以处理某队列的任务产生时,丢到系统队列,当系统队列
				execute(handleTask);
			}
		});
    }
	
	public ScheduledThreadPoolQueueGroupExecutor(int corePoolSize, ThreadFactory threadFactory,RejectedExecutionHandler handler,
			QueueGroupManager kqm) {
		super(corePoolSize, threadFactory, handler);
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

