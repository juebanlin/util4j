package net.jueb.util4j.queue.order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.queue.order.OrderTaskQueue.Task;

public class OrderTaskQueueGroup {
	public final Logger log = LoggerFactory.getLogger(getClass());
	private final ReentrantLock lock=new ReentrantLock();
	protected final Map<String,OrderTaskQueue> queueMap = new ConcurrentHashMap<String,OrderTaskQueue>();

	public OrderTaskQueue put(String key,Task task)
	{
		OrderTaskQueue queue=queueMap.get(key);
		if(queue==null || !queue.isActive())
		{
			try {
				lock.lock();
				queue=queueMap.get(key);
				if(queue==null)
				{//对null的处理
					queue= new OrderTaskQueue(key);
					queueMap.put(key,queue);
					queue.start();
				}else
				{//对离线的处理
					if(!queue.isActive())
					{
						log.warn("线程队列:"+key+",离线,未处理任务数:"+queue.getTasks().size()+",重建线程队列线程……");
						queue.stop();
						queue= new OrderTaskQueue(key,queue.getTasks());
						queueMap.put(key,queue);
						queue.start();
						log.warn("线程队列:"+key+"重建:"+queue.isActive()+",未处理任务数:"+queue.getTasks().size()+"");
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}finally{
				lock.unlock();
			}
		}
		queue.addTask(task);
		return queue;
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
			log.error(e.getMessage(),e);
		}finally
		{
			lock.unlock();
		}
	}
}
