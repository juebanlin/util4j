package net.jueb.util4j.queue.taskQueue.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.math.RandomUtils;

import net.jueb.util4j.queue.taskQueue.Task;
import net.jueb.util4j.queue.taskQueue.TaskQueueExecutor;
import net.jueb.util4j.queue.taskQueue.TaskQueuesExecutor;
import net.jueb.util4j.queue.taskQueue.impl.order.orderTask.OrderTaskQueue2;
import net.jueb.util4j.queue.taskQueue.impl.order.queueExecutor.FixedThreadPoolQueuesExecutor;
import net.jueb.util4j.queue.taskQueue.impl.order.queueExecutor.FixedThreadPoolQueuesExecutor_Condition;
import net.jueb.util4j.queue.taskQueue.impl.order.queueExecutor.FixedThreadPoolQueuesExecutor_Condition2;
import net.jueb.util4j.queue.taskQueue.impl.order.queueExecutor.SingleThreadBlockingTaskQueueExecutor;
import net.jueb.util4j.thread.NamedThreadFactory;

public class TestQueues{
    	 public Task buildTask()
    	 {
    		 return new Task() {
				@Override
				public void run() {
					
				}
				
				@Override
				public String name() {
					return "";
				}
			};
    	 }
    	 
    	 public void test(final int taskCount,final TaskQueueExecutor o)
		 {
    		 final Executor e=Executors.newFixedThreadPool(2,new NamedThreadFactory("taskAdder"));
			 final Long[] startTime=new Long[1];
			 Runnable t=new Runnable() {
				public void run() {
					long time=System.currentTimeMillis();
					//开始时间标记任务
					o.execute(new Task() {
						@Override
						public void run() {
							startTime[0]=System.currentTimeMillis();
						}
						@Override
						public String name() {
							return "";
						}
					});
					//各个队列随机插入影响任务
					for(int i=1;i<=taskCount;i++)
					{
						e.execute(new Runnable() {
							@Override
							public void run() {
								Task t=buildTask();
								o.execute(t);
							}
						});
					}
					//结束时间标记任务
					o.execute(new Task() {
						@Override
						public void run() {
							long time= System.currentTimeMillis()-startTime[0];
							System.err.println("最后一个任务完成,队列耗时:"+time+",当前线程ID:"+Thread.currentThread().getId());
						}
						@Override
						public String name() {
							return "";
						}
					});
					time=System.currentTimeMillis()-time;
					System.err.println("放入任务耗时:"+time+",当前线程ID:"+Thread.currentThread().getId());
				}
			};
			new Thread(t).start();
		 }

		public void test(final int taskCount,final int queueCount,final TaskQueuesExecutor o)
    	{
			 final Executor e=Executors.newFixedThreadPool(2,new NamedThreadFactory("taskAdder"));
    		 final Map<Integer,Integer> queueOp=new HashMap<>();
    		 final Map<Integer,Long> startTime=new HashMap<>();
    		 final AtomicLong addTime=new AtomicLong();
    		 Runnable t=new Runnable() {
 				public void run() {
 					//开始时间标记任务
					for(int i=1;i<=queueCount;i++)
					{
						final int queueName=i;
						long t=System.currentTimeMillis();
						o.execute(queueName+"", new Task() {
							@Override
							public void run() {
								startTime.put(queueName, System.currentTimeMillis());
								queueOp.put(queueName, 0);
							}
							@Override
							public String name() {
								return "";
							}
						});
						t=System.currentTimeMillis()-t;
						addTime.addAndGet(t);
					}
					//各个队列随机插入影响任务
 					for(int i=1;i<=taskCount;i++)
 					{
 						long t=System.currentTimeMillis();
 						e.execute(new Runnable() {
							@Override
							public void run() {
								Task t=buildTask();
		 						int queue=RandomUtils.nextInt(queueCount)+1;//随机加入队列
		 						o.execute(queue+"",t);
							}
						});
 						t=System.currentTimeMillis()-t;
						addTime.addAndGet(t);
 					}
 					//结束时间标记任务
 					for(int i=1;i<=queueCount;i++)
 					{
 						final int queueName=i;
 						long t=System.currentTimeMillis();
 						o.execute(queueName+"", new Task() {
							@Override
							public void run() {
								long time= System.currentTimeMillis()-startTime.get(queueName);
								System.err.println("队列："+queueName+",最后一个任务完成,添加队列耗时:"+addTime.get()+",队列总耗时:"+time+",当前线程ID:"+Thread.currentThread().getId());
							}
							@Override
							public String name() {
								return "";
							}
						});
 						t=System.currentTimeMillis()-t;
						addTime.addAndGet(t);
 					}
 				}
 			};
 			new Thread(t).start();
    	 }
    	 
