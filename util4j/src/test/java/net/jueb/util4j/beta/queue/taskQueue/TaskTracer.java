package net.jueb.util4j.beta.queue.taskQueue;

/**
 * 任务跟踪
 * @author Administrator
 */
public abstract interface TaskTracer{

	public long getSeq();
	
	public long getAppendTime();

	public long getRunStartTime();

	public long getRunEndTime();

	public QueueTask getTask();
}
