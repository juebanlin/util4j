package net.jueb.util4j.beta.queue.taskQueue.impl;

import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import net.jueb.util4j.beta.queue.taskQueue.AbstractQueueTaskExecutor;
import net.jueb.util4j.beta.queue.taskQueue.QueueTask;
import net.jueb.util4j.beta.queue.taskQueue.TaskTracer;

/**
 * 顺序任务执行队列
 * @author Administrator
 */
public class DefaultQueueTaskExecutor extends AbstractQueueTaskExecutor{
	
	protected final Deque<DefaultTaskTracer> tasks = new ConcurrentLinkedDeque<DefaultTaskTracer>();
	private final ReentrantLock lock=new ReentrantLock();
	private final AtomicLong threadSeq=new AtomicLong();
	private volatile boolean isRunning;
	protected DefaultQueueTaskThread thread;
	
	public DefaultQueueTaskExecutor(String name) {
		super(name);
	}
	
	protected final void addLast(QueueTask task)
	{
		DefaultTaskTracer tr=new DefaultTaskTracer(task);
		tr.setAppendTime(System.currentTimeMillis());
		tr.setSeq(threadSeq.incrementAndGet());
		tasks.addLast(tr);
		thread.wakeUpIfSleep();
	}
	
	@Override
	public boolean appendTask(QueueTask task) {
		if(task==null )
		{
			log.error("task is null");
		}
		if(!isActive())
		{
			log.error("Executor is not Active");
		}
		if(task!=null )
		{
			addLast(task);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean appendTask(List<QueueTask> tasks) {
		if(tasks==null )
		{
			log.error("task is null");
			return false;
		}
		if(!isActive())
		{
			log.error("Executor is not Active");
			return false;
		}
		for(QueueTask task:tasks)
		{
			addLast(task);
		}
		return true;
	}
	
	@Override
	public final boolean isActive() {
		return isRunning;
	}

	protected final void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	protected final DefaultQueueTaskThread getCurrentTaskThread()
	{
		return thread;
	}

	protected DefaultQueueTaskThread buildThread()
	{
		DefaultQueueTaskThread thread=new DefaultQueueTaskThread(this);
		thread.setDaemon(true);
		thread.setName("QueueExecutor["+getName()+"]-"+threadSeq.incrementAndGet());
		return thread;
	}

	@Override
	public int getOrderCount() {
		return tasks.size();
	}

	@Override
	public TaskTracer getCurrentTask() {
		return thread!=null?thread.getCurrentTask():null;
	}

	@Override
	protected DefaultTaskTracer lastRunTask() {
		return thread!=null?thread.getLastTask():null;
	}

	@Override
	protected DefaultTaskTracer nextTask() {
		return tasks.peek();
	}

	@Override
	protected DefaultTaskTracer pollTask() {
		return tasks.poll();
	}
	
	protected void runNextTask()
	{
		
	}

	@Override
	public void start() {
		lock.lock();
		try {
			if(!isRunning)
			{
				if(thread==null)
				{
					thread=buildThread();
				}
				thread.start();
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
	}

	@Override
	public Queue<TaskTracer> stop(boolean force) {
		lock.lock();
		Queue<TaskTracer> queue=new ConcurrentLinkedQueue<TaskTracer>();
		try {
			isRunning=false;
			if(thread!=null)
			{
				if(force)
				{
					thread.shutdownForce();
				}else
				{
					thread.shutdown();
				}
			}
			
			if(tasks.size()>0)
			{
				queue.addAll(tasks);
				tasks.clear();
			}
			return queue;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			lock.unlock();
		}
		return queue;
	}
}
