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
		update();
	}
	
	private final Set<Worker> workers = new HashSet<Worker>();
	
	private final ReentrantLock lock=new ReentrantLock();
	
	protected void update()
	{
		lock.lock();
		try {
			addWorkerIfNecessary();
			wakeUpWorkerIfNecessary();
		} finally {
			lock.unlock();
		}
	}
	
	private void addWorkerIfNecessary() 
	{
		if(workers.isEmpty())
		{
			addWorker();
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
			worker.wakeUpUnsafe();
		}
	}

	private class Worker implements Runnable {
		
		private CountDownLatch cd;
		private final ReentrantLock lock=new ReentrantLock();
		
		private void wakeUpUnsafe()
		{
			if(cd!=null)
			{
				cd.countDown();
			}
		}
		
		private  void sleep() throws InterruptedException 
		{
			cd = new CountDownLatch(1);
			cd.await();
		}
		
        public void run() {
            try {
            	for(;;)
            	{
            		Task task=getQueue().poll();
                    if(task==null)
                    {
                    	try {
							sleep();
							continue;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
                    }
                    try {
                		runTask(task);
					} catch (Throwable e) {
						log.error(e.getMessage(),e);
					}
            	}
            } finally {
            	lock.lock();
        		try {
        			 workers.remove(this);
                     workers.notifyAll();
        		} finally {
        			lock.unlock();
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
	public static int i;
	public static void main(String[] args) throws InterruptedException {
		TaskQueueExecutor t=new SingleThreadTaskQueueExecutor("Test");
		Thread.sleep(5000);
		final int count=10000000;
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
					SingleThreadTaskQueueExecutor.i=x;
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
