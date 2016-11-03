package net.jueb.util4j.queue.queueExecutor.queueGroup.impl.adapter;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import net.jueb.util4j.queue.queueExecutor.queue.QueueExecutor;
import net.jueb.util4j.queue.queueExecutor.queueGroup.IndexQueueGroupManager;
import net.jueb.util4j.queue.queueExecutor.queueGroup.KeyQueueGroupManager;
import net.jueb.util4j.queue.queueExecutor.queueGroup.QueueGroupExecutorService;
import net.jueb.util4j.queue.queueExecutor.queueGroup.IndexQueueGroupManager.IndexGroupEventListener;
import net.jueb.util4j.queue.queueExecutor.queueGroup.KeyQueueGroupManager.KeyGroupEventListener;

public class ScheduledThreadPoolQueueGroupExecutor extends ScheduledThreadPoolExecutor implements QueueGroupExecutorService{

    private final IndexQueueGroupManager iqm;
    private final KeyQueueGroupManager kqm;
    
    protected void init()
    {
    	this.iqm.setGroupEventListener(new IndexGroupEventListener() {
			@Override
			public void onQueueHandleTask(short index, Runnable handleTask) {
				//当sqm有可以处理某队列的任务产生时,丢到系统队列,当系统队列
				execute(handleTask);
			}
		});
    	this.kqm.setGroupEventListener(new KeyGroupEventListener() {
			@Override
			public void onQueueHandleTask(String key, Runnable handleTask) {
				//当sqm有可以处理某队列的任务产生时,丢到系统队列,当系统队列
				execute(handleTask);
			}
		});
    }
	
	public ScheduledThreadPoolQueueGroupExecutor(int corePoolSize, ThreadFactory threadFactory,RejectedExecutionHandler handler,
			IndexQueueGroupManager iqm,KeyQueueGroupManager kqm) {
		super(corePoolSize, threadFactory, handler);
		if (iqm==null || kqm==null)
		{
			throw new IllegalArgumentException();
		}
		this.iqm=iqm;
		this.kqm=kqm;
		init();
	}

	@Override
	public void execute(short index, Runnable task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(short index, List<Runnable> tasks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAlias(short index, String alias) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAlias(short index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueueExecutor getQueueExecutor(short index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<QueueExecutor> indexIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(String key, Runnable task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(String key, List<Runnable> tasks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAlias(String key, String alias) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAlias(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueueExecutor getQueueExecutor(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<QueueExecutor> keyIterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
