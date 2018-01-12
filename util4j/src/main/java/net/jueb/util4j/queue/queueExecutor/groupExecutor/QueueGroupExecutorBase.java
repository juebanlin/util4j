package net.jueb.util4j.queue.queueExecutor.groupExecutor;

import java.util.Iterator;
import java.util.List;

import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;

/**
 * 任务队列组执行器设计原型
 * @author juebanlin
 */
public interface QueueGroupExecutorBase{
	
	/**
	 * 执行队列任务
	 * @param solt 队列号
	 * @param task 任务
	 */
	public void execute(short index,Runnable task);
	
	/**
	 * 批量执行队列任务
	 * @param solt 队列号
	 * @param tasks 批量任务
	 */
	public void execute(short index,List<Runnable> tasks);
	
	
	/**
	 * 获取任务执行器,此队列的名字等于队列别名
	 * @param queue
	 * @return
	 */
	public QueueExecutor getQueueExecutor(short index);
	
	/**
	 * 迭代执行器
	 */
	Iterator<QueueExecutor> indexIterator();
}
