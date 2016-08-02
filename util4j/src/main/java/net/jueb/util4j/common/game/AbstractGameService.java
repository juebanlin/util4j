package net.jueb.util4j.common.game;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGameService implements GameService{
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private  class ThreadHolder implements Runnable 
	{
		private boolean isRun;
		public void setRun(boolean isRun) {
			this.isRun = isRun;
		}
		@Override
		public void run() {
			while(isRun)
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
					log.error(e.getMessage(),e);
				}
			}
		}
	}
	
	{
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				log.info("jvm进程关闭");
				doJvmClose();
			}
		}));
	}
	
	/**
	 * 执行启动服务逻辑
	 */
	protected abstract void doStart()throws Throwable;
	/**
	 * 执行关闭服务逻辑
	 */
	protected abstract void doClose()throws Throwable;
	/**
	 * 是否后台
	 */
	protected boolean daemon;
	
	protected boolean isDaemon() {
		return daemon;
	}
	protected void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}
	/**
	 * 非后台进程防止jvm退出
	 */
	private final ThreadHolder threadHolder=new ThreadHolder();
	
	protected void daemonInit()
	{
		threadHolder.setRun(true);
		Thread t=new Thread(threadHolder);
		t.setDaemon(false);
		t.setName("GameService-ThreadHolder");
		t.start();
	}
	
	protected void daemonStop()
	{
		threadHolder.setRun(false);
	}
	
	public void startService(){
		switch (getState()) {
		case Stoped:
			setState(ServiceState.Starting);
			try {
				doStart();
				setState(ServiceState.Active);
				daemonInit();
			} catch (Throwable e) {
				log.error(e.getMessage(),e);
				e.printStackTrace();
				setState(ServiceState.Stoped);
			}
			break;
		case Starting:
		case Active:
			break;
		case Stoping:
			break;
		default:
			break;
		}
	}
	
	@Override
	public void closeService() {
		switch (getState()) {
		case Stoped:
			break;
		case Starting:
		case Active:
			setState(ServiceState.Stoping);
			try {
				doClose();
				setState(ServiceState.Stoped);
				daemonStop();
			} catch (Throwable e) {
				log.error(e.getMessage(),e);
				e.printStackTrace();
				setState(ServiceState.Active);
			}
			break;
		case Stoping:
			break;
		default:
			break;
		}
	}
	
	@Override
	public ServiceState getState() {
		return state;
	}
	
	private ServiceState state=ServiceState.Stoped;
	
	private void setState(ServiceState state)
	{
		this.state=state;
	}
	
	private final Map<String,Object> attributes=new HashMap<String,Object>();
	public boolean hasAttribute(String key) {
		return attributes.containsKey(key);
	}

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public Object removeAttribute(String key) {
		return attributes.remove(key);
	}

	public void clearAttributes() {
		attributes.clear();
	}
	
	/**
	 * 执行jvm关闭
	 */
	protected void doJvmClose()
	{
		closeService();
	}
}
