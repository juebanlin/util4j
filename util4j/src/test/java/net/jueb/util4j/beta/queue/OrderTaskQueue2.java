package net.jueb.util4j.beta.queue;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 顺序任务执行队列
 * 
 * @author Administrator
 */
public class OrderTaskQueue2 {
	public final Logger log = LoggerFactory.getLogger(getClass());
//	protected final Deque<Task> tasks = new ConcurrentLinkedDeque<Task>();
	protected final Queue<Task> tasks = new ConcurrentLinkedQueue<Task>();
	protected final TaskRunner runner;// 运行者
	protected final CountMonitor cm=new CountMonitor();
	public static long CountMonitorInterval=60*60*1000;//监视毫秒间隔
	private final ReentrantLock lock=new ReentrantLock();
	private final Condition wakeUpCondition=lock.newCondition();//唤醒条件
	public OrderTaskQueue2(String name) {
		runner = new TaskRunner(name);
	}
	public OrderTaskQueue2(String name,Collection<Task> tasks) {
		this.tasks.addAll(tasks);
		runner = new TaskRunner(name);
	}

	public String getName() {
		return runner.getName();
	}

	public void addTask(Task task) {
		if (task == null) {
			return ;
		}
		lock.lock();
		try {
			tasks.add(task);
			wakeUpCondition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public Queue<Task> getTasks() {
		return tasks;
	}

	public CountMonitor getCountMonitor() {
		return cm;
	}

	public int taskCount() {
		return tasks.size();
	}

	public boolean removeTask(Task task) {
		return tasks.remove(task);
	}

	public void start() {
		runner.start();
	}

	public boolean isActive()
	{
		return runner.isActive();
	}
	
	public void stop() {
		runner.shutdown();
	}

	public static interface Task {

		public void action()throws Throwable;

		public String name();
	}

	/**
	 * 任务对象
	 * @author Administrator
	 */
	class TaskObj {
		private long startNanoTime=0;// 开始时间
		private long endNanoTime = 0;// 结束时间
		private Task task;

		public TaskObj(Task task) {
			this.task = task;
		}

		public void start() {
			startNanoTime = System.nanoTime();
			try {
				task.action();
			} catch (Throwable e) {
				log.error("task error[" + task.getClass() + "]:"+ e.getMessage(),e);
				e.printStackTrace();
			}
			endNanoTime = System.nanoTime();
		}


		public Task getTask() {
			return task;
		}

		public void setTask(Task task) {
			this.task = task;
		}

		public long getStartNanoTime() {
			return startNanoTime;
		}

		public long getEndNanoTime() {
			return endNanoTime;
		}
	}

	/**
	 * 线程执行者
	 * 
	 * @author Administrator
	 *
	 */
	class TaskRunner {
		private TaskObj taskObj;// 当前任务对象
		private final String name;
		private RunnnerCore runnnerCore;// 运行核心线程
		private boolean isStarting;//线程是否正在启动
		public TaskRunner(String name) {
			this.name = name;
		}

		public void start() {
			isStarting=true;
			if (runnnerCore != null) 
			{
				runnnerCore.shutdown();
			}
			runnnerCore = new RunnnerCore();
			runnnerCore.setDaemon(true);
			runnnerCore.setName(name);
			runnnerCore.start();
		}

		private class RunnnerCore extends Thread {
			private boolean isActive;// 关闭=false
			
			@Override
			public void run() 
			{
				isStarting=false;
				isActive = true;
				try {
					while (isActive) 
					{
						lock.lock();
						try {
							Task task = tasks.poll();
							if (task == null) 
							{// 线程睡眠
								wakeUpCondition.await();
							}else
							{
								taskObj = new TaskObj(task);
								cm.taskRunBefore(taskObj);
								taskObj.start();
								cm.taskRunAfter(taskObj);
							}
						} finally {
							lock.unlock();
						}
					}
				} catch (Throwable e) {
					log.error(e.getMessage(),e);
				}
				isActive = false;
			}

			public void shutdown() {
				this.isActive = false;
			}

			public boolean isActive() {
				return isActive && isAlive();
			}
		}

		public boolean isActive() {
			return (this.runnnerCore!=null && this.runnnerCore.isActive())||this.isStarting;
		}

		public void shutdown() {
			if(this.runnnerCore!=null)
			{
				this.runnnerCore.shutdown();
			}
		}

		public TaskObj getLastRunTaskObj() {
			return taskObj;
		}

		public String getName() {
			return name;
		}
	}
	
	/**
	 * 效率监视
	 * @author Administrator
	 */
	class CountMonitor{
		private boolean enable=true;
		private final HashMap<String, MonitorEntry> entrys=new HashMap<String,MonitorEntry>();
		private final HashMap<String, MonitorEntry> interval=new HashMap<String,MonitorEntry>();//区间监测
		private long lastCheckTime=System.nanoTime();//上一次检查时间
		private long checkInterval=CountMonitorInterval;//监测时间间隔
		private SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss SSS");
		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public void taskRunBefore(TaskObj obj)
		{
			if(!enable)
			{
				return;
			}
			long currentTime=System.nanoTime();
			//当前时间距离上次检查时间的间隔毫秒大于规定值
			if(currentTime-lastCheckTime>TimeUnit.MILLISECONDS.toNanos(checkInterval))
			{
				syso(interval,getName()+"区间监视["+sdf.format(new Date(TimeUnit.NANOSECONDS.toMillis(lastCheckTime)))+","+sdf.format(TimeUnit.NANOSECONDS.toMillis(currentTime))+"]");
				syso(entrys,getName()+"总监视");
				interval.clear();
				lastCheckTime=currentTime;
			}
		}
		
		public void taskRunAfter(TaskObj obj)
		{
			if(!enable)
			{
				return;
			}
			long nanoTime=obj.getEndNanoTime()-obj.getStartNanoTime();//脚本耗时
			String name=obj.getTask().name();
			//更新区间监测数据
			long currentTime=System.nanoTime();
			if(currentTime-lastCheckTime<=TimeUnit.MILLISECONDS.toNanos(checkInterval))
			{
				if(!interval.containsKey(name))
				{
					interval.put(name, new MonitorEntry(name));
				}
				MonitorEntry entry=interval.get(name);
				entry.setTotalCount(entry.getTotalCount()+1);
				entry.setTotalTime(entry.getTotalTime()+nanoTime);
				if(entry.getMaxTime()<nanoTime)
				{
					entry.setMaxTime(nanoTime);
				}
			}
			//更新总监测数据
			if(!entrys.containsKey(name))
			{
				entrys.put(name, new MonitorEntry(name));
			}
			MonitorEntry entry=entrys.get(name);
			entry.setTotalCount(entry.getTotalCount()+1);
			entry.setTotalTime(entry.getTotalTime()+nanoTime);
			if(entry.getMaxTime()<nanoTime)
			{
				entry.setMaxTime(nanoTime);
			}
		}
		
		
		public void syso(HashMap<String, MonitorEntry> entrys,String title)
		{
			StringBuffer sb=new StringBuffer();
			sb.append("\n");
			sb.append("****************"+title+"********************\n");
			long allCount=0;
			long allTimes=0;
			for(String name:entrys.keySet())
			{
				MonitorEntry entry=entrys.get(name);
				allCount+=entry.getTotalCount();
				allTimes+=entry.getTotalTime();
				long totalMillTime=TimeUnit.NANOSECONDS.toMillis(entry.getTotalTime());
				long avgMillTime=TimeUnit.NANOSECONDS.toMillis(entry.getAvgTime());
				long maxMillTime=TimeUnit.NANOSECONDS.toMillis(entry.getMaxTime());
				sb.append("任务"+name+"执行总次数:"+entry.getTotalCount()+",总耗时"+totalMillTime+",平均耗时"+avgMillTime+",最大耗时"+maxMillTime+"\n");
			}
			sb.append("总计执行任务次数:"+allCount+"\n");
			sb.append("总计执行脚任务耗时:"+TimeUnit.NANOSECONDS.toMillis(allTimes)+"\n");
			sb.append("总计任务平均耗时:"+TimeUnit.NANOSECONDS.toMillis(allTimes/allCount)+"\n");
			sb.append("*************************************************");
			log.info(sb.toString());
			System.out.println(sb.toString());
		}
		
		/**
		 * 监视对象
		 * @author Administrator
		 */
		class MonitorEntry{
			private final String name;
			private long totalTime;//总时间
			private long totalCount;//总执行次数
			private long maxTime;//最大时间
			public MonitorEntry(String name) {
				this.name=name;
			}
			public String getName() {
				return name;
			}
			public long getTotalTime() {
				return totalTime;
			}
			public void setTotalTime(long totalTime) {
				this.totalTime = totalTime;
			}
			public long getTotalCount() {
				return totalCount;
			}
			public void setTotalCount(long totalCount) {
				this.totalCount = totalCount;
			}
			public long getMaxTime() {
				return maxTime;
			}
			public void setMaxTime(long maxTime) {
				this.maxTime = maxTime;
			}
			public long getAvgTime() {
				return totalTime/totalCount;
			}
		}
	}
	public static void main(String[] args) {
		final List<Task> ts=new ArrayList<Task>();
		final Map<Integer,Long> map=new HashMap<Integer,Long>();
		final Logger log = LoggerFactory.getLogger("taskLog");
		final int start=1;
		final int end=1000000;
		for(int i=start;i<=end;i++)
		{
			final int x=i;
			ts.add(new Task(){
				@Override
				public void action() throws Throwable {
					if(x==start)
					{
						System.err.println(System.currentTimeMillis());
						map.put(1,System.currentTimeMillis());
					}
					if(x==end)
					{
						System.err.println(System.currentTimeMillis());
						map.put(2,System.currentTimeMillis());
					}
//					log.debug("任务"+x+"执行");
				}

				@Override
				public String name() {
					return null;
				}
				
			});
		}
		final OrderTaskQueue2 q=new OrderTaskQueue2("123");
		q.start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(Task t:ts)
				{
					q.addTask(t);
				}
			}
		}).start();
		try {
			Thread.sleep(5000);
			long time=map.get(2)-map.get(1);
			System.out.println(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		new Scanner(System.in).nextLine();
	}
}
