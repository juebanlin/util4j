package net.jueb.util4j.queue.queueExecutor;

import java.util.Queue;

public interface QueueFactory {

	public RunnableQueue buildQueue(); 
	
	public RunnableQueue buildQueue(Queue<Runnable> queue); 
}
