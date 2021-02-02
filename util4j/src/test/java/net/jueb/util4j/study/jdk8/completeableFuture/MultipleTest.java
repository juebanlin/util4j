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
	
	/**
	 * 同类型-只要有一个-执行完成就执行下一步
	 */
	public static void test_same_acceptEitherAsync()
	{
		CompletableFuture<String> source1= CompletableFuture.supplyAsync(() -> {
			System.out.println("string1 start");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			return "string1";
		});
		CompletableFuture<String> source2= CompletableFuture.supplyAsync(() -> {
			System.out.println("string2 start");
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
			}
			return "string2";
		});
		source1.acceptEitherAsync(source2,(a)->{
			System.out.println("string is:"+a);
		});
	}
	
	/**
	 *同类型-只要有一个-执行完成就执行下一步并返回新类型
	 */
	public static void test_same_applyToEitherAsync()
	{
		CompletableFuture<String> source1= CompletableFuture.supplyAsync(() -> {
			System.out.println("string1 start");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			return "string1";
		});
		CompletableFuture<String> source2= CompletableFuture.supplyAsync(() -> {
			System.out.println("string2 start");
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
			}
			return "string2";
		});
		//同类型-只要有一个执行完成就执行下一步
		source1.applyToEitherAsync(source2,(a)->{
			System.out.println("string is:"+a);
			return "1";
		});
	}
	
	/**
	 * 接受2个不同future的结果,当2个都完成后才执行
	 */
	public static void test_notsame_thenAcceptBothAsync()
	{
		CompletableFuture<String> source1= CompletableFuture.supplyAsync(() -> {
			System.out.println("source1 start");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			return "string1";
		});
		CompletableFuture<Integer> source2= CompletableFuture.supplyAsync(() -> {
			System.out.println("source2 start");
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
			}
			return 1;
		});
		source1.thenAcceptBothAsync(source2, (v,u)->{
			System.out.println("v="+v+",u="+u);
		});
	}
	
	/**
	 * 接受2个不同future的结果,当2个都完成后才执行并返回新类型结果
	 */
	public static void test_notsame_thenCombineAsync()
	{
		CompletableFuture<String> source1= CompletableFuture.supplyAsync(() -> {
			System.out.println("source1 start");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			System.out.println("source1 end");
			return "string1";
		});
		CompletableFuture<Integer> source2= CompletableFuture.supplyAsync(() -> {
			System.out.println("source2 start");
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
			}
			System.out.println("source2 end");
			return 1;
		});
		source1.thenCombineAsync(source2,(v,u)->{
			System.out.println("v="+v+",u="+u);
			return false;
		});
	}
	/**
	 * 组合-返回新的不同类型的future,和handle类型,不同的是handle是单个future延续执行
	 */
	public static void test_notsame_thenComposeAsync()
	{
		CompletableFuture<String> source1= CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
			}
			System.out.println("source1 start");
			return "string1";
		});
		CompletableFuture<Integer> source2= CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			System.out.println("source2 start");
			return 1;
		});
		CompletableFuture<Boolean> source3= CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(8000);
			} catch (Exception e) {
			}
			System.out.println("source3 start");
			return false;
		});
		//组合有序执行
		source1.thenComposeAsync((v)->{
			System.out.println("source1 ->source2");
			return source2;
		}).thenComposeAsync((v)->{
			System.out.println("source2 ->source3");
			return source3;
		}).thenAcceptAsync((v)->{
			System.out.println(v);
		});
	}
	
	public static void main(String[] args) throws InterruptedException {
		test_notsame_thenCombineAsync();
		Thread.sleep(100000000);
	}
}
