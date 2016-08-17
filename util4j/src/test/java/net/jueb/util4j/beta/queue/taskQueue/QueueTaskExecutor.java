package net.jueb.util4j.beta.queue.taskQueue;

import java.util.List;
import java.util.Queue;

/**
 * 任务队列规范接口
 * @author Administrator
 */
public interface QueueTaskExecutor {

	/**
	 * 获取队列名称
	 * @return
	 */
	public String getName();
	
	/**
	 * 启动队列
	 */
	public void start();
	
	/**
	 * 停止队列,返回未执行的队列
	 */
	public Queue<TaskTracer> stop(boolean force);
	
	/**
	 * 追加任务
	 * @param task
	 */
	public boolean appendTask(QueueTask task);
	
	/**
	 * 批量追加任务
	 * @param tasks
	 */
	public boolean appendTask(List<QueueTask> tasks);
	
	/**
	 * 队列是否活动
	 * @return
	 */
	public boolean isActive();
	
	/**
	 * 获取正在执行的任务
	 * @return
	 */
	public TaskTracer getCurrentTask();
	
	/**
	 * 获取队列任务数量
	 * @return
	 */
	public int getOrderCount();
	
	/**
	 * 获取执行序列
	 * @return
	 */
	public long getRunSeq();
}
