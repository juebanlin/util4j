package net.jueb.util4j.queue.queueExecutor.groupExecutor;

import java.util.Iterator;

import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutor.KeyElement;

/**
 * 队列组
 * @author juebanlin
 */
public interface QueueGroupManager extends Iterable<QueueExecutor>{
	
	boolean hasQueueExecutor(String name);
	
	/**
	 * 获取任务执行器,此队列的名字等于队列别名
	 * @param
	 * @return
	 */
	QueueExecutor getQueueExecutor(String name);

	/**
	 * 迭代执行器
	 */
	@Override
	Iterator<QueueExecutor> iterator();
	
	Iterator<KeyElement<QueueExecutor>> keyIterator();

	/**
	 * 获取累计完成任务数量
	 * @return
	 */
	long getToalCompletedTaskCount();

	/**
	 * 获取指定队列累计完成队列数量
	 * @param name
	 * @return
	 */
	long getToalCompletedTaskCount(String name);
	
	void setGroupEventListener(KeyGroupEventListener listener);
	
	QueueFactory getQueueFactory();
	
	@FunctionalInterface
	interface KeyGroupEventListener{
		/**
		 * 某队列的处理任务
		 * @param name
		 * @param queueProcessTask
		 */
		void onQueueHandleTask(String name,Runnable queueProcessTask);
	}
}
