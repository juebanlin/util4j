package net.jueb.util4j.queue.queueExecutor.queue.impl;

import java.util.List;
import java.util.Queue;

import net.jueb.util4j.queue.queueExecutor.RunnableQueueEventWrapper;
import net.jueb.util4j.queue.queueExecutor.queue.QueueExecutor;

/**
 * 具有事件和调度器功能的队列
 * @author juebanlin
 */
public abstract class RunnableQueueExecutorEventWrapper extends RunnableQueueEventWrapper implements QueueExecutor {

	private final String name;

	public RunnableQueueExecutorEventWrapper(Queue<Runnable> queue,String name) {
		super(queue);
		this.name=name;
	}

	public String getQueueName() {
		return name;
	}

	@Override
	public final void execute(Runnable task) {
		offer(task);
	}

	@Override
	public final void execute(List<Runnable> tasks) {
		addAll(tasks);
	}
}