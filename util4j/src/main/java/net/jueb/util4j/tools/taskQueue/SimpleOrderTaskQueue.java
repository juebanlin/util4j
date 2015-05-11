package net.jueb.util4j.tools.taskQueue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 顺序任务执行队列
 * @author Administrator
 */
public class SimpleOrderTaskQueue {
	private Logger log=LoggerFactory.getLogger(getClass());
	/**
	 * 任务执行时间最多不能超过5秒
	 */
	public static long TaskRunTimeOutMillis=5000;
	private ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<Runnable>();
	private final TaskRunner runner;//运行者
	private final String name;
	private boolean shutdown;

	public SimpleOrderTaskQueue(String name) {
		this.name=name;
		runner=new TaskRunner();
		runner.setName("OrderTaskQueue["+name+"]");
	}
	
	/**
	 * 线程执行者
	 * @author Administrator
	 *
	 */
	class TaskRunner extends Thread
	{
		CountDownLatch latch;
		TaskObj currentTask;//当前任务对象
		@Override
		public void run() {
			try {
				while(!shutdown)
				{
					if(tasks.isEmpty())
					{//线程睡眠
						long awaitStartTime=System.currentTimeMillis();
						log.debug("tasks.isEmpty(),TaskRunner sleep……");
						latch=new CountDownLatch(1);
						latch.await();
						latch=null;
						long awaitTime=System.currentTimeMillis()-awaitStartTime;
						log.debug("TaskRunner WakeUp,sleepTime="+awaitTime+" Millis");
					}else
					{//线程被外部条件唤醒
						currentTask=new TaskObj(tasks.poll());
						currentTask.start();
						currentTask=null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * 任务对象
		 * @author Administrator
		 */
		class TaskObj{
			private long startTime;//开始时间
			private long endTime;//结束时间
			private long costTime;//消耗时间
			private Runnable task;
			public TaskObj(Runnable task) {
				this.task=task;
			}
			public void start()
			{
				startTime=System.currentTimeMillis();
				try {
					task.run();
				} catch (Exception e) {
					log.debug("task error["+currentTask.getClass()+"]:"+e.getMessage());
				}
				endTime=System.currentTimeMillis();
				costTime=endTime-startTime;
			}
			
			/**
			 * 获取当前位置所花执行时间
			 * @return
			 */
			public long getCostTime()
			{
				if(costTime==0)
				{
					costTime=System.currentTimeMillis()-startTime;
				}
				return costTime;
			}
			
			public String staskInfo()
			{
				return task.getClass().toString();
			}
		}
		
		/**
		 * 如果是睡眠,则唤醒
		 */
		public void wakeUpIfSleep()
		{
			synchronized (this) {
				if(runner.latch!=null)
				{//如果线程睡眠则唤醒
					runner.latch.countDown();
				}
			}
		}
		
	}
	
	
	
	public String getName()
	{
		return name;
	}
	
	public void addTask(Runnable task)
	{
		synchronized (this) {
			if(task!=null)
			{
				tasks.add(task);
				runner.wakeUpIfSleep();
			}
		}
	}
	
	public void start()
	{
		shutdown=false;
		runner.setDaemon(true);
		runner.start();
	}
	
	public void stop()
	{
		shutdown=true;
	}
	
	public static void main(String[] args) throws InterruptedException {
		SimpleOrderTaskQueue jo=new SimpleOrderTaskQueue("TestJobQueue");
		jo.start();
		for(int i=0;i<1000;i++)
		{
			Thread.sleep(5000);
			jo.addTask(new Runnable() {
				
				@Override
				public void run() {
					System.out.println("11111");
				}
			});
			if(i==3)
			{
				jo.addTask(new Runnable() {
					
					@Override
					public void run() {
						System.out.println("222222");
						try {
							Thread.sleep(6000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}
}
