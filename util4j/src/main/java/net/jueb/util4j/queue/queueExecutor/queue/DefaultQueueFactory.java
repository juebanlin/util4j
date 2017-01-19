package net.jueb.util4j.queue.queueExecutor.queue;

import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.RunnableQueue;

public class DefaultQueueFactory implements QueueFactory
{
	@Override
	public RunnableQueue buildQueue() {
		return new DefaultRunnableQueue();
	}
}
