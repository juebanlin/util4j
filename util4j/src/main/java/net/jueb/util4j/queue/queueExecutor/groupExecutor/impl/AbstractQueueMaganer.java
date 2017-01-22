package net.jueb.util4j.queue.queueExecutor.groupExecutor.impl;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.queue.RunnableQueueWrapper;

public abstract class AbstractQueueMaganer {

	public AbstractQueueMaganer() {
	}
	
	public AbstractQueueMaganer(QueueFactory queueFactory) {
		this.queueFactory = queueFactory;
	}

	/**
	 * 获取可用队列工厂
	 * @return
	 */
	protected final QueueFactory getQueueFactory_()
	{
		QueueFactory queueFactory=getQueueFactory();
		if(queueFactory==null)
		{
			queueFactory=getDefaultQueueFactory();
		}
		return queueFactory;
	}
	
	public QueueFactory getQueueFactory() {
		return queueFactory;
	}
	
	public void setQueueFactory(QueueFactory queueFactory)
	{
		this.queueFactory=queueFactory;
	}
	
	public static final QueueFactory DefaultQueueFactory=()->{return new RunnableQueueWrapper(new ConcurrentLinkedQueue<>());};
	
	public QueueFactory queueFactory;

	/**
	 * 获取默认队列工厂
	 * @return
	 */
	public final QueueFactory getDefaultQueueFactory() {
		return DefaultQueueFactory;
	}
}
