package net.jueb.util4j.queue.queueExecutor;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.jctools.queues.MpscLinkedQueue;
import org.jctools.queues.atomic.MpmcAtomicArrayQueue;

import net.jueb.util4j.queue.queueExecutor.queue.RunnableQueueWrapper;

@FunctionalInterface
public interface QueueFactory {

	/**
	 * 默认队列工厂
	 */
	QueueFactory DEFAULT_QUEUE_FACTORY=()->new RunnableQueueWrapper(new ConcurrentLinkedQueue<>());
	
	/**
	 * 多生产单消费者队列工厂,适合同一时刻,多个线程往队列丢任务,单个线程处理队列任务
	 */
	QueueFactory MPSC_QUEUE_FACTORY=()->new RunnableQueueWrapper(MpscLinkedQueue.newMpscLinkedQueue());
	
	/**
	 * 多生产单多消费者队列工厂,适合同一时刻,多个线程往队列丢任务,多个线程处理队列任务
	 */
	QueueFactory MPMC_QUEUE_FACTORY=()->new RunnableQueueWrapper(new MpmcAtomicArrayQueue<>(Short.MAX_VALUE));
	
	
	RunnableQueue buildQueue(); 
}
