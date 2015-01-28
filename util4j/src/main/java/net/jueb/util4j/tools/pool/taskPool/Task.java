package net.jueb.util4j.tools.pool.taskPool;

public interface Task extends Runnable {
	/**
	 * 创建该任务的管理员的名字
	 * @return
	 */
	public String getAdminName();
	/**
	 * 自定义任务名字
	 * @return
	 */
	public String getTaskName();
	
	/**
	 * 自定义任务ID
	 * @return
	 */
	public String getTaskID();
	
	/**
	 * 设置任务优先级,必须大于0
	 * @return
	 */
	public int getProperty();
}
