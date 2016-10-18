package net.jueb.util4j.queue.queueExecutor.queueGroup.impl;

import java.util.List;

import net.jueb.util4j.queue.queueExecutor.queue.QueueExecutor;

/**
 * 具有调度器功能的队列
 * @author juebanlin
 */
public class RunnableQueueExecutor extends AbstractRunnableQueue implements QueueExecutor {

	private static final long serialVersionUID = -5250005650070366676L;

	private final String name;

	public RunnableQueueExecutor(String name) {
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

	@Override
	protected void event_taskOfferBefore() {
		
	}

	@Override
	protected void event_taskOfferAfter(boolean offeredSucceed) {
		
	}
}