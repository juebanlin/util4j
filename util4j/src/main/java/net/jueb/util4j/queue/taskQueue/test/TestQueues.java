package net.jueb.util4j.queue.taskQueue.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.lang.math.RandomUtils;
import net.jueb.util4j.queue.taskQueue.Task;
import net.jueb.util4j.queue.taskQueue.TaskQueueExecutor;
import net.jueb.util4j.queue.taskQueue.TaskQueuesExecutor;
import net.jueb.util4j.queue.taskQueue.impl.FixedThreadPoolQueuesExecutor;
import net.jueb.util4j.queue.taskQueue.impl.OrderTaskQueue;
import net.jueb.util4j.queue.taskQueue.impl.OrderTaskQueue2;
import net.jueb.util4j.queue.taskQueue.impl.SingleThreadTaskQueueExecutor;
import net.jueb.util4j.queue.taskQueue.impl.taskCenter.TaskCenter;

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
    	 
    	 
    	 public void test(final int taskCount,final int queueCount,final TaskQueuesExecutor o)
    	 {
    		 final Map<Integer,Integer> queueOp=new HashMap<>();
    		 final Map<Integer,Long> startTime=new HashMap<>();
    		 Runnable t=new Runnable() {
 				public void run() {
 					//开始时间标记任务
					for(int i=1;i<=queueCount;i++)
					{
						final int queueName=i;
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
					}
					//各个队列随机插入影响任务
 					for(int i=1;i<=taskCount;i++)
 					{
 						Task t=buildTask();
 						int queue=RandomUtils.nextInt(queueCount)+1;
 						o.execute(queue+"",t);
 					}
 					//结束时间标记任务
 					for(int i=1;i<=queueCount;i++)
 					{
 						final int queueName=i;
 						o.execute(queueName+"", new Task() {
							@Override
							public void run() {
								long time= System.currentTimeMillis()-startTime.get(queueName);
								System.err.println("队列："+queueName+",最后一个任务完成,队列耗时:"+time+",当前线程ID:"+Thread.currentThread().getId());
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
    	 
    	 public void test(final int taskCount,final TaskQueueExecutor o)
    	 {
    		 final Long[] startTime=new Long[1];
    		 Runnable t=new Runnable() {
 				public void run() {
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
 						Task t=buildTask();
 						o.execute(t);
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
 				}
 			};
 			new Thread(t).start();
    	 }
    	 public static void main(String[] args) {
    		 TestQueues tq=new TestQueues();
    		 int qt=1000000;//每个队列测试任务数量 
    		
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
    		 
//      	TaskQueuesExecutor ft=new FixedThreadPoolQueuesExecutor(2,2);
//      	tq.test(qt*4,4, ft);
//      	队列：2,最后一个任务完成,队列耗时:1183,当前线程ID:14
//      	队列：1,最后一个任务完成,队列耗时:1183,当前线程ID:15
//      	队列：3,最后一个任务完成,队列耗时:1183,当前线程ID:14
//      	队列：4,最后一个任务完成,队列耗时:1183,当前线程ID:15
    		 
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
//    		最后一个任务完成,队列耗时:114,当前线程ID:14
    		
    		
//    		OrderTaskQueue ft=new OrderTaskQueue("test");
//    		ft.start();
//     		tq.test(qt,ft);
//			最后一个任务完成,队列耗时:217,当前线程ID:13
    		 
//    		OrderTaskQueue2 ft=new OrderTaskQueue2("test");
//     		ft.start();
//      	tq.test(qt,ft);
//			最后一个任务完成,队列耗时:361,当前线程ID:13
    		 
    		TaskCenter ft=TaskCenter.getInstance();
    		tq.test(qt,ft);
//    		最后一个任务完成,队列耗时:1493,当前线程ID:1451
     		new Scanner(System.in).nextLine();
		}
}