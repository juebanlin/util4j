package net.jueb.util4j.queue.queueExecutor;

import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;

public interface QueueContext {

	/**
	 * 获取当前队列
	 * @return
	 */
	QueueExecutor getExecutor();
	
	/**
	 * 上一个任务
	 * @return
	 */
	Runnable last();
	
	/**
	 * 下一个任务
	 * @return
	 */
	Runnable next();
}
