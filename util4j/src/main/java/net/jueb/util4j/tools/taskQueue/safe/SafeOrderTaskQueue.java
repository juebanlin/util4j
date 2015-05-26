package net.jueb.util4j.tools.taskQueue.safe;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 顺序任务执行队列
 * 具备任务超时处理
 * @author Administrator
 */
public class SafeOrderTaskQueue {
	private Logger log = LoggerFactory.getLogger(getClass());
	protected final ConcurrentLinkedQueue<Task> tasks = new ConcurrentLinkedQueue<Task>();
	protected static TaskRunnerWatcher watcher;// 公共运行者监视器
	protected final TaskRunner runner;// 运行者
	
	{
		synchronized (this) {
			if (watcher == null) {
				watcher = new TaskRunnerWatcher();
				watcher.setName("OrderTaskQueueWatcher");
				watcher.setDaemon(true);
				watcher.start();
			}
		}
	}

	public SafeOrderTaskQueue(String name, long TaskRunTimeOutMillis) {
		runner = new TaskRunner(name, TaskRunTimeOutMillis, watcher);
	}

	public static interface Task {

		public void action();
	}

	/**
	 * 任务对象
	 * 
	 * @author Administrator
	 */
	class TaskObj {
		private long startTime = System.currentTimeMillis();// 开始时间
		private long endTime = 0;// 结束时间
		private Task task;

		public TaskObj(Task task) {
			this.task = task;
		}

		public void start() {
			try {
				task.action();
			} catch (Exception e) {
				log.debug("task error[" + task.getClass() + "]:"
						+ e.getMessage());
			}
			endTime = System.currentTimeMillis();
		}

		public long getStartTime() {
			return startTime;
		}

		public long getEndTime() {
			return endTime;
		}
	}

	/**
	 * 线程执行者
	 * 
	 * @author Administrator
	 *
	 */
	class TaskRunner {
		private long TaskRunTimeOutMillis;// 脚本超时毫秒
		private TaskRunnerWatcher watcher;
		private TaskObj currentTask;// 当前任务对象
		private final String name;
		private RunnnerCore runnnerCore;// 运行核心线程
		private boolean isActive;

		public TaskRunner(String name, long TaskRunTimeOutMillis,
				TaskRunnerWatcher watcher) {
			this.name = name;
			this.watcher = watcher;
			this.TaskRunTimeOutMillis = TaskRunTimeOutMillis;
		}

		public void start() {
			if (runnnerCore == null) {
				runnnerCore = new RunnnerCore();
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			runnnerCore.setDaemon(true);
			runnnerCore.setName(name);
			runnnerCore.start();
		}

		class RunnnerCore extends Thread {
			private CountDownLatch latch;
			private boolean shutdown;// 关闭=false

			@Override
			public void run() {
				isActive=true;
				watcher.regist(TaskRunner.this);// 当线程启动后注册到监视器
				try {
					while (!shutdown) {
						watcher.wakeUpIfSleep();// 唤醒监视器
						if (tasks.isEmpty()) {// 线程睡眠
							long awaitStartTime = System.currentTimeMillis();
							log.debug("tasks.isEmpty(),TaskRunner sleep……");
							latch = new CountDownLatch(1);
							latch.await();
							latch = null;
							long awaitTime = System.currentTimeMillis()
									- awaitStartTime;
							log.debug("TaskRunner WakeUp,sleepTime="
									+ awaitTime + " Millis");
						} else {// 线程被外部条件唤醒
							currentTask = new TaskObj(tasks.poll());
							currentTask.start();
							currentTask = null;
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				isActive=false;
			}

			/**
			 * 如果是睡眠,则唤醒
			 */
			public void wakeUpIfSleep() {
				if (latch != null) {// 如果线程睡眠则唤醒
					latch.countDown();
				}
			}

			public void shutdown() {
				this.shutdown = true;
			}
		}

		/**
		 * 跳过当前任务执行下面的任务
		 */
		public void skipCurrentTask(TaskObj task) {
			if (task == currentTask) {
				RunnnerCore oldCore = this.runnnerCore;
				oldCore.setName(name + "_old");
				RunnnerCore newCore = new RunnnerCore();
				this.runnnerCore = newCore;
				start();
				oldCore.shutdown();// 优雅推出线程
				log.warn("skip task" + task.toString());
			}
		}

		public boolean isActive() {
			return isActive;
		}

		public void shutdown() {
			this.runnnerCore.shutdown();
		}

		/**
		 * 如果是睡眠,则唤醒
		 */
		public void wakeUpIfSleep() {
			if (!tasks.isEmpty()) {
				runnnerCore.wakeUpIfSleep();
			}
		}

		public long getTaskRunTimeOutMillis() {
			return TaskRunTimeOutMillis;
		}

		public TaskObj getCurrentTask() {
			return currentTask;
		}
	}

	/**
	 * 监护线程 用于唤醒和处理超时任务
	 * 
	 * @author Administrator
	 */
	class TaskRunnerWatcher extends Thread {
		private HashSet<TaskRunner> runners = new HashSet<TaskRunner>();
		private CountDownLatch latch;

		public TaskRunnerWatcher() {
		}

		@Override
		public void run() {
			try {
				while (true) {
					Thread.sleep(100);
					boolean allStop = true;
					for (TaskRunner runner : runners) {// 遍历任务执行者
						if (runner.isActive()) {
							allStop = false;
							runner.wakeUpIfSleep();
							long TaskRunTimeOutMillis = runner
									.getTaskRunTimeOutMillis();
							if (TaskRunTimeOutMillis > 0) {
								TaskObj task = runner.getCurrentTask();
								if (task != null) {// 判断任务是否超时
									long startTime = task.getStartTime();
									long endTime = task.getEndTime();
									long costTime = 0;
									if (endTime <= 0) {
										endTime = System.currentTimeMillis();
									}
									costTime = endTime - startTime;
									if (costTime > TaskRunTimeOutMillis) {// 执行超时
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
			if (latch != null) {
				latch.countDown();
			}
		}
	}

	public String getName() {
		return runner.name;
	}

	public void addTask(Task task) {
		if (task != null) {
			tasks.add(task);
			runner.wakeUpIfSleep();
		}
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

	public void stop() {
		runner.shutdown();
	}

	public static void main(String[] args) throws InterruptedException {
		SafeOrderTaskQueue jo = new SafeOrderTaskQueue("TestJobQueue", 10000);
		jo.start();
		for (int i = 0; i < 1000; i++) {
			final int x = i;
			Thread.sleep(5000);
			jo.addTask(new Task() {
				@Override
				public void action() {
					System.out.println("x=" + x);
					if (x == 5) {
						try {
							Thread.sleep(11000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				@Override
				public String toString() {
					return x + "";
				}
			});
		}
	}
}
