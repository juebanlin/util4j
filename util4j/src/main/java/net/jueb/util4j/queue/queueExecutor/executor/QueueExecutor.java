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
	public void execute(Runnable task);
	
	/**
	 * 批量执行任务
	 * @param tasks
	 */
	public void execute(List<Runnable> tasks);
	
	/**
	 * 队列大小
	 * @return
	 */
	public int size();
	
	public void setAlias(String alias);
	
	public String getAlias();
	
	public void addTag(String tag);
	
	public void removeTag(String tag);
	
	public boolean hasTag(String tag);
	
	public Set<String> getTags();
	
	public boolean hasAttribute(String key);

	public void setAttribute(String key, Object value);

	public Object getAttribute(String key);

	public Object removeAttribute(String key);

	public void clearAttributes();
}
