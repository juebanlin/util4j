package net.jueb.util4j.queue.queueExecutor.groupExecutor;

import java.util.Iterator;
import java.util.List;

import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;

/**
 * 任务队列组执行器
 * @author juebanlin
 */
public interface QueueGroupExecutor{
	
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
