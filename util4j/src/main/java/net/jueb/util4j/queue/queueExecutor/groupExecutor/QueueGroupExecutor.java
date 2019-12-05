package net.jueb.util4j.queue.queueExecutor.groupExecutor;

import java.util.Iterator;
import java.util.List;

import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;

/**
 * 任务队列组执行器
 * @author juebanlin
 */
public interface QueueGroupExecutor{

	QueueGroupManager getQueueGroupManager();

	interface KeyElement<T>{
		String getKey();
		T getValue();
	}
	
	void execute(String key,Runnable task);
	
	void execute(String key,List<Runnable> tasks);
	
	boolean hasQueueExecutor(String key);
	
	QueueExecutor getQueueExecutor(String key);
	
	Iterator<KeyElement<QueueExecutor>> keyIterator();
}
