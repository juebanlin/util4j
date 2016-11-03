package net.jueb.util4j.test;

import java.util.Scanner;

import net.jueb.util4j.queue.queueExecutor.queueGroup.QueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.queueGroup.impl.DefaultQueueGroupExecutor;

public class TestQueue {

	public static void main(String[] args) {
		QueueGroupExecutor qe=new DefaultQueueGroupExecutor(2,8);
		qe.execute((short) 1,new Runnable() {
			
			@Override
			public void run() {
				System.out.println("1");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("2");
			}
		});
		qe.execute((short) 2,new Runnable() {
					
					@Override
					public void run() {
						System.out.println("3");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("4");
					}
				});
		new Scanner(System.in).nextLine();
	}
}
