package net.jueb.util4j.tools.taskQueue.lite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务对象
 * 
 * @author Administrator
 */
 class TaskObj {
	private Logger log = LoggerFactory.getLogger(getClass());
	private long startTime=System.currentTimeMillis();// 开始时间
	private long endTime=0;// 结束时间
	private Task task;

	public TaskObj(Task task) {
		this.task = task;
	}

	public void start() {
		try {
			task.action();
		} catch (Exception e) {
			log.debug("task error[" + task.getClass() + "]:" + e.getMessage());
		}
		endTime = System.currentTimeMillis();
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}
}