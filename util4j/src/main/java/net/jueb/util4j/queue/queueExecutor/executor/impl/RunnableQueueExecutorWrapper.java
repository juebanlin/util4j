package net.jueb.util4j.queue.queueExecutor.executor.impl;

import java.util.List;
import java.util.Queue;

import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;
import net.jueb.util4j.queue.queueExecutor.queue.RunnableQueueWrapper;

/**
 * 具有调度器功能的队列
 * @author juebanlin
 */
public class RunnableQueueExecutorWrapper extends RunnableQueueWrapper implements QueueExecutor {

	private final String name;

	public RunnableQueueExecutorWrapper(Queue<Runnable> queue,String name) {
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