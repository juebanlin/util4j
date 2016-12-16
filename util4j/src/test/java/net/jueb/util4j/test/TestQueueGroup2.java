package net.jueb.util4j.test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jctools.queues.MpscLinkedQueue;
import org.jctools.queues.atomic.MpmcAtomicArrayQueue;

import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.RunnableQueue;
import net.jueb.util4j.queue.queueExecutor.RunnableQueueWrapper;
import net.jueb.util4j.queue.queueExecutor.queueGroup.IndexQueueGroupManager;
import net.jueb.util4j.queue.queueExecutor.queueGroup.KeyQueueGroupManager;
import net.jueb.util4j.queue.queueExecutor.queueGroup.QueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.queueGroup.impl.ArrayIndexQueueManager;
import net.jueb.util4j.queue.queueExecutor.queueGroup.impl.DefaultQueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.queueGroup.impl.StringQueueManager;

public class TestQueueGroup2 {

	public static void main(String[] args) {
	}
	
	protected QueueGroupExecutor buildByJdk()
	{
		//多生产多消费者队列(线程竞争队列)
		Queue<Runnable> bossQueue=new ConcurrentLinkedQueue<>();
		QueueFactory qf=new QueueFactory() {
			@Override
			public RunnableQueue buildQueue() {
				Queue<Runnable> queue=new ConcurrentLinkedQueue<>();
				return new RunnableQueueWrapper(queue);
			}
		};
		IndexQueueGroupManager iqm=new ArrayIndexQueueManager(qf);
		KeyQueueGroupManager kqm=new StringQueueManager(qf);
		return new DefaultQueueGroupExecutor(2, 8,bossQueue, iqm, kqm);
	}
	
	protected QueueGroupExecutor buildByMpMc()
	{
		int maxQueueCount=65536;
		//多生产多消费者队列(线程竞争队列)
		Queue<Runnable> bossQueue=new MpmcAtomicArrayQueue<>(maxQueueCount);
		QueueFactory qf=new QueueFactory() {
			@Override
			public RunnableQueue buildQueue() {
				//多生产单消费者队列(PS:bossQueue决定了一个队列只能同时被一个线程处理)
				Queue<Runnable> queue=MpscLinkedQueue.newMpscLinkedQueue();
				return new RunnableQueueWrapper(queue);
			}
		};
		IndexQueueGroupManager iqm=new ArrayIndexQueueManager(qf);
		KeyQueueGroupManager kqm=new StringQueueManager(qf);
		return new DefaultQueueGroupExecutor(2, 8,bossQueue, iqm, kqm);
	}
}
