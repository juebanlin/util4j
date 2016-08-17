package net.jueb.util4j.beta.queue.taskQueue.impl;

import net.jueb.util4j.beta.queue.taskQueue.QueueTaskContext;

class DefaultQueueTaskContext implements QueueTaskContext{

	private final DefaultQueueTaskExecutor executor;
	
	protected DefaultQueueTaskContext(DefaultQueueTaskExecutor executor) {
		super();
		this.executor = executor;
	}

	@Override
	public DefaultTaskTracer lastRunTask() {
		return executor.lastRunTask();
	}

	@Override
	public DefaultTaskTracer nextTask() {
		return executor.nextTask();
	}
}
