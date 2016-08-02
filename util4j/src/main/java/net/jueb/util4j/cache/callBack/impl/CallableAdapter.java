package net.jueb.util4j.cache.callBack.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.jueb.util4j.cache.callBack.CallBack;

/**
 * 解决webapp异步处理
 * @author Administrator
 * @param <C> Callable<C>
 * @param <T> CallBack<T>
 */
public abstract class CallableAdapter<C,T> implements Callable<C>,CallBack<T>{
		private final CountDownLatch latch=new CountDownLatch(1);
		private T result;
		private boolean isTimeOut=true;
		@Override
		public final void call(T result) {
			this.result=result;
			isTimeOut=false;
			latch.countDown();//解除阻塞
		}

		@Override
		public final void timeOutCall() {
			isTimeOut=true;
			latch.countDown();//解除阻塞
		}

		@Override
		public final C call() throws Exception {
			if(doAsycn())
			{
				latch.await(getTimeOut(), TimeUnit.MILLISECONDS);//等待CallBack的回调或者超时解锁
			}
			return doCall(result,isTimeOut);
		}
		
		protected abstract C doCall(T result,boolean isTimeOut);
		
		/**
		 * 执行异步,返回是否必须执行
		 * @return
		 */
		protected abstract boolean doAsycn();
	}