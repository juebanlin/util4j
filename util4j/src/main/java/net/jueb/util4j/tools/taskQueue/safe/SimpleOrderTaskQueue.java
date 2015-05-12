package net.jueb.util4j.tools.taskQueue.safe;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 顺序任务执行队列
 * 
 * @author Administrator
 */
public class SimpleOrderTaskQueue {
	private Logger log = LoggerFactory.getLogger(getClass());
	/**
	 * 任务执行时间最多不能超过5秒
	 */
	protected final ConcurrentLinkedQueue<Task> tasks = new ConcurrentLinkedQueue<Task>();
	protected final TaskRunner runner;// 运行者

	public SimpleOrderTaskQueue(String name) {
		runner = new TaskRunner(name);
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
		private TaskObj currentTask;// 当前任务对象
		private final String name;
		private RunnnerCore runnnerCore;// 运行核心线程
		private boolean isActive;

		public TaskRunner(String name) {
			this.name = name;
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
				try {
					while (!shutdown) {
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
			if (!tasks.isEmpty() && runnnerCore!=null) {
				runnnerCore.wakeUpIfSleep();
			}
		}

		public TaskObj getCurrentTask() {
			return currentTask;
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
		SimpleOrderTaskQueue jo = new SimpleOrderTaskQueue("TestJobQueue");
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
