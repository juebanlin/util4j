package net.jueb.util4j.hotSwap.classProvider;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.file.FileUtil;
import net.jueb.util4j.hotSwap.classSources.ClassSource;
import net.jueb.util4j.hotSwap.classSources.ClassSource.DirClassFile;
import net.jueb.util4j.hotSwap.classSources.ClassSource.URLClassFile;

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
			classSource.addEventListener((event->{
				switch (event) {
				case Change:
					disableReload=false;
					if(isAutoReload())
					{
						reload();
					}
					break;
				case Delete:
					disableReload=true;
					break;
				default:
					break;
				}
			}));
			loadClasses();
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
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
		Set<Class<?>> fileClass=new HashSet<>();
		for(DirClassFile dcf:classSource.getDirClassFiles())
		{
			File file=new File(dcf.getRootDir().getFile());
			if(!file.exists())
			{
				continue;
			}
			newClassLoader.addURL(dcf.getRootDir());
			for(String className:dcf.getClassNames())
			{
				Class<?> clazz=newClassLoader.loadClass(className);
				if(clazz!=null)
				{
					fileClass.add(clazz);
				}
			}
		}
		Set<Class<?>> urlClass=new HashSet<>();
		for(URLClassFile ucf:classSource.getUrlClassFiles())
		{
			newClassLoader.addURL(ucf.getURL());
			Class<?> clazz=newClassLoader.loadClass(ucf.getClassName());
			if(clazz!=null)
			{
				urlClass.add(clazz);
			}
		}
		Set<Class<?>> jarClass=new HashSet<>();
		for(URL jar:classSource.getJars())
		{
			JarFile jarFile=null;
			try {
				File file=new File(jar.getFile());
				if(!file.exists())
				{
					continue;
				}
				jarFile=new JarFile(jar.getFile());
				Map<String, JarEntry>  map=FileUtil.findClassByJar(jarFile);
				if(!map.isEmpty())
				{
					newClassLoader.addURL(jar);
					for(String className:map.keySet())
					{
						Class<?> clazz=newClassLoader.loadClass(className);
						if(clazz!=null)
						{
							jarClass.add(clazz);
						}
					}
				}
			} finally {
				if(jarFile!=null)
				{
					jarFile.close();
				}
			}
		}
		allClass.addAll(fileClass);
		allClass.addAll(urlClass);
		allClass.addAll(jarClass);
		_log.debug("classloader init complete,allClass:"+allClass.size()+",fileClass:" + fileClass.size() + ",urlClass:" + urlClass.size() + ",jarClass:" + jarClass.size());
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