    	 public void testOrder(final TaskQueueExecutor o)
    	 {
    		 final Executor e=Executors.newFixedThreadPool(2,new NamedThreadFactory("taskAdder"));
    		 final AtomicInteger atomicInteger=new AtomicInteger(0);
    		 for(int i=0;i<1000;i++)
    		 {
    			 final int x=i;
    			 e.execute(new Runnable() {
					@Override
					public void run() {
						 o.execute(new Task() {
								@Override
								public void run() {
									int sleep=RandomUtils.nextInt(100);
									if(x%2==0)
									{
										System.err.println("i="+x+",value="+atomicInteger.incrementAndGet()+",sleep="+sleep);
									}else
									{
										System.err.println("i="+x+",value="+atomicInteger.decrementAndGet()+",sleep="+sleep);
									}
									try {
										Thread.sleep(sleep);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								@Override
								public String name() {
									return "";
								}
							});
					}
				});
    		 }
    	 }
    	 
    	 public void testOrder(final TaskQueuesExecutor o)
    	 {
//    		 
    	 }
    	 
    	 public static void main(String[] args) throws InterruptedException {
    		 TestQueues tq=new TestQueues();
    		 int qt=1000000;//每个队列测试任务数量 
    		 Queue<Task> queue=new ConcurrentLinkedQueue<Task>();//queue存入耗时：36
    		 Queue<Task> queue2=new LinkedBlockingQueue<Task>();//BlockingQueue存入耗时：180
    		 long t=System.currentTimeMillis();
			 for(int i=1;i<=qt;i++)
			 {
				Task t1=tq.buildTask();
				queue.add(t1);
			 }
			 System.err.println("queue存入耗时："+(System.currentTimeMillis()-t));
			 t=System.currentTimeMillis();
			 for(int i=1;i<=qt;i++)
			 {
				Task t2=tq.buildTask();
				queue2.add(t2);
			 }
			 System.err.println("BlockingQueue存入耗时："+(System.currentTimeMillis()-t));
			 Thread.sleep(10000);
    		/**
    		 * 多队列多线程测试
    		 */
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(4,4);
//     		tq.test(qt*4,4, ft);
//     		队列：2,最后一个任务完成,队列耗时:1448,当前线程ID:14
//     		队列：4,最后一个任务完成,队列耗时:1448,当前线程ID:17
//     		队列：3,最后一个任务完成,队列耗时:1448,当前线程ID:16
//     		队列：1,最后一个任务完成,队列耗时:1448,当前线程ID:15
    		 
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(1,2);
//    		tq.test(qt*4,4, ft);
//    		队列：1,最后一个任务完成,队列耗时:1142,当前线程ID:14
//    		队列：2,最后一个任务完成,队列耗时:1142,当前线程ID:15
//    		队列：3,最后一个任务完成,队列耗时:1142,当前线程ID:14
//    		队列：4,最后一个任务完成,队列耗时:1142,当前线程ID:15
    		 
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(1,3);
//     		tq.test(qt*4,4, ft);
//     		队列：2,最后一个任务完成,队列耗时:1325,当前线程ID:14
//     		队列：3,最后一个任务完成,队列耗时:1325,当前线程ID:16
//     		队列：1,最后一个任务完成,队列耗时:1325,当前线程ID:15
//     		队列：4,最后一个任务完成,队列耗时:1325,当前线程ID:14
     		
//     		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(1,4);
//     		tq.test(qt*4,4, ft);
//     		队列：3,最后一个任务完成,队列耗时:1516,当前线程ID:14
//     		队列：2,最后一个任务完成,队列耗时:1516,当前线程ID:15
//     		队列：1,最后一个任务完成,队列耗时:1516,当前线程ID:16
//     		队列：4,最后一个任务完成,队列耗时:1516,当前线程ID:17
    		 
//			TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(2,2);
//			tq.test(qt*4,4, ft);
//      	队列：2,最后一个任务完成,队列耗时:1183,当前线程ID:14
//      	队列：1,最后一个任务完成,队列耗时:1183,当前线程ID:15
//      	队列：3,最后一个任务完成,队列耗时:1183,当前线程ID:14
//      	队列：4,最后一个任务完成,队列耗时:1183,当前线程ID:15
			 
			TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor_Condition(2,2);
	      	tq.test(qt*4,4, ft);
//	      	队列：1,最后一个任务完成,队列耗时:2663,当前线程ID:17
//	      	队列：2,最后一个任务完成,队列耗时:2663,当前线程ID:14
//	      	队列：4,最后一个任务完成,队列耗时:2662,当前线程ID:14
//	      	队列：3,最后一个任务完成,队列耗时:2663,当前线程ID:17
//	      	
//	      	TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor_Condition2(2,2);
//	      	tq.test(qt*8,8, ft);
    		 
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(2,3);
//    		tq.test(qt*4,4, ft);
//    		队列：1,最后一个任务完成,队列耗时:1310,当前线程ID:14
//    		队列：3,最后一个任务完成,队列耗时:1310,当前线程ID:16
//    		队列：2,最后一个任务完成,队列耗时:1310,当前线程ID:15
//    		队列：4,最后一个任务完成,队列耗时:1310,当前线程ID:14
    		
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(2,4);
//    		tq.test(qt*4,4, ft);
//    		队列：4,最后一个任务完成,队列耗时:1549,当前线程ID:14
//    		队列：1,最后一个任务完成,队列耗时:1549,当前线程ID:16
//    		队列：3,最后一个任务完成,队列耗时:1549,当前线程ID:17
//    		队列：2,最后一个任务完成,队列耗时:1549,当前线程ID:15

//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(3,3);
//     		tq.test(qt*4,4, ft);
//     		队列：1,最后一个任务完成,队列耗时:1307,当前线程ID:14
//     		队列：3,最后一个任务完成,队列耗时:1307,当前线程ID:16
//     		队列：2,最后一个任务完成,队列耗时:1307,当前线程ID:15
//     		队列：4,最后一个任务完成,队列耗时:1307,当前线程ID:14
    		 
//     		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(3,4);
//     		tq.test(qt*4,4, ft);
//     		队列：3,最后一个任务完成,队列耗时:1448,当前线程ID:14
//     		队列：1,最后一个任务完成,队列耗时:1448,当前线程ID:16
//     		队列：4,最后一个任务完成,队列耗时:1448,当前线程ID:15
//     		队列：2,最后一个任务完成,队列耗时:1448,当前线程ID:17
    		 
//      	TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(4,4);
//      	tq.test(qt*4,4, ft);
//      	队列：3,最后一个任务完成,队列耗时:1500,当前线程ID:14
//      	队列：1,最后一个任务完成,队列耗时:1500,当前线程ID:17
//      	队列：2,最后一个任务完成,队列耗时:1500,当前线程ID:16
//      	队列：4,最后一个任务完成,队列耗时:1500,当前线程ID:15
    		 
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(4,4);
//    		tq.test(qt*8,8, ft);
//    		队列：4,最后一个任务完成,队列耗时:2709,当前线程ID:17
//    		队列：5,最后一个任务完成,队列耗时:2710,当前线程ID:16
//    		队列：1,最后一个任务完成,队列耗时:2711,当前线程ID:16
//    		队列：3,最后一个任务完成,队列耗时:2713,当前线程ID:16
//    		队列：6,最后一个任务完成,队列耗时:2713,当前线程ID:16
//    		队列：7,最后一个任务完成,队列耗时:2713,当前线程ID:16
//    		队列：8,最后一个任务完成,队列耗时:2713,当前线程ID:16
//    		队列：2,最后一个任务完成,队列耗时:2713,当前线程ID:17
    		 
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(4,4);
//     		tq.test(qt*16,16, ft);
//     		队列：3,最后一个任务完成,队列耗时:6379,当前线程ID:14
//     		队列：4,最后一个任务完成,队列耗时:6379,当前线程ID:17
//     		队列：5,最后一个任务完成,队列耗时:6379,当前线程ID:14
//     		队列：2,最后一个任务完成,队列耗时:6379,当前线程ID:16
//     		队列：1,最后一个任务完成,队列耗时:6379,当前线程ID:15
//     		队列：7,最后一个任务完成,队列耗时:6379,当前线程ID:14
//     		队列：8,最后一个任务完成,队列耗时:6379,当前线程ID:16
//     		队列：6,最后一个任务完成,队列耗时:6379,当前线程ID:17
//     		队列：12,最后一个任务完成,队列耗时:6380,当前线程ID:17
//     		队列：13,最后一个任务完成,队列耗时:6380,当前线程ID:17
//     		队列：14,最后一个任务完成,队列耗时:6380,当前线程ID:17
//     		队列：15,最后一个任务完成,队列耗时:6380,当前线程ID:17
//     		队列：16,最后一个任务完成,队列耗时:6380,当前线程ID:17
//     		队列：11,最后一个任务完成,队列耗时:6380,当前线程ID:16
//     		队列：10,最后一个任务完成,队列耗时:6379,当前线程ID:14
//     		队列：9,最后一个任务完成,队列耗时:6379,当前线程ID:15
    		
    		/**
    		 * 单队列多线程测试
    		 */
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(1,1);
//    		tq.test(qt,1, ft);
//    		队列：1,最后一个任务完成,队列耗时:283,当前线程ID:14
    		 
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(1,2);
//     		tq.test(qt,1, ft);
//     		队列：1,最后一个任务完成,队列耗时:288,当前线程ID:15
 		 
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(1,3);
//    		tq.test(qt,1, ft);
//    		队列：1,最后一个任务完成,队列耗时:302,当前线程ID:15
    		
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(1,4);
//    		tq.test(qt,1, ft);
//    		队列：1,最后一个任务完成,队列耗时:289,当前线程ID:15
			 
    		 /**
     		 * 单队列单线程测试
     		 */ 
//    		TaskQueueExecutor ft=new SingleThreadTaskQueueExecutor("test");
//    		tq.test(qt,ft);
//    		放入任务耗时:107,当前线程ID:13
//    		最后一个任务完成,队列耗时:106,当前线程ID:14
    		 
//    		TaskQueueExecutor ft=new SingleThreadBlockingTaskQueueExecutor("test");
//     		tq.test(qt,ft);
//     		放入任务耗时:200,当前线程ID:13
//     		最后一个任务完成,队列耗时:199,当前线程ID:14;
    		
//    		OrderTaskQueue ft=new OrderTaskQueue("test");
//    		ft.start();
//     		tq.test(qt,ft);
//     		放入任务耗时:209,当前线程ID:14
//     		最后一个任务完成,队列耗时:241,当前线程ID:13
    		 
//    		OrderTaskQueue2 ft=new OrderTaskQueue2("test");
//     		ft.start();
//     		tq.test(qt,ft);
//			最后一个任务完成,队列耗时:361,当前线程ID:13
    		 
//    		TaskCenter ft=new TaskCenter("test");
//    		tq.test(qt,ft);
//    		最后一个任务完成,队列耗时:1493,当前线程ID:1451
    		 
//    		TaskCenter ft=new TaskCenter("test");
//    		ft.setMaxThread(1);
//     		tq.test(qt,ft);
//     		最后一个任务完成,队列耗时:135,当前线程ID:47
    		 
     		new Scanner(System.in).nextLine();
		}
}