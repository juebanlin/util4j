package net.jueb.util4j.beta.queue.taskQueue.impl;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import net.jueb.util4j.beta.queue.taskQueue.Task;
import net.jueb.util4j.beta.queue.taskQueue.TaskQueueExecutor;

/**
 * 单线程任务队列执行器
 * @author juebanlin
 */
public class SingleThreadTaskQueueExecutor extends AbstractTaskQueueExecutor{

	private final ThreadFactory threadFactory;
	
	public SingleThreadTaskQueueExecutor(String queueName) {
		this(queueName,Executors.defaultThreadFactory());
	}
	
	public SingleThreadTaskQueueExecutor(String queueName,ThreadFactory threadFactory) {
		super(queueName);
		if (threadFactory == null)
	            throw new NullPointerException();
		this.threadFactory = threadFactory;
	}
	
	public ThreadFactory getThreadFactory() {
		return threadFactory;
	}

	@Override
	public void execute(Task task) {
		super.execute(task);
		addWorkerIfNecessary();
		wakeUpWorkerIfNecessary();
	}
	
	private final Set<Worker> workers = new HashSet<Worker>();
	
	private void addWorkerIfNecessary() 
	{
		synchronized (workers) {
            if (workers.isEmpty()) {
                addWorker();
            }
        }
    }
	
	private void addWorker()
	{
		Worker worker = new Worker();
	    Thread thread = getThreadFactory().newThread(worker);
	    thread.start();
	    workers.add(worker);
	}

	/**
	 * 唤醒工作人员,如果有必要
	 */
	private void wakeUpWorkerIfNecessary()
	{
		for(Worker worker:workers)
		{
			worker.wakeUpIfNecessary();
		}
	}

	private class Worker implements Runnable {
		
		private CountDownLatch cd=new CountDownLatch(1);
		private final ReentrantLock lock=new ReentrantLock();
		
		protected void wakeUpIfNecessary()
		{
			lock.lock();
			try {
				cd.countDown();
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}finally{
				lock.unlock();
			}
		}
		
		private void sleep() throws InterruptedException 
		{
			lock.lock();
			try {
				cd = new CountDownLatch(1);
				cd.await();
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}finally{
				lock.unlock();
			}
		}
		
        public void run() {
            try {
            	while(true)
            	{
            		Task task=getQueue().poll();
                    if(task!=null)
                    {
                    	try {
                    		runTask(task);
						} catch (Throwable e) {
							log.error(e.getMessage(),e);
						}
                    }else
                    {
                    	try {
                    		sleep();
    					} catch (InterruptedException e) {
    						log.error(e.getMessage(),e);
    					}
                    }
            	}
            } finally {
                synchronized (workers) {
                    workers.remove(this);
                    workers.notifyAll();
                }
            }
        }
    }
	
	protected void runTask(Task task)
	{
		task.run();
	}
	
	public static long start;
	public static long end;
	public static void main(String[] args) {
		TaskQueueExecutor t=new SingleThreadTaskQueueExecutor("Test");
		final int count=1000000;
		for(int i=1;i<=count;i++)
		{
			final int x=i;
			t.execute(new Task() {
				@Override
				public void run() {
					if(x==1)
					{
						SingleThreadTaskQueueExecutor.start=System.nanoTime();
						System.out.println(x+":"+count);
					}
					if(x>=count)
					{
						SingleThreadTaskQueueExecutor.end=System.nanoTime();
						long t=SingleThreadTaskQueueExecutor.end-SingleThreadTaskQueueExecutor.start;
						System.err.println("t:"+TimeUnit.NANOSECONDS.toMillis(t));
						System.err.println("t:"+t);
					}
//					System.out.println(x);
//					UUID.randomUUID().toString();
				}
				@Override
				public String name() {
					return x+"";
				}
			});
		}
		new Scanner(System.in).nextLine();
	}
}
