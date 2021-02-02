package net.jueb.util4j.queue.queueExecutor.executor;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 任务队列执行器
 * @author juebanlin
 */
public interface QueueExecutor extends Executor{

	String getName();

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

	/**
	 * 被线程处理的次数
	 * @return
	 */
	default long handleCount(){
		return 0;
	}

	/**
	 * 单次被处理的最大任务数量
	 * @return
	 */
	default long maxProcessCount(){
		return 0;
	}

	void setAlias(String alias);
	
	String getAlias();
	
	void addTag(String tag);
	
	void removeTag(String tag);
	
	boolean hasTag(String tag);
	
	Set<String> getTags();
	
	boolean hasAttribute(String key);

	<T> void setAttribute(String key, T value);

	<T> T getAttribute(String key);

	<T> T removeAttribute(String key);

	void clearAttributes();
}
