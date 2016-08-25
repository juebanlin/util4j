package net.jueb.util4j.beta.queue.taskQueue.impl;

import net.jueb.util4j.beta.queue.taskQueue.Task;

class RunnableTaskAdapter implements Task{

	private final Runnable runnable;
	
	public RunnableTaskAdapter(Runnable runnable) {
		this.runnable=runnable;
	}
	
	@Override
	public void run() {
		runnable.run();
	}

	public Runnable getRunnable() {
		return runnable;
	}

	@Override
	public String name() {
		return runnable.toString();
	}
}
