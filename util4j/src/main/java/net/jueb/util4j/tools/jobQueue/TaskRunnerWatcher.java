package net.jueb.util4j.tools.jobQueue;

import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监护线程 用于唤醒和处理超时任务
 * 
 * @author Administrator
 */
class TaskRunnerWatcher extends Thread {
	public static long TaskRunTimeOutMillis = 10000;
	private Logger log = LoggerFactory.getLogger(getClass());
	private HashSet<TaskRunner> runners = new HashSet<TaskRunner>();
	private CountDownLatch latch;

	@Override
	public void run() {
		try {
			while (true) {
				boolean allStop = true;
				for (TaskRunner runner : runners) {// 遍历任务执行者
					if (runner.isAlive()) {
						allStop = false;
						runner.wakeUpIfSleep();
						TaskObj task = runner.getCurrentTask();
						if (task != null) 
						{// 判断任务是否超时
							long startTime=task.getStartTime();
							long endTime=task.getEndTime();
							long costTime=0;
							if(endTime<=0)
							{
								endTime=System.currentTimeMillis();
							}
							costTime=endTime-startTime;
							if (costTime> TaskRunTimeOutMillis) {// 执行超时
								runner.skipCurrentTask(task);
							}
						}
					}
				}
				if (allStop) {// 如果所有线程都死亡了,则监视者睡眠
					log.debug("Not Found Alive TaskRunner,Watcher sleep……");
					long awaitStartTime = System.currentTimeMillis();
					latch = new CountDownLatch(1);
					latch.await();
					latch = null;
					long awaitTime = System.currentTimeMillis()
							- awaitStartTime;
					log.debug("Some TaskRunner isAlive,Watcher WakeUp,sleepTime="
							+ awaitTime + " Millis");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 注册
	 * 
	 * @param runner
	 */
	public void regist(TaskRunner runner) {
		this.runners.add(runner);
	}

	/**
	 * 取消监视注册
	 * 
	 * @param runner
	 */
	public void unRegist(TaskRunner runner) {
		this.runners.remove(runner);
	}

	/**
	 * 唤醒监视器
	 */
	public void wakeUpIfSleep() {
		synchronized (this) {
			if (latch != null) {
				latch.countDown();
			}
		}
	}
}