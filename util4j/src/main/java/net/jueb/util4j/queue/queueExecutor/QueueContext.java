package net.jueb.util4j.queue.queueExecutor;

import net.jueb.util4j.queue.queueExecutor.queue.QueueExecutor;

public interface QueueContext {

	/**
	 * 获取当前队列
	 * @return
	 */
	public QueueExecutor getExecutor();
	
	/**
	 * 上一个任务
	 * @return
	 */
	public Runnable last();
	
	/**
	 * 下一个任务
	 * @return
	 */
	public Runnable next();
	
	public boolean hasAttribute(String key);

	public void setAttribute(String key, Object value);

	public Object getAttribute(String key);

	public Object removeAttribute(String key);

	public void clearAttributes();
}
