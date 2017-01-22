package net.jueb.util4j.queue.queueExecutor;

@FunctionalInterface
public interface QueueFactory {

	public RunnableQueue buildQueue(); 
}
