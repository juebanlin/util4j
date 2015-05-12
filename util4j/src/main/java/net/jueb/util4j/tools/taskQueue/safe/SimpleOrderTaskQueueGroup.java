package net.jueb.util4j.tools.taskQueue.safe;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import net.jueb.util4j.tools.taskQueue.safe.SimpleOrderTaskQueue.Task;

public class SimpleOrderTaskQueueGroup {

	ReentrantLock lock=new ReentrantLock();
	protected ConcurrentHashMap<String,SimpleOrderTaskQueue> queueMap = new ConcurrentHashMap<String,SimpleOrderTaskQueue>();

	public void put(String key,Task task)
	{
		if(queueMap.get(key)==null)
		{
			try {
				lock.lock();
				if(queueMap.get(key)==null)
				{
					SimpleOrderTaskQueue queue= new SimpleOrderTaskQueue(key);
					queue.start();
					queueMap.put(key,queue);
				}
			} catch (Exception e) {
			}finally{
				lock.unlock();
			}
		}
		SimpleOrderTaskQueue queue=queueMap.get(key);
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
				SimpleOrderTaskQueue queue=queueMap.get(key);
				queue.stop();
			}
		} catch (Exception e) {
		}finally
		{
			lock.unlock();
		}
	}
}
