package net.jueb.util4j.beta.queue.taskQueue;

import java.util.Set;

/**
 * 顺序任务
 * @author Administrator
 */
public interface QueueTask{

	/**
	 * 任务名称
	 * @return
	 */
	public String name();
	
	/**
	 * 任务标签
	 * @return
	 */
	public Set<String> getTags();
	
	/**
	 * 运行
	 */
	public void run(QueueTaskContext context);
	
}