package net.jueb.util4j.tools.taskQueue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import net.jueb.util4j.tools.taskQueue.OrderTaskQueue.Task;

public class OrderTaskQueueGroup {

	ReentrantLock lock=new ReentrantLock();
	protected ConcurrentHashMap<String,OrderTaskQueue> queueMap = new ConcurrentHashMap<String,OrderTaskQueue>();

	public void put(String key,Task task)
	{
		if(queueMap.get(key)==null)
		{
			try {
				lock.lock();
				if(queueMap.get(key)==null)
				{
					OrderTaskQueue queue= new OrderTaskQueue(key);
					queue.start();
					queueMap.put(key,queue);
				}
			} catch (Exception e) {
			}finally{
				lock.unlock();
			}
		}
		OrderTaskQueue queue=queueMap.get(key);
		if(queue!=null)
		{
			queue.addTask(task);
		}
	}
	
	public void stop()
	{
		try {
			lock.lock();
			for(String key:queueMap.keySet())
			{
				OrderTaskQueue queue=queueMap.get(key);
				queue.stop();
			}
		} catch (Exception e) {
		}finally
		{
			lock.unlock();
		}
	}
}
