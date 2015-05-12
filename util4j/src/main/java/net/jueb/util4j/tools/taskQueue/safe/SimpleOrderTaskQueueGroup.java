package net.jueb.util4j.tools.taskQueue.safe;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import net.jueb.util4j.tools.taskQueue.safe.SimpleOrderTaskQueue.Task;

public class SimpleOrderTaskQueueGroup {

	ReentrantLock lock=new ReentrantLock();
	private ConcurrentHashMap<String,SimpleOrderTaskQueue> queueMap = new ConcurrentHashMap<String,SimpleOrderTaskQueue>();

	public void put(String key,Task task)
	{
		try {
			lock.lock();
			SimpleOrderTaskQueue queue=queueMap.get(key);
			if(queue==null)
			{
				queue=new SimpleOrderTaskQueue(key);
				queue.start();
				queueMap.put(key, queue);
			}
			queue.addTask(task);
		} catch (Exception e) {
		}finally
		{
			lock.unlock();
		}
	}
	
	public void stop()
	{
		try {
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
