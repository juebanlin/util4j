package net.jueb.util4j.beta.queue.taskQueue.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务线程
 * @author Administrator
 */
class DefaultQueueTaskThread extends Thread{
	
	public final Logger log = LoggerFactory.getLogger(getClass());
	private CountDownLatch latch;
	private boolean isActive;// 关闭=false
	private final ReentrantLock lock=new ReentrantLock();
	private volatile DefaultTaskTracer currentTask;
	private final DefaultQueueTaskExecutor executor;
	private volatile DefaultTaskTracer lastTask;
	private final DefaultQueueTaskContext context;
	
	protected DefaultQueueTaskThread(DefaultQueueTaskExecutor executor) {
		super();
		this.executor = executor;
		this.context=new DefaultQueueTaskContext(executor);
	}

	private void sleep() throws InterruptedException
	{
		latch = new CountDownLatch(1);
		latch.await();
	}
	
	@Override
	public final void run() {
		isActive = true;
		executor.setRunning(true);
		log.debug("TaskThreadRunning");
		try {
			while (isActive) 
			{
				currentTask=executor.pollTask();
				if (currentTask == null) 
				{// 线程睡眠
					sleep();
				} else 
				{// 线程被外部条件唤醒
					currentTask.setRunStartTime(System.currentTimeMillis());
					try {
						currentTask.getTask().run(context);
					}catch (Throwable e) {
						if(e instanceof InterruptedException)
						{
							//线程中断
						}else
						{
							log.error("执行器任务异常["+executor.getName()+"]==>["+currentTask.getTask().name()+"]:"+e.getMessage(),e);
							e.printStackTrace();
						}
					}
					currentTask.setRunEndTime(System.currentTimeMillis());
					lastTask=currentTask;
					currentTask=null;
				}
			}
		} catch (Throwable e) {
			if(e instanceof InterruptedException)
			{
				//线程中断
			}else
			{
				log.error("执行器线程异常["+getName()+"]:"+e.getMessage(),e);
				e.printStackTrace();
			}
		}
		isActive = false;
	}
	
	public final DefaultTaskTracer getCurrentTask() {
		return currentTask;
	}

	public final DefaultTaskTracer getLastTask() {
		return lastTask;
	}

	/**
	 * 如果是睡眠,则唤醒
	 */
	public final void wakeUpIfSleep() {
		try {
			lock.lock();
			if (latch != null && latch.getCount()>0) {// 如果线程睡眠则唤醒
				latch.countDown();
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	}

	public final void shutdown() {
		this.isActive = false;
		this.wakeUpIfSleep();
	}
	
	/**
	 * 强制关闭
	 */
	public final void shutdownForce() {
		shutdown();
		interrupt();
	}

	public final boolean isActive() {
		return isActive && isAlive();
	}
}