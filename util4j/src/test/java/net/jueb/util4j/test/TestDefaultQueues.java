package net.jueb.util4j.test;

import lombok.extern.slf4j.Slf4j;
import net.jueb.util4j.lock.waiteStrategy.BlockingWaitConditionStrategy;
import net.jueb.util4j.lock.waiteStrategy.SleepingWaitConditionStrategy;
import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.RunnableQueue;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultQueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultQueueManager;
import net.jueb.util4j.queue.queueExecutor.queue.RunnableQueueWrapper;
import net.jueb.util4j.queue.taskQueue.Task;
import net.jueb.util4j.queue.taskQueue.TaskQueueExecutor;
import org.apache.commons.lang.math.RandomUtils;
import org.jctools.queues.MpmcArrayQueue;
import org.jctools.queues.MpscArrayQueue;
import org.jctools.queues.MpscLinkedQueue;
import org.jctools.queues.atomic.MpmcAtomicArrayQueue;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class TestDefaultQueues {
    	 public Task buildTask()
    	 {
    		 return new Task() {
				@Override
				public void run() {
//					CdkeyFactoryRandomImpl cd=new CdkeyFactoryRandomImpl();
//					cd.build();
//					cd.build();
//					cd.build();
//					cd.build();
				}
				
				@Override
				public String name() {
					return "";
				}
			};
    	 }

    	/**
    	 * @param queueCount 队列数量
    	 * @param o
    	 */
		public void test(final long timeMillsLimit,final int queueCount,int threadNum,final QueueGroupExecutor o) throws InterruptedException {
			ExecutorService executorService= Executors.newFixedThreadPool(threadNum);
			final long[] startTimes=new long[queueCount];
			final int[] resultQueueCount=new int[queueCount];
			final AtomicLong addTime=new AtomicLong();
			CountDownLatch threadCountDown=new CountDownLatch(threadNum);
			CountDownLatch queueCountDown=new CountDownLatch(queueCount);
			Runnable[] tasks=new Runnable[threadNum];
			for (int i = 0; i < threadNum; i++) {
				tasks[i]=()->{
					try {
						{//丢任务到随机队列
							long startTime=System.currentTimeMillis();
							for(;;){
								for(int j=0;j<queueCount;j++){
									long time=System.currentTimeMillis()-startTime;
									if(time>=timeMillsLimit){
										log.info(Thread.currentThread().getName()+"线程退出");
										threadCountDown.countDown();
										return;
									}
									final int queueIndex=j;
									Runnable t=()->{
										resultQueueCount[queueIndex]++;
									};
									time=System.currentTimeMillis();
									o.execute(queueIndex+"",t);
									time=System.currentTimeMillis()-time;
									addTime.addAndGet(time);
								}
							}

						}
					}catch (Exception e){
						log.error(e.getMessage(),e);
					}
				};
			}
			//初始化开始任务
			for (int i = 0; i < queueCount; i++) {
				long t=System.currentTimeMillis();
				int queueIndex=i;
				o.execute(queueIndex+"",()->{
					startTimes[queueIndex]=System.currentTimeMillis();
					log.info("队列："+queueIndex+",初始化当前线程ID:"+Thread.currentThread().getId());
				});
				t=System.currentTimeMillis()-t;
				addTime.addAndGet(t);
			}
			//并行添加任务
			for (Runnable task : tasks) {
				executorService.submit(task);
			}
			threadCountDown.await();
			//结束任务
			for (int i = 0; i < queueCount; i++) {
				int queueIndex=i;
				o.execute(queueIndex+"",()->{
					long time=System.currentTimeMillis()-startTimes[queueIndex];;
					int num = resultQueueCount[queueIndex];
					log.info("队列："+queueIndex+",最后一个任务完成,添加队列耗时:"+addTime.get()+",得到任务数量:"+num+",队列总耗时:"+time+",当前线程ID:"+Thread.currentThread().getId());
					queueCountDown.countDown();
				});
			}
			queueCountDown.await();
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
//							try {
//								Thread.sleep(sleep);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
						}
						@Override
						public String name() {
							return "";
						}
					});
    		 }
    	 }
    	 
    	 public void testOrder(final int taskCount,final int queueCount,final QueueGroupExecutor o)
    	 {
    		 final Map<String,AtomicInteger> map1=new HashMap<>();
    		 final Map<String,AtomicInteger> map2=new HashMap<>();
    		 for(int i=1;i<=queueCount;i++)
    		 {
    			 map1.put(""+i, new AtomicInteger());
    			 map2.put(""+i, new AtomicInteger());
    		 }
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
 						final int queue=RandomUtils.nextInt(queueCount)+1;//随机加入队列
 						final String queueName=queue+"";
 						Task t=new Task() {
							@Override
							public void run() {
								int sleep=RandomUtils.nextInt(100);
								AtomicInteger count=map1.get(queueName);
								AtomicInteger value=map2.get(queueName);
								int index=count.incrementAndGet();
								if(index%2==0)
								{
									value.incrementAndGet();
//									System.err.println("i="+index+",value="+value.incrementAndGet()+",sleep="+sleep);
								}else
								{
									value.decrementAndGet();
//									System.err.println("i="+index+",value="+value.decrementAndGet()+",sleep="+sleep);
								}
//								try {
//									Thread.sleep(sleep);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
							}
							@Override
							public String name() {
								return null;
							}
						};
 						
 						long time=System.currentTimeMillis();
 						o.execute(queueName+"",t);
 						time=System.currentTimeMillis()-time;
						addTime.addAndGet(time);
 					}
 					//结束时间标记任务
 					for(int i=1;i<=queueCount;i++)
 					{
 						final int queue=i;
 						final String queueName=i+"";
 						o.execute(queueName+"", new Task() {
							@Override
							public void run() {
								long time= System.currentTimeMillis()-startTime.get(queue);
								AtomicInteger count=map1.get(queueName);
								AtomicInteger value=map2.get(queueName);
								System.err.println("队列："+queueName+"count="+count+",value="+value+"最后一个任务完成,添加队列耗时:"+addTime.get()+",队列总耗时:"+time+",当前线程ID:"+Thread.currentThread().getId());
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
    	 
    	 protected static QueueGroupExecutor buildStageByMpMc(int min,int max,int maxQueue,int maxQueuePendingTask)
    		{
    			//多生产多消费者队列(线程竞争队列)
    			Queue<Runnable> bossQueue=new ConcurrentLinkedQueue<>();
    			QueueFactory qf=new QueueFactory() {
    				@Override
    				public RunnableQueue buildQueue() {
    					//多生产单消费者队列(PS:bossQueue决定了一个队列只能同时被一个线程处理)
    					Queue<Runnable> queue=MpscLinkedQueue.newMpscLinkedQueue();
    					return new RunnableQueueWrapper(queue);
    				}
    			};
    			QueueGroupManager kqm=new DefaultQueueManager(qf,0);
    			DefaultQueueGroupExecutor.Builder b=new DefaultQueueGroupExecutor.Builder();
    			b.setAssistExecutor(Executors.newSingleThreadExecutor());
    			return b.setMaxPoolSize(max).setCorePoolSize(min).setKeepAliveTime(3, TimeUnit.SECONDS).setBossQueue(bossQueue).setQueueGroupManagerr(kqm).setWaitConditionStrategy(new SleepingWaitConditionStrategy()).build();
    		}
    	 
    	 public static void main(String[] args) throws InterruptedException {
    		 TestDefaultQueues tq=new TestDefaultQueues();
			 Thread.sleep(5000);
			 System.out.println("队列测试开始");
    		/**
    		 * 多队列多线程测试
    		 */
			QueueGroupExecutor ft=buildStageByMpMc(2,10,10,100000);
			System.out.println("#########1");
			tq.test(1000,10,11, ft);//1000W随机分配到10个队列

//			队列：6,最后一个任务完成,添加队列耗时:1343,队列总耗时:27089,当前线程ID:21
//			队列：10,最后一个任务完成,添加队列耗时:1343,队列总耗时:28280,当前线程ID:25
//			队列：2,最后一个任务完成,添加队列耗时:1343,队列总耗时:28353,当前线程ID:17
//			队列：9,最后一个任务完成,添加队列耗时:1343,队列总耗时:28634,当前线程ID:24
//			队列：4,最后一个任务完成,添加队列耗时:1343,队列总耗时:28719,当前线程ID:19
//			队列：8,最后一个任务完成,添加队列耗时:1343,队列总耗时:28743,当前线程ID:23
//			队列：3,最后一个任务完成,添加队列耗时:1343,队列总耗时:28849,当前线程ID:18
//			队列：5,最后一个任务完成,添加队列耗时:1343,队列总耗时:28854,当前线程ID:20
//			队列：1,最后一个任务完成,添加队列耗时:1343,队列总耗时:28945,当前线程ID:16
//			队列：7,最后一个任务完成,添加队列耗时:1343,队列总耗时:28986,当前线程ID:22
			
//			QueueGroupExecutor ft=buildStageByMpMc(2,10,10000);
//			tq.test(qt*5,10, ft);
//			队列：6,最后一个任务完成,添加队列耗时:1214,队列总耗时:27985,当前线程ID:22
//			队列：7,最后一个任务完成,添加队列耗时:1214,队列总耗时:28381,当前线程ID:19
//			队列：8,最后一个任务完成,添加队列耗时:1214,队列总耗时:28387,当前线程ID:23
//			队列：1,最后一个任务完成,添加队列耗时:1214,队列总耗时:28485,当前线程ID:21
//			队列：2,最后一个任务完成,添加队列耗时:1214,队列总耗时:28528,当前线程ID:20
//			队列：5,最后一个任务完成,添加队列耗时:1214,队列总耗时:28679,当前线程ID:24
//			队列：3,最后一个任务完成,添加队列耗时:1214,队列总耗时:28727,当前线程ID:16
//			队列：10,最后一个任务完成,添加队列耗时:1214,队列总耗时:28737,当前线程ID:25
//			队列：4,最后一个任务完成,添加队列耗时:1214,队列总耗时:28781,当前线程ID:17
//			队列：9,最后一个任务完成,添加队列耗时:1214,队列总耗时:28796,当前线程ID:18
			
    		Thread.sleep(10000000);
		}
}