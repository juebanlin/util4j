package net.jueb.util4j.beta.queue.taskQueue;

import java.util.concurrent.Executor;

/**
 * 任务队列执行器
 * @author juebanlin
 */
public interface TaskQueueExecutor extends Executor{
	
	/**
	 * 获取队列名称
	 * @return
	 */
	public String getQueueName();
	
	/**
	 * 执行任务
	 * @param task
	 */
	public void execute(Task task);
}
