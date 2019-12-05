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
	
	boolean hasQueueExecutor(String key);
	
	/**
	 * 获取任务执行器,此队列的名字等于队列别名
	 * @param
	 * @return
	 */
	QueueExecutor getQueueExecutor(String key);

	/**
	 * 迭代执行器
	 */
	@Override
	Iterator<QueueExecutor> iterator();
	
	Iterator<KeyElement<QueueExecutor>> keyIterator();
	
	long getToalCompletedTaskCount();
	
	long getToalCompletedTaskCount(String key);
	
	void setGroupEventListener(KeyGroupEventListener listener);
	
	QueueFactory getQueueFactory();
	
	@FunctionalInterface
	interface KeyGroupEventListener{
		/**
		 * 某队列的处理任务
		 * @param key
		 * @param handleTask
		 */
		void onQueueHandleTask(String key,Runnable handleTask);
	}
}
