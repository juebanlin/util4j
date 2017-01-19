package net.jueb.util4j.queue.queueExecutor.groupExecutor.impl;

import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.queue.DefaultQueueFactory;

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
	
	private static final QueueFactory DefaultQueueFactory=new DefaultQueueFactory();
	
	public QueueFactory queueFactory;

	/**
	 * 获取默认队列工厂
	 * @return
	 */
	public final QueueFactory getDefaultQueueFactory() {
		return DefaultQueueFactory;
	}
}
