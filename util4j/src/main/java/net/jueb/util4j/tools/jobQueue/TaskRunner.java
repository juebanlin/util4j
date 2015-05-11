package net.jueb.util4j.tools.jobQueue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务线程执行者
 * 
 * @author Administrator
 *
 */
 class TaskRunner {
	private Logger log = LoggerFactory.getLogger(getClass());
	private TaskRunnerWatcher watcher;
	private ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<Runnable>();
	private TaskObj currentTask;// 当前任务对象
	private final String name;
	private RunnnerCore runnnerCore;//运行核心线程

	public TaskRunner(String name,TaskRunnerWatcher watcher) {
		this.name = name;
		this.watcher = watcher;
		watcher.regist(this);//当线程启动后注册到监视器
	}

	public void start() {
		if(runnnerCore==null)
		{
			runnnerCore=new RunnnerCore();
			try {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		runnnerCore.setDaemon(true);
		runnnerCore.setName(name);
		runnnerCore.start();
	}
	
	class RunnnerCore extends Thread
	{
		private CountDownLatch latch;
		private boolean shutdown;//关闭=false
		@Override
		public void run() {
			try {
				while (!shutdown) {
					watcher.wakeUpIfSleep();// 唤醒监视器
					if (tasks.isEmpty()) {// 线程睡眠
						long awaitStartTime = System.currentTimeMillis();
						log.debug("tasks.isEmpty(),TaskRunner sleep……");
						latch = new CountDownLatch(1);
						latch.await();
						latch=null;
						long awaitTime = System.currentTimeMillis()- awaitStartTime;
						log.debug("TaskRunner WakeUp,sleepTime=" + awaitTime+ " Millis");
					} else {// 线程被外部条件唤醒
						currentTask = new TaskObj(tasks.poll());
						currentTask.start();
						currentTask = null;
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * 如果是睡眠,则唤醒
		 */
		public void wakeUpIfSleep() {
			if (latch != null) {// 如果线程睡眠则唤醒
				latch.countDown();
			}
		}
		
		public void shutdown()
		{
			synchronized (this) {
				this.shutdown=true;
			}
		}
	}

	/**
	 * 跳过当前任务执行下面的任务
	 */
	public void skipCurrentTask(TaskObj task)
	{
		synchronized (currentTask) {
			if(task==currentTask)
			{
				RunnnerCore oldCore=this.runnnerCore;
				oldCore.setName(name+"_old");
				oldCore.shutdown();//优雅推出线程
				RunnnerCore newCore=new RunnnerCore();
				this.runnnerCore=newCore;
				start();
				log.warn("skip task"+task.toString());
			}
		}
	}
	
	public boolean isAlive()
	{
		return this.runnnerCore.isAlive();
	}
	
	public void shutdown()
	{
		this.runnnerCore.shutdown();
	}
	
	/**
	 * 如果是睡眠,则唤醒
	 */
	public void wakeUpIfSleep() {
		if(!tasks.isEmpty())
		{
			runnnerCore.wakeUpIfSleep();
		}
	}
	
	public TaskObj getCurrentTask()
	{
		synchronized (this) {
			return currentTask;
		}
	}
	
	public void addTask(Runnable task)
	{
		this.tasks.add(task);
		runnnerCore.wakeUpIfSleep();
	}
}