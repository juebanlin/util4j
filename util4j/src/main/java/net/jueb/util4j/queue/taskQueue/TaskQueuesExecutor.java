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
	public void execute(String queueName,Task task);
	
	/**
	 * 批量执行队列任务
	 * 如果队列不存在则创建
	 * @param queue
	 * @param tasks
	 */
	public void execute(String queueName,List<Task> tasks);
	
	/**
	 * 打开一个队列
	 * 返回一个队列执行器
	 */
	public TaskQueueExecutor openQueue(String queueName);
	
	/**
	 * 关闭队列
	 * @param queueName
	 * @return
	 */
	public TaskQueue closeQueue(String queueName);
	
	/**
	 * 获取任务执行器
	 * @param queue
	 * @return
	 */
	public TaskQueueExecutor getQueueExecutor(String queueName);
}
