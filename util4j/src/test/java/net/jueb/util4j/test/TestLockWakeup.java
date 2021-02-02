package net.jueb.util4j.test;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import io.netty.channel.Channel.Unsafe;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestLockWakeup {

	static ExecutorService exec=new ThreadPoolExecutor(2, Integer.MAX_VALUE,60L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
	public static void main(String[] args) throws IOException {
		TestLockWakeup t=new TestLockWakeup();
		t.test1();
	}
	
	public void test1() throws IOException {
		SelectorProvider sp=SelectorProvider.provider();
		Selector st=sp.openSelector();
		exec.execute(()->{
			log.info("worker1休眠");
			try {
				st.select();
				log.info("worker1醒了");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		exec.execute(()->{
			log.info("worker2休眠");
			try {
				st.select();
				log.info("worker2醒了");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		exec.execute(()->{
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log.info("worker3唤醒");
			st.wakeup();
			log.info("worker3唤醒");
			st.wakeup();//只能唤醒一个
		});
	}
	
	public void test2() throws IOException {
		Thread[] ts=new Thread[2];
		exec.execute(()->{
			log.info("worker1休眠");
			try {
				ts[0]=Thread.currentThread();
				LockSupport.park();
				log.info("worker1醒了");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		exec.execute(()->{
			log.info("worker2休眠");
			try {
				ts[1]=Thread.currentThread();
				LockSupport.park();
				log.info("worker2醒了");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		exec.execute(()->{
			try {
				Thread.sleep(50000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log.info("worker3唤醒");
			LockSupport.unpark(ts[0]);
			log.info("worker3唤醒");
			LockSupport.unpark(ts[1]);
		});
	}
	
	/**
	 * 321-333=12
	 * 12-10=2ms
	 * 351-365=14
	 * 14-10=4ms
	 * @throws IOException
	 */
	public void test3() throws IOException {
		SelectorProvider sp=SelectorProvider.provider();
		Selector st=sp.openSelector();
		exec.execute(()->{
			try {
				for(int i=0;i<10;i++)
				{
					log.info("worker1休眠"+i);
					st.select();
					log.info("worker1醒了"+i);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		exec.execute(()->{
			try {
				Thread.sleep(100);
				for(int i=0;i<10;i++)
				{
					st.wakeup();
//					Thread.sleep(1);
					LockSupport.parkNanos(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * 253-265=13
	 * 13-10=3ms
	 * 268-281=13
	 * 13-0=12ms
	 * @throws IOException
	 */
	public void test4() throws IOException {
		Thread[] ts=new Thread[2];
		exec.execute(()->{
			try {
				ts[0]=Thread.currentThread();
				for(int i=0;i<10;i++)
				{
					log.info("worker1休眠"+i);
					LockSupport.park();
					log.info("worker1醒了"+i);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		exec.execute(()->{
			try {
				Thread.sleep(100);
				for(int i=0;i<10;i++)
				{
					LockSupport.unpark(ts[0]);
					LockSupport.parkNanos(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
