package net.jueb.util4j.beta.queue.taskQueue;

import java.util.concurrent.Executor;

/**
 * 任务队列执行器
 * @author juebanlin
 */
public interface TaskQueueGroupExecutor extends Executor{
	
	/**
	 * 执行队列任务
	 * 如果队列不存在则创建
	 * @param queue
	 * @param task
	 */
	public TaskQueueExecutor execute(String queueName,Task task);
	
	/**
	 * 获取任务队列
	 * @param queue
	 * @return
	 */
	public TaskQueueExecutor getQueue(String queueName);
	
	/**
	 * 获取任务队列,如果不存在则创建
	 * @param queue
	 * @return
	 */
	public TaskQueueExecutor getQueueOrCreate(String queueName);
}
