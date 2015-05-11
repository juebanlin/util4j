package net.jueb.util4j.tools.taskQueue;

import java.util.concurrent.ConcurrentHashMap;
import net.jueb.util4j.tools.taskQueue.OrderTaskQueue.Task;

public class OrderTaskQueueGroup {

	private Object lock=new Object();
	private long TaskRunTimeOutMillis;
	private ConcurrentHashMap<String,OrderTaskQueue> queueMap = new ConcurrentHashMap<String,OrderTaskQueue>();

	public OrderTaskQueueGroup(long TaskRunTimeOutMillis) {
		this.TaskRunTimeOutMillis=TaskRunTimeOutMillis;
	}
	
	public void put(String key,Task task)
	{
		synchronized(lock) {
			OrderTaskQueue queue=queueMap.get(key);
			if(queue==null)
			{
				queue=new OrderTaskQueue(key,TaskRunTimeOutMillis);
				queue.start();
				queueMap.put(key, queue);
			}
			queue.addTask(task);
		}
	}
	
	public void stop()
	{
		synchronized (lock) {
			for(String key:queueMap.keySet())
			{
				OrderTaskQueue queue=queueMap.get(key);
				queue.stop();
			}
		}
	}
}
