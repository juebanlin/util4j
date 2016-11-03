package net.jueb.util4j.cache.callBack.impl;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.cache.callBack.AnnotationCallBack;

/**
 * 注解回调实现
 * @author Administrator
 * @param <T>
 */
public class AnnotationCallBackImpl<T> implements AnnotationCallBack<T>{
	protected Logger _log = LoggerFactory.getLogger(this.getClass());
	private Object callTarget;
	private String functionName;
	private String timeOutfunctionName;
	private boolean byId;
	private int functionId;
	private int timeOutFunctionId;
	private long timeOut=DEFAULT_TIMEOUT*3;
	
	public AnnotationCallBackImpl(Object callTarget,String functionName,String timeOutfunctionName) {
		if(callTarget==null)
		{
			throw new NullPointerException("callTarget is null");
		}
		this.byId=false;
		this.callTarget=callTarget;
		this.functionName=functionName;
		this.timeOutfunctionName=timeOutfunctionName;
	}
	
	public AnnotationCallBackImpl(Object callTarget,int functionId,int timeOutFunctionId) {
		if(callTarget==null)
		{
			throw new NullPointerException("callTarget is null");
		}
		this.byId=true;
		this.callTarget=callTarget;
		this.functionId=functionId;
		this.timeOutFunctionId=timeOutFunctionId;
	}
	
	/**
	 * 匹配方法
	 * @param obj
	 * @param name
	 * @return
	 */
	protected Method findMethod(Object obj,String name)
	{
		Method method=null;
		if(callTarget!=null)
		{
			Method[] methods=callTarget.getClass().getDeclaredMethods();
			for(Method m:methods)
			{
				CallBackFunction function=m.getAnnotation(CallBackFunction.class);
				if(function!=null && function.name()!=null && function.name().equals(name))
				{
					method=m;
					break;
				}
			}
		}
		return method;
	}
	/**
	 * 根据注解ID匹配
	 * @param obj
	 * @param functionId
	 * @return
	 */
	protected Method findMethod(Object obj,int functionId)
	{
		Method method=null;
		if(callTarget!=null)
		{
			Method[] methods=callTarget.getClass().getDeclaredMethods();
			for(Method m:methods)
			{
				CallBackFunction function=m.getAnnotation(CallBackFunction.class);
				if(function!=null && function.id()==functionId)
				{
					method=m;
					break;
				}
			}
		}
		return method;
	}
	
	public void setTimeOut(long timeOut) {
		this.timeOut=timeOut;
	}

	@Override
	public long getTimeOut() {
		return timeOut;
	}

	@Override
	public void call(T result) {
		Method method=null;
		if(byId)
		{
			method=findMethod(callTarget,functionId);
		}else
		{
			method=findMethod(callTarget,functionName);
		}
		if(method!=null)
		{
			try {
				method.setAccessible(true);
				method.invoke(callTarget,result);
			} catch (Exception e) {
				_log.error("实例方法调用异常:"+result,e);
			}
		}
	}

	@Override
	public void timeOutCall() {
		Method method=null;
		if(byId)
		{
			method=findMethod(callTarget,timeOutFunctionId);
		}else
		{
			method=findMethod(callTarget, timeOutfunctionName);
		}
		if(method!=null)
		{
			try {
				method.setAccessible(true);
				method.invoke(callTarget);
			} catch (Exception e) {
				_log.error(e.getMessage(),e);
			}
		}
	}

	@Override
	public Object getCallTarget() {
		return callTarget;
	}

	@Override
	public String getFunctionName() {
		return functionName;
	}

	@Override
	public String getTimeOutFunctionName() {
		return timeOutfunctionName;
	}
}
