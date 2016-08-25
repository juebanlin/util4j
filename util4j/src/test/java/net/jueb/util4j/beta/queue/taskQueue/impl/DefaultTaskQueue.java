package net.jueb.util4j.beta.queue.taskQueue.impl;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.jueb.util4j.beta.queue.taskQueue.Task;
import net.jueb.util4j.beta.queue.taskQueue.TaskQueue;

public class DefaultTaskQueue extends ConcurrentLinkedQueue<Task> implements TaskQueue{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6158476112928562757L;

	private final String name;
	
	public static enum QueueState{
		/**
		 * 处理中
		 */
		processing,
		/**
		 * 就绪中
		 */
		ready
	}
	protected volatile QueueState state;
	
	public DefaultTaskQueue(String name) {
		if (name == null)
            throw new NullPointerException();
		this.name=name;
	}
	
	@Override
	public final String getQueueName() {
		return name;
	}

	public final QueueState getState() {
		return state;
	}
	
	protected void setState(QueueState state)
	{
		if (state == null)
            throw new NullPointerException();
		this.state=state;
	}
}
