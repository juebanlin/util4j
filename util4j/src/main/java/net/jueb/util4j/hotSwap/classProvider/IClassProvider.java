package net.jueb.util4j.hotSwap.classProvider;

import java.util.Set;

/**
 * 类提供者
 * @author juebanlin
 */
public interface IClassProvider {
	
	@FunctionalInterface
	public static interface EventListener{
		/**
		 * 加载完成
		 */
		public void onLoaded();
	}

	public static enum State {
		/**
		 * 脚本未加载
		 */
		ready,
		/**
		 * 脚本加载中
		 */
		loading,
		/**
		 * 脚本加载完成
		 */
		loaded,
	}
	
	public State getState();
	
	public ClassLoader getClassLoader();
	
	public Set<Class<?>> getLoadedClasses();
	/**
	 * 重载class
	 * if(getClassLoader()==getClass().getClassLoader())
	 * 注意,调用此方法的实例的classLoader不能等于当前的classLoader,
	 * 否则会造成classLoader内存泄漏
	 */
	public void reload();
	
	public void addListener(EventListener listener);
	
	public void removeListener(EventListener listener);
	
	public boolean isAutoReload();
	
	public void setAutoReload(boolean autoReload);
}
