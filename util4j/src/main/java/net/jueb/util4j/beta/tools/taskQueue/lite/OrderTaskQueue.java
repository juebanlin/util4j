package net.jueb.util4j.beta.tools.taskQueue.lite;

/**
 * 顺序任务执行队列
 * @author Administrator
 */
public class OrderTaskQueue {
	public static long TaskRunTimeOutMillis=10000;
	private static TaskRunnerWatcher watcher;//公共运行者监视器
	private final TaskRunner runner;//运行者
	private final String name;
	{
		synchronized (this) {
			if(watcher==null)
			{
				watcher=new TaskRunnerWatcher(TaskRunTimeOutMillis);
				watcher.setName("OrderTaskQueueWatcher");
				watcher.setDaemon(true);
				watcher.start();
			}
		}
	}

	public OrderTaskQueue(String name) {
		this.name=name;
		runner=new TaskRunner(name,watcher);
	}
	
	public String getName()
	{
		return name;
	}
	
	public void addTask(Task task)
	{
		runner.addTask(task);
	}
	
	public void start()
	{
		runner.start();
	}
	
	public void stop()
	{
		runner.shutdown();
	}
	public int taskCount()
	{
		return runner.taskCount();
	}
	
	public boolean removeTask(Task task)
	{
		return this.runner.removeTask(task);
	}
	
	public static void main(String[] args) throws InterruptedException {
		OrderTaskQueue jo=new OrderTaskQueue("TestJobQueue");
		jo.start();
		for(int i=0;i<1000;i++)
		{
			final int x=i;
			Thread.sleep(5000);
			jo.addTask(new Task(){
				@Override
				public void action() {
					System.out.println("x="+x);
					if(x==5)
					{
						try {
							Thread.sleep(11000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				@Override
				public String toString() {
					return x+"";
				}
			});
		}
	}
	
}
