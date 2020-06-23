package net.jueb.util4j.test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

import org.jctools.queues.MpscArrayQueue;
import org.jctools.queues.MpscLinkedQueue;
import org.jctools.queues.atomic.MpmcAtomicArrayQueue;

import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.RunnableQueue;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.NioQueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultQueueManager;
import net.jueb.util4j.queue.queueExecutor.queue.RunnableQueueWrapper;

public class TestQueueGroup2 {

	public static void main(String[] args) {
	}
	
	protected QueueGroupExecutor buildByJdk(int min,int max)
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
		QueueGroupManager kqm=new DefaultQueueManager(qf);
		NioQueueGroupExecutor.Builder b=new NioQueueGroupExecutor.Builder();
		b.setAssistExecutor(Executors.newSingleThreadExecutor());
		return b.setMaxPoolSize(max).setCorePoolSize(min).setBossQueue(bossQueue).setQueueGroupManagerr(kqm).build();
	}
	
	protected QueueGroupExecutor buildByMpMc(int minThread,int maxThread,int maxQueue,int maxPendingTask)
	{
		//多生产多消费者队列(线程竞争队列)
		Queue<Runnable> bossQueue=new MpscArrayQueue<>(maxQueue);
		QueueFactory qf=new QueueFactory() {
			@Override
			public RunnableQueue buildQueue() {
				//多生产单消费者队列(PS:bossQueue决定了一个队列只能同时被一个线程处理)
				Queue<Runnable> queue=MpscLinkedQueue.newMpscLinkedQueue();
				return new RunnableQueueWrapper(queue);
			}
		};
		QueueGroupManager kqm=new DefaultQueueManager(qf);
		NioQueueGroupExecutor.Builder b=new NioQueueGroupExecutor.Builder();
		b.setAssistExecutor(Executors.newSingleThreadExecutor());
		return b.setMaxPoolSize(minThread).setCorePoolSize(maxThread).setBossQueue(bossQueue).setQueueGroupManagerr(kqm).build();
	}
}
