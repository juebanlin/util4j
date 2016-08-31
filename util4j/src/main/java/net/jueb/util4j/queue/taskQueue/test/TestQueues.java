package net.jueb.util4j.queue.taskQueue.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.math.RandomUtils;

import net.jueb.util4j.queue.taskQueue.Task;
import net.jueb.util4j.queue.taskQueue.TaskQueueExecutor;
import net.jueb.util4j.queue.taskQueue.TaskQueuesExecutor;
import net.jueb.util4j.queue.taskQueue.impl.order.queueExecutor.multithread.disruptor.FixedThreadPoolQueuesExecutor_mina_disruptor;
import net.jueb.util4j.queue.taskQueue.impl.order.queueExecutor.multithread.mina.FixedThreadPoolQueuesExecutor_mina;

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
    		 final AtomicLong addTime=new AtomicLong();
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
					time=System.currentTimeMillis()-time;
					addTime.addAndGet(time);
					//各个队列随机插入影响任务
					for(int i=1;i<=taskCount;i++)
					{
						Task t=buildTask();
						time=System.currentTimeMillis();
						o.execute(t);
						time=System.currentTimeMillis()-time;
						addTime.addAndGet(time);
					}
					//结束时间标记任务
					o.execute(new Task() {
						@Override
						public void run() {
							long time= System.currentTimeMillis()-startTime[0];
							System.err.println("最后一个任务完成,放入任务耗时:"+addTime.get()+",队列耗时:"+time+",当前线程ID:"+Thread.currentThread().getId());
						}
						@Override
						public String name() {
							return "";
						}
					});
				}
			};
			new Thread(t).start();
		 }

		public void test(final int taskCount,final int queueCount,final TaskQueuesExecutor o)
    	{
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
 						Task t=buildTask();
 						int queue=RandomUtils.nextInt(queueCount)+1;//随机加入队列
 						long time=System.currentTimeMillis();
 						o.execute(queue+"",t);
 						time=System.currentTimeMillis()-time;
						addTime.addAndGet(time);
 					}
 					//结束时间标记任务
 					for(int i=1;i<=queueCount;i++)
 					{
 						final int queueName=i;
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
 					}
 				}
 			};
 			new Thread(t).start();
    	 }
    	 
    	 public void testOrder(final TaskQueueExecutor o)
    	 {
    		 final AtomicInteger atomicInteger=new AtomicInteger(0);
    		 for(int i=0;i<1000;i++)
    		 {
    			 final int x=i;
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
			 Thread.sleep(5000);
			 int queueCount=2;
			 FixedThreadPoolQueuesExecutor_mina_disruptor queue3=new FixedThreadPoolQueuesExecutor_mina_disruptor(2,4);
    		 t=System.currentTimeMillis();
    		 for(int i=1;i<=qt;i++)
			 {
    			queue3.execute(RandomUtils.nextInt(queueCount)+"", tq.buildTask());
			 }
			 System.err.println("QueuesExecutor1存入耗时："+(System.currentTimeMillis()-t));//3932
			 
			 FixedThreadPoolQueuesExecutor_mina queue4=new FixedThreadPoolQueuesExecutor_mina(2,4);
			 t=System.currentTimeMillis();
			 for(int i=1;i<=qt*queueCount;i++)
			 {
    			queue4.execute(RandomUtils.nextInt(queueCount)+"", tq.buildTask());
			 }
			 System.err.println("QueuesExecutor2存入耗时："+(System.currentTimeMillis()-t));
			 queue.clear();
			 queue2.clear();
			 Thread.sleep(10000);
    		/**
    		 * 多队列多线程测试
    		 */
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor_mina(1,8);
//			tq.test(qt*8,8, ft);
//			队列：1,最后一个任务完成,添加队列耗时:2710,队列总耗时:2976,当前线程ID:15
//			 队列：3,最后一个任务完成,添加队列耗时:2710,队列总耗时:2976,当前线程ID:16
//			 队列：5,最后一个任务完成,添加队列耗时:2710,队列总耗时:2976,当前线程ID:15
//			 队列：4,最后一个任务完成,添加队列耗时:2710,队列总耗时:2976,当前线程ID:17
//			 队列：2,最后一个任务完成,添加队列耗时:2710,队列总耗时:2976,当前线程ID:14
//			 队列：8,最后一个任务完成,添加队列耗时:2710,队列总耗时:2976,当前线程ID:17
//			 队列：7,最后一个任务完成,添加队列耗时:2710,队列总耗时:2976,当前线程ID:15
//			 队列：6,最后一个任务完成,添加队列耗时:2710,队列总耗时:2976,当前线程ID:16
			 
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor_mina_disruptor(4,8);
//     		tq.test(qt*8,8, ft);
//     		队列：6,最后一个任务完成,添加队列耗时:1894,队列总耗时:284,当前线程ID:21
//     		队列：8,最后一个任务完成,添加队列耗时:1894,队列总耗时:285,当前线程ID:16
//     		队列：5,最后一个任务完成,添加队列耗时:1894,队列总耗时:285,当前线程ID:20
//     		队列：3,最后一个任务完成,添加队列耗时:1894,队列总耗时:285,当前线程ID:16
//     		队列：7,最后一个任务完成,添加队列耗时:1894,队列总耗时:285,当前线程ID:19
//     		队列：2,最后一个任务完成,添加队列耗时:1894,队列总耗时:285,当前线程ID:20
//     		队列：1,最后一个任务完成,添加队列耗时:1894,队列总耗时:285,当前线程ID:21
//     		队列：4,最后一个任务完成,添加队列耗时:1894,队列总耗时:285,当前线程ID:17
     		
//    		TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor_mina(2,2);
//    		tq.test(qt*2,2, ft);
//    		队列：2,最后一个任务完成,添加队列耗时:721,队列总耗时:783,当前线程ID:19
//    		队列：1,最后一个任务完成,添加队列耗时:721,队列总耗时:783,当前线程ID:18
//    		tq.test(qt*4,4, ft);
//    		队列：1,最后一个任务完成,添加队列耗时:1337,队列总耗时:1463,当前线程ID:18
//    		队列：2,最后一个任务完成,添加队列耗时:1337,队列总耗时:1463,当前线程ID:19
//    		队列：3,最后一个任务完成,添加队列耗时:1337,队列总耗时:1463,当前线程ID:18
//    		队列：4,最后一个任务完成,添加队列耗时:1337,队列总耗时:1463,当前线程ID:19 
			 
//			TaskQueueExecutor t1=new SingleThreadTaskQueueExecutor_CountDownLatch("");
//			TaskQueueExecutor t2=new SingleThreadTaskQueueExecutor("");
//			TaskQueueExecutor t3=new SingleThreadBlockingTaskQueueExecutor("");
//			tq.test(qt, t1);
//			tq.test(qt, t1);
//			tq.test(qt, t1);
//			tq.test(qt, t1);
//			最后一个任务完成,放入任务耗时:494,队列耗时:547,当前线程ID:21
//			最后一个任务完成,放入任务耗时:491,队列耗时:555,当前线程ID:21
//			最后一个任务完成,放入任务耗时:476,队列耗时:559,当前线程ID:21
//			最后一个任务完成,放入任务耗时:525,队列耗时:565,当前线程ID:21
			
//			tq.test(qt, t2);
//			tq.test(qt, t2);
//			tq.test(qt, t2);
//			tq.test(qt, t2);
//			最后一个任务完成,放入任务耗时:546,队列耗时:568,当前线程ID:22
//			最后一个任务完成,放入任务耗时:561,队列耗时:575,当前线程ID:22
//			最后一个任务完成,放入任务耗时:585,队列耗时:616,当前线程ID:22
//			最后一个任务完成,放入任务耗时:598,队列耗时:617,当前线程ID:22
			
//			tq.test(qt, t3);
//			tq.test(qt, t3);
//			tq.test(qt, t3);
//			tq.test(qt, t3);
//			最后一个任务完成,放入任务耗时:793,队列耗时:821,当前线程ID:22
//			最后一个任务完成,放入任务耗时:795,队列耗时:839,当前线程ID:22
//			最后一个任务完成,放入任务耗时:842,队列耗时:874,当前线程ID:22
//			最后一个任务完成,放入任务耗时:833,队列耗时:874,当前线程ID:22
     		new Scanner(System.in).nextLine();
		}
}