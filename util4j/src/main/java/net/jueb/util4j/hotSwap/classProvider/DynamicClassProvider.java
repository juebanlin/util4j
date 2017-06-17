package net.jueb.util4j.hotSwap.classProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.hotSwap.classSources.ClassSource;
import net.jueb.util4j.hotSwap.classSources.ClassSource.ClassSourceInfo;

/**
 * 动态类生产
 * @author juebanlin
 */
public class DynamicClassProvider {

	protected final Logger _log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 类资源
	 */
	protected final ClassSource classSource;
	/**
	 * 是否自动重载变更代码
	 */
	protected volatile boolean autoReload;
	private final ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock();
	private final Set<EventListener> listeners=new HashSet<>();
	
	protected ClassLoader classLoader;
	
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
	
	protected volatile State state = State.ready;

	public DynamicClassProvider(ClassSource classSource) {
		this(classSource, true);
	}

	public DynamicClassProvider(ClassSource classSource, boolean autoReload) {
		this.classSource = classSource;
		this.autoReload = autoReload;
		init();
	}
	
	private boolean disableReload;
	
	private void init() {
		try {
			classSource.addEventListener(this::onClassSourceScaned);
			loadClasses();
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	protected void onClassSourceScaned()
	{
		if(isAutoReload())
		{
			reload();
		}
	}
	
	
	@FunctionalInterface
	public static interface EventListener{
		/**
		 * 加载完成
		 */
		public void onLoaded(Set<Class<?>> classes);
	}
	
	/**
	 * 加载所有的脚本类
	 * @throws Exception
	 */
	protected final void loadClasses() throws Exception 
	{
		if (state == State.loading) 
		{
			return;
		}
		rwLock.writeLock().lock();
		try {
			state = State.loading;
			DynamicClassLoader newClassLoader = new DynamicClassLoader();
			Set<Class<?>> classes=loadClasses(classSource,newClassLoader);
			newClassLoader.close();//关闭资源文件引用
			classes=Collections.unmodifiableSet(classes);
			this.classLoader = newClassLoader;
			onLoaded(classes);
			for(EventListener listener:listeners)
			{
				try {
					listener.onLoaded(classes);
				} catch (Throwable e) {
				}
			}
		} finally {
			state = State.loaded;
			rwLock.writeLock().unlock();
		}
	}
	
	/**
	 * 使用loader加载所有class
	 * @return
	 * @throws Exception
	 */
	protected Set<Class<?>> loadClasses(ClassSource soruce,DynamicClassLoader newClassLoader) throws Exception 
	{
		Set<Class<?>> allClass = new HashSet<>();
		List<ClassSourceInfo> sources=soruce.getClassSources();
		if(sources.isEmpty())
		{
			return allClass;
		}
		for(ClassSourceInfo cs:sources)
		{
			newClassLoader.addURL(cs.getUrl());
			for(String className:cs.getClassNames())
			{
				Class<?> clazz=newClassLoader.loadClass(className);
				if(clazz!=null)
				{
					allClass.add(clazz);
				}
			}
		}
		_log.debug("classloader init complete,find Class:"+allClass.size());
		return allClass;
	}

	protected void onClassLoaded(Class<?> clazz)
	{
		
	}

	public final State getState() {
		return state;
	}
	
	public ClassLoader getClassLoader()
	{
		rwLock.readLock().lock();
		try {
			return classLoader;
		} finally {
			rwLock.readLock().unlock();
		}
	}

	public final void reload() {
		if(disableReload)
		{//脚本源已经删除
			_log.error("disableReload="+disableReload);
			return ;
		}
		try {
			loadClasses();
		} catch (Throwable e) {
			_log.error(e.getMessage(), e);
		}
	}
	
	public final void addListener(EventListener listener)
	{
		rwLock.writeLock().lock();
		try {
			listeners.add(listener);
		} finally {
			rwLock.writeLock().unlock();
		}
	}
	
	public final void removeListener(EventListener listener)
	{
		rwLock.writeLock().lock();
		try {
			listeners.remove(listener);
		} finally {
			rwLock.writeLock().unlock();
		}
	}
	
	public boolean isAutoReload() {
		return autoReload;
	}

	public void setAutoReload(boolean autoReload) {
		this.autoReload = autoReload;
	}
	
	protected void onLoaded(Set<Class<?>> classes)
	{
		
	}
}
