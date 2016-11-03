package net.jueb.util4j.queue.queueExecutor.queue.impl;

import net.jueb.util4j.queue.queueExecutor.DefaultExecuteQueue;
import net.jueb.util4j.queue.queueExecutor.RunnableQueue;
import net.jueb.util4j.queue.queueExecutor.queue.QueueExecutor;

public abstract class AbstractQueueExecutorBase extends AbstractQueueExecutor implements QueueExecutor{

	private final RunnableQueue queue;
	private final String queueName;
	
	public AbstractQueueExecutorBase(String queueName) {
		this.queue=new DefaultExecuteQueue();
		this.queueName=queueName;
	}
	
	public AbstractQueueExecutorBase(RunnableQueue queue,String queueName) {
		if (queue == null || queueName==null)
            throw new NullPointerException();
		this.queue=new DefaultExecuteQueue();
		this.queueName=queueName;
	}
	
	@Override
	public final void execute(Runnable command) {
		if (command == null)
            throw new NullPointerException();
		queue.offer(command);
	}

	@Override
	public final String getQueueName() {
		return queueName;
	}

	public final RunnableQueue getQueue() {
		return queue;
	}
}
