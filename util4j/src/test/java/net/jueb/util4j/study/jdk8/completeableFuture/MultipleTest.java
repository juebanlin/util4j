package net.jueb.util4j.study.jdk8.completeableFuture;

import java.util.concurrent.CompletableFuture;

/**
 * 多个异步操作持续执行并相互转换
 * 注意,一旦产生异常,则直接跳到下个能处理异常的函数执行,期间会忽略不能处理异常的函数类型
 * @author jaci
 */
public class MultipleTest {

	/**
	 * 不同异步持续处理
	 */
	public static void test1()
	{
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			System.out.println("start");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			return "string";
		});
		future.whenCompleteAsync((v, e) -> {//处理上阶段逻辑
			System.out.println("1,v="+v+",e="+e);
			try {
				Thread.sleep(1000);
			} catch (Exception e2) {
			}
			throw new RuntimeException("BBB Exception");//如果抛出异常则直接跳到后面能处理异常的函数执行
		}).whenCompleteAsync((v, e) -> {//接着处理并沿用之前的参数和上阶段的异常
			System.out.println("2,v="+v+",e="+e);
			try {
				Thread.sleep(1000);
			} catch (Exception e2) {
			}
		}).handleAsync((v,e)->{
			System.out.println("3,v="+v+",e="+e);
			try {
				Thread.sleep(1000);
			} catch (Exception e2) {
			}
			return 0.01f;
		}).thenAcceptAsync(v->{//异步接受
			System.out.println("4,v="+v);
			try {
				Thread.sleep(1000);
			} catch (Exception e2) {
			}
		}).handleAsync((v,e)->{
			System.out.println("5,v="+v+",e="+e);
			try {
				Thread.sleep(1000);
			} catch (Exception e2) {
			}
			return 111l;
		}).thenApplyAsync((v)->{//处理并返回新类型
			System.out.println("6,v="+v);
			try {
				Thread.sleep(1000);
			} catch (Exception e2) {
			}
			return Integer.valueOf(1);
		}).handleAsync((v,e)->{
			System.out.println("7,v="+v+",e="+e);
			try {
				Thread.sleep(1000);
			} catch (Exception e2) {
			}
			return 111l;
		}).thenApplyAsync((v)->{
			System.out.println("8,v="+v);
			try {
				Thread.sleep(1000);
			} catch (Exception e2) {
			}
			return Boolean.FALSE;
		}).whenCompleteAsync((v,e)->{
			System.out.println("9,v="+v+",e="+e);
		})
		;
	}
	
	/**
	 * 不同参数结果的转换衔接
	 */
	public static void test2()
	{
		CompletableFuture<String> source = CompletableFuture.supplyAsync(() -> {
			System.out.println("start");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			return "string";
		});
		//带异常的处理衔接
		CompletableFuture<String> source2=source.handleAsync((v,e)->{
			System.out.println("handle String,v="+v+",e="+e);
			try {
				Thread.sleep(1000);
			} catch (Exception e2) {
			}
			return 1;
		}).handleAsync((v,e)->{
			System.out.println("string to int success,v="+v+",e="+e);
			try {
				Thread.sleep(1000);
			} catch (Exception e2) {
			}
			return "int to string";
		});
		//不带异常的处理
		source2.thenApplyAsync((v)->{//参数+异常=>返回新类型
			return 2;
		}).thenAcceptAsync(v->{//处理参数不返回
			System.out.println("int result is:"+v);
		}).thenApplyAsync((v)->{//产生新的返回结果
			return "3";
		}).whenCompleteAsync((v,e)->{
			System.out.println("finaly reult,v="+v+",e="+e);
		});
	}
	
	public static void main(String[] args) throws InterruptedException {
		test2();
		Thread.sleep(100000000);
	}
}
