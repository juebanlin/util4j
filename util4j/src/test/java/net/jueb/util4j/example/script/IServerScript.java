package net.jueb.util4j.example.script;

import net.jueb.util4j.hotSwap.classFactory.generic.IGenericScript;
import net.jueb.util4j.net.JConnection;

/**
 * 服务器脚本
 * @author juebanlin@gmail.com
 * time:2015年6月17日
 */
public interface IServerScript extends IGenericScript,Runnable{

	public void setRequest(Request request);
	
	public Request getRequest();
	
	public void setRunMode(RunMode mode);
	
	public RunMode getRunMode();
	
	public Object[] getParams();
	
	@SuppressWarnings("unchecked")
	default public <T> T getParam(int index) {
		return (T) getParams()[index];
	}
	/**
	 * 如果不存在此索引对象则返回arg
	 * @param index
	 * @param arg
	 * @return
	 */
	default public <T> T getParamOrElse(int index,T arg)
	{
		T t=null;
		if(getParams()!=null && index>=0 && index<getParams().length)
		{
			t=getParam(index);
		}else
		{
			t=arg;
		}
		return t;
	}
	/**
	 * 如果不存在此索引对象则返回null
	 * @param index
	 * @return
	 */
	default public <T> T getParamOrNull(int index)
	{
		return getParamOrElse(index, null);
	}
	
	public void setParams(Object ...params);
	
	public void action();
	
	public void handleRequest(Request request);
	
	public static enum RunMode{
		/**
		 * 执行操作
		 */
		Action,
		/**
		 * 执行请求处理
		 */
		HandleRequest;
	}
	
	public static class Request {
		private JConnection connection;
		private Object content;

		public Request() {
			
		}

		public Request(JConnection connection, Object content) {
			super();
			this.connection = connection;
			this.content = content;
		}

		public JConnection getConnection() {
			return connection;
		}

		public void setConnection(JConnection connection) {
			this.connection = connection;
		}

		public Object getContent() {
			return content;
		}

		public void setContent(Object content) {
			this.content = content;
		}
	}
}
