package net.jueb.util4j.beta.queue.taskQueue.test;

import net.jueb.util4j.queue.order.OrderTaskQueue.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang.math.RandomUtils;
import net.jueb.util4j.queue.order.OrderTaskQueueGroup;

public class Test {

	OrderTaskQueueGroup o=new OrderTaskQueueGroup();
	public static void main(String[] args) {
		new Test().test();
		new Scanner(System.in).nextLine();
	}
	
	public Task buildTask()
	{
		return new Task(){
			@Override
			public void action() throws Throwable {
			}

			@Override
			public String name() {
				return "";
			}
			
		};
	}
	Map<Integer,Long> startTime=new HashMap<>();
	public void test()
	{
		 new Thread(new Runnable() {

			@Override
			public void run() {
				int taskCount=10000000;
				int index=4;
				//开始时间标记任务
				for(int i=1;i<=index;i++)
				{
					Task t=buildTask();
					final int queueName=i;
					o.put(queueName+"", new Task() {
						@Override
						public String name() {
							return "";
						}
						@Override
						public void action() throws Throwable {
							startTime.put(queueName, System.currentTimeMillis());
						}
					});
				}
				//各个队列随机插入影响任务
				for(int i=1;i<=taskCount;i++)
				{
					Task t=buildTask();
					int queue=RandomUtils.nextInt(index)+1;
					o.put(queue+"",t);
				}
				//结束时间标记任务
				for(int i=1;i<=index;i++)
				{
					Task t=buildTask();
					final int queueName=i;
					o.put(queueName+"", new Task() {
						@Override
						public String name() {
							return "";
						}
						@Override
						public void action() throws Throwable {
							long time= System.currentTimeMillis()-startTime.get(queueName);
							System.err.println("队列："+queueName+",最后一个任务完成,队列耗时:"+time);
						}
					});
				}
			
			}
		 }).start();
	}
}
