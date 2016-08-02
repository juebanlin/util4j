package net.jueb.util4j.queue.taskQueue;

public interface QueueTaskContext{

	/**
	 * 获取上一个执行的任务
	 * @return
	 */
	public abstract TaskTracer lastRunTask();
	
	/**
	 * 下一个待执行的任务
	 * @return
	 */
	public abstract TaskTracer nextTask();
}
