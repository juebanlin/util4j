package net.jueb.util4j.cache.callBack.impl;

import java.util.Objects;
import java.util.function.Consumer;
import net.jueb.util4j.cache.callBack.CallBack;

/**
 * 使用JDK8的表达式和方法引用实现
 * <pre>{@code
 * public void run() 
 * {
 * 	    CallBack<Boolean> callBack=null;
 *    	//表达式实现
 *    	callBack=new CallBackBind<Boolean>((result)->{
 *   		System.out.println("登录结果:"+result);
 *    	},()->{
 *   		System.out.println("登录超时");
 *    	});
 *    	//方法引用
 *    	callBack=new CallBackBind<Boolean>(this::login_call,this::login_call_timeout);
 *    	callBack.call(true);
 * }
 *
 *    public void login_call(Boolean result)
 *    {
 *    	System.out.println("登录结果:"+result);
 *    }
 *    
 *    public void login_call_timeout()
 *    {
 *    	System.out.println("登录超时");
 *    }
 * 
 *</pre>
 * @author Administrator
 * @param <T>
 */
public class CallBackBind<T> implements CallBack<T>{

	private long timeOut;
	private final Consumer<T> call;
	private final Runnable timeOutCall;
	
	public CallBackBind(Consumer<T> call, Runnable timeOutCall) {
		this(call,timeOutCall,DEFAULT_TIMEOUT);
	}
	
	public CallBackBind(Consumer<T> call, Runnable timeOutCall,long timeOut) {
		super();
		Objects.requireNonNull(call);
		Objects.requireNonNull(timeOutCall);
		this.call = call;
		this.timeOutCall = timeOutCall;
		this.timeOut = timeOut;
	}

	@Override
	public void call(T result) {
		call.accept(result);
	}

	@Override
	public long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	@Override
	public void timeOutCall() {
		timeOutCall.run();
	}
}
