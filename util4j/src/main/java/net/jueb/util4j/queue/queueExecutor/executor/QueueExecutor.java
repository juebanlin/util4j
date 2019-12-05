package net.jueb.util4j.queue.queueExecutor.executor;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 任务队列执行器
 * @author juebanlin
 */
public interface QueueExecutor extends Executor{
	
	/**
	 * 执行任务
	 * @param task
	 */
	void execute(Runnable task);
	
	/**
	 * 批量执行任务
	 * @param tasks
	 */
	void execute(List<Runnable> tasks);
	
	/**
	 * 队列大小
	 * @return
	 */
	int size();
	
	void setAlias(String alias);
	
	String getAlias();
	
	void addTag(String tag);
	
	void removeTag(String tag);
	
	boolean hasTag(String tag);
	
	Set<String> getTags();
	
	boolean hasAttribute(String key);

	void setAttribute(String key, Object value);

	Object getAttribute(String key);

	Object removeAttribute(String key);

	void clearAttributes();
}
