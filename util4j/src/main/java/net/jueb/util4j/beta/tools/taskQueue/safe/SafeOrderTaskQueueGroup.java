package net.jueb.util4j.beta.tools.taskQueue.safe;

import java.util.concurrent.ConcurrentHashMap;

import net.jueb.util4j.beta.tools.taskQueue.safe.SafeOrderTaskQueue.Task;

public class SafeOrderTaskQueueGroup {

	private Object lock=new Object();
	private long TaskRunTimeOutMillis;
	private ConcurrentHashMap<String,SafeOrderTaskQueue> queueMap = new ConcurrentHashMap<String,SafeOrderTaskQueue>();

	public SafeOrderTaskQueueGroup(long TaskRunTimeOutMillis) {
		this.TaskRunTimeOutMillis=TaskRunTimeOutMillis;
	}
	
	public void put(String key,Task task)
	{
		synchronized(lock) {
			SafeOrderTaskQueue queue=queueMap.get(key);
			if(queue==null)
			{
				queue=new SafeOrderTaskQueue(key,TaskRunTimeOutMillis);
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
				SafeOrderTaskQueue queue=queueMap.get(key);
				queue.stop();
			}
		}
	}
}
