package net.jueb.util4j.queue.taskQueue;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * 任务队列组执行器
 * @author juebanlin
 */
public interface TaskQueuesExecutor extends Executor{
	
	/**
	 * 执行队列任务
	 * 如果队列不存在则创建
	 * @param queue
	 * @param task
	 */
	public TaskQueueExecutor execute(String queueName,Task task);
	
	/**
	 * 批量执行队列任务
	 * 如果队列不存在则创建
	 * @param queue
	 * @param tasks
	 */
	public TaskQueueExecutor execute(String queueName,List<Task> tasks);
	
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
