package net.jueb.util4j.beta.queue.taskQueue.impl;

import java.util.Scanner;
import java.util.Set;

import net.jueb.util4j.beta.queue.taskQueue.QueueTask;
import net.jueb.util4j.beta.queue.taskQueue.QueueTaskContext;

public class Test {

	public static void main(String[] args) {
		Test t=new Test();
		t.test();
		Scanner sc=new Scanner(System.in);
		sc.nextLine();
		sc.close();
	}
	
	
	public void test()
	{
		DefaultQueueTaskExecutor t=new DefaultQueueTaskExecutor("testQueue");
		t.start();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.err.println("start add task");
		for(int i=0;i<100;i++)
		{
			if(i%2==0)
			{
				t.appendTask(buildTask(i));
			}else
			{
				t.appendTask(buildTask2(i,i*100));
			}
		}
		System.err.println(t.getOrderCount());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(t.stop(false));
	}
	
	public QueueTask buildTask(final int index)
	{
		QueueTask task=new QueueTask() {
			@Override
			public void run(QueueTaskContext context) {
				System.out.println("脚本:"+name()+"执行"+context.lastRunTask()+"<==>"+context.nextTask());
			}
			
			@Override
			public String name() {
				return "task-"+index;
			}
			
			@Override
			public Set<String> getTags() {
				return null;
			}
			@Override
			public String toString() {
				return name();
			}
		};
		return task;
	}
	
	public QueueTask buildTask2(final int index,final long sleep)
	{
		QueueTask task=new QueueTask() {
			@Override
			public void run(QueueTaskContext context) {
				System.out.println("睡眠:"+name()+"执行"+context.lastRunTask()+"<==>"+context.nextTask());
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public String name() {
				return "task-"+index;
			}
			
			@Override
			public Set<String> getTags() {
				return null;
			}
			@Override
			public String toString() {
				return name();
			}
		};
		return task;
	}
}
