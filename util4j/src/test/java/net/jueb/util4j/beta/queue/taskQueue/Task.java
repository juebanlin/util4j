package net.jueb.util4j.beta.queue.taskQueue;

/**
 * 任务
 * @author juebanlin
 */
public interface Task extends Runnable{
	
	/**
	 * 任务名称
	 * @return
	 */
	public String name();
}
