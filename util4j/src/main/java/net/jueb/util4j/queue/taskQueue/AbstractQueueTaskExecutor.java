package net.jueb.util4j.queue.taskQueue;

import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象任务执行器
 * @author Administrator
 */
public abstract class AbstractQueueTaskExecutor implements QueueTaskExecutor{
	public final Logger log = LoggerFactory.getLogger(getClass());
	protected final String name;
	private final AtomicLong seq=new AtomicLong();
	
	public AbstractQueueTaskExecutor(String name) {
		this.name = name;
	}
	
	@Override
	public final String getName() {
		return this.name;
	}
	
	protected final long nextSeq()
	{
		return seq.incrementAndGet();
	}
	
	@Override
	public final long getRunSeq() {
		return seq.longValue();
	}
	
	/**
	 * 获取上一个执行的任务
	 * @return
	 */
	protected abstract TaskTracer lastRunTask();
	
	/**
	 * 下一个待执行的任务
	 * @return
	 */
	protected abstract TaskTracer nextTask();
	
	/**
	 * 取出下一个待执行的任务
	 * @return
	 */
	protected abstract TaskTracer pollTask();
}
