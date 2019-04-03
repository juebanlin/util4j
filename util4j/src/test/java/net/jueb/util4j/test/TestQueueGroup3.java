package net.jueb.util4j.test;

import java.util.Queue;
import java.util.concurrent.Executors;

import org.jctools.queues.MpscLinkedQueue;
import org.jctools.queues.atomic.MpmcAtomicArrayQueue;

import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.RunnableQueue;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.NioQueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultQueueManager;
import net.jueb.util4j.queue.queueExecutor.queue.RunnableQueueWrapper;

public class TestQueueGroup3 {

	protected static QueueGroupExecutor buildByMpMc(int min,int max,int maxPendingTask)
	{
		int maxQueueCount=maxPendingTask;
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
		QueueGroupManager kqm=new DefaultQueueManager(qf);
		NioQueueGroupExecutor.Builder b=new NioQueueGroupExecutor.Builder();
		b.setAssistExecutor(Executors.newSingleThreadExecutor());
		NioQueueGroupExecutor e= b.setMaxPoolSize(max).setCorePoolSize(min).setBossQueue(bossQueue).setQueueGroupManagerr(kqm)
				.build();
		for(int i=0;i<max;i++)
		{
			e.addWorkerIfNecessary();
		}
		return e;
	}
	
	public static void main(String[] args) {
		QueueGroupExecutor qe=buildByMpMc(2, 4, 10000);
		qe.execute("1",new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		qe.execute("2",new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		qe.execute("3",new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		qe.execute("4",new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	};
}
