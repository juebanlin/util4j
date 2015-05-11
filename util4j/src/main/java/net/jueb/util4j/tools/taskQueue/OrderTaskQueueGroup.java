package net.jueb.util4j.tools.taskQueue;

import java.util.concurrent.ConcurrentHashMap;
import net.jueb.util4j.tools.taskQueue.OrderTaskQueue.Task;

public class OrderTaskQueueGroup {

	private Object lock=new Object();
	private long TaskRunTimeOutMillis;
	private ConcurrentHashMap<String,OrderTaskQueue> map = new ConcurrentHashMap<String,OrderTaskQueue>();

	public OrderTaskQueueGroup(long TaskRunTimeOutMillis) {
		this.TaskRunTimeOutMillis=TaskRunTimeOutMillis;
	}
	
	public void put(String key,Task task)
	{
		synchronized(lock) {
			OrderTaskQueue queue=map.get(key);
			if(queue==null)
			{
				queue=new OrderTaskQueue(key,TaskRunTimeOutMillis);
			}
			queue.addTask(task);
		}
	}
}
