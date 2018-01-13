package net.jueb.util4j.queue.queueExecutor.groupExecutor;

import java.util.Iterator;
import java.util.List;

import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;

/**
 * 任务队列组执行器
 * @author juebanlin
 */
public interface QueueGroupExecutor extends QueueGroupExecutorBase{
	
	/**
	 * 执行队列任务
	 * @param index 队列号
	 * @param task 任务
	 */
	public void execute(short index,Runnable task);
	
	/**
	 * 批量执行队列任务
	 * @param index 队列号
	 * @param tasks 批量任务
	 */
	public void execute(short index,List<Runnable> tasks);
	
	/**
	 * 是否存在此队列执行器
	 * @param index
	 * @return
	 */
	public boolean hasQueueExecutor(short index);
	
	/**
	 * 获取任务执行器,此队列的名字等于队列别名
	 * 没有则创建
	 * @param queue
	 * @return
	 */
	public QueueExecutor getQueueExecutor(short index);
	
	/**
	 * 迭代执行器
	 */
	Iterator<IndexElement<QueueExecutor>> indexIterator();
	
	
	public static interface KeyElement<T>{
		public String getKey();
		public T getValue();
	}
	
	public void execute(String key,Runnable task);
	public void execute(String key,List<Runnable> tasks);
	public boolean hasQueueExecutor(String key);
	public QueueExecutor getQueueExecutor(String key);
	Iterator<KeyElement<QueueExecutor>> keyIterator();
}
