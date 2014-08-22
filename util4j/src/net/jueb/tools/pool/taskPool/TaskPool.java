package net.jueb.tools.pool.taskPool;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import net.jueb.tools.log.Log;

import org.apache.log4j.Logger;

public class TaskPool {
	public Logger log=Log.getLog(this.getClass().getName());
	private final ExecutorService inThread=Executors.newSingleThreadExecutor();
	private Thread currentTask;//当前正在执行的任务
	//双缓冲任务队列,用于存储当前排队的
	private final BlockingQueue<Task> tasks=new LinkedBlockingQueue<Task>();
	public TaskPool()
	{
		//创建内部调度线程
		Runnable in=new Runnable() {//线程池本身内部维护线程
			@Override
			public void run() {
				Task task=null;
				while(true)
				{
					task=tasks.poll();//从缓冲任务队列获取排队中的任务
					if(task==null)
					{
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							log.error("任务池内部调度机制延时异常!");
						}
						continue;//结束本次循环	
					}
					String count=tasks.size()+"";
					log.info("装载任务["+task.getTaskName()+"],剩余任务:"+count);
					currentTask=new Thread(task);//将任务装载到一个新线程
					
					if(task.getProperty()>0)
					{
						currentTask.setPriority(task.getProperty());//设置线程优先级
					}
					
					currentTask.start();//启动带任务线程
					log.info("任务["+task.getTaskName()+"]已执行!");
					try {
						synchronized(currentTask)//调用哪个对象的等待，就锁谁
						{
							log.info("等待任务["+task.getTaskName()+"]结束!");
							currentTask.wait();//等待上一个结束线程
							log.info("任务["+task.getTaskName()+"]已结束!");
						}	
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}	
			}
		};
		//启动内部调度线程
		inThread.execute(in);
		log.info("任务池启动完成!");
	}
	/**
	 * 往队列添加任务
	 * @param task
	 */
	public void addTask(final Task task)
	{
		synchronized (tasks) {
			Task r=new Task() {
				//装饰模式，添加自动解锁
				@Override
				public void run() {
					try {
						task.run();
					} catch (Exception e) {
						log.debug("任务出错退出:"+e.getMessage());
					}finally{//无论任务是否异常，都必须解锁
						synchronized(this)//语法要求，调用哪个对象的解除阻塞，就锁谁
						{
							this.notify();//通知其它正在等待的线程可以进行运行了；
						}
					}
				}
				@Override
				public String getTaskName() {
					return task.getTaskName();
				}
				@Override
				public String getTaskID() {
					return task.getTaskID();
				}
				@Override
				public String getAdminName() {
					return task.getAdminName();
				}
				@Override
				public int getProperty() {
					return task.getProperty();
				}
			};
			tasks.add(r);
			log.info("添加任务:["+r.getTaskName()+"]成功!");
		}
	}
	/**
	 * 获取当前任务池数量
	 * @return
	 */
	public int getTaskCount()
	{
		return tasks.size();
	}
	/**
	 * 关闭线程调度机制
	 */
	public void closePool()
	{
		//currentTask.destroy();
		this.inThread.shutdown();
		log.info("任务池关闭");
	}
	/**
	 * 清空还未执行的任务
	 */
	public void removeAllTask()
	{
		tasks.remove();
	}
}
