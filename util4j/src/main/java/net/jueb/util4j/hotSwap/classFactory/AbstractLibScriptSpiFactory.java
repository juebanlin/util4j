package net.jueb.util4j.hotSwap.classFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.file.FileUtil;
import net.jueb.util4j.thread.NamedThreadFactory;

/**
 * 动态加载jar内的脚本,支持包含匿名内部类 T不能做为父类加载 T尽量为接口类型,因为只有接口类型的类才没有逻辑,才可以不热加载,并且子类可选择实现
 */
public abstract class AbstractLibScriptSpiFactory<T extends IScript> extends AbstractStaticScriptFactory<T> {
	protected final Logger _log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 脚本库目录
	 */
	protected final String scriptLibDir;
	protected ScriptClassLoader classLoader;

	/**
	 * 是否自动重载变更代码
	 */
	protected volatile boolean autoReload;

	/**
	 * 记录加载的jar文件和时间
	 */
	protected final Map<String, RecordFile> loadedRecord = new ConcurrentHashMap<String, RecordFile>();
	protected final Map<Integer, Class<? extends T>> codeMap = new ConcurrentHashMap<Integer, Class<? extends T>>();
	private final ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock();
	
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

	/**
	 * 文件监测间隔时间
	 */
	protected long intervalMillis = TimeUnit.SECONDS.toMillis(10);
	protected static final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1,
			new NamedThreadFactory("ScriptFactoryMonitor", true));

	protected AbstractLibScriptSpiFactory(String scriptLibDir) {
		this(scriptLibDir, true);
	}

	protected AbstractLibScriptSpiFactory(String scriptLibDir, boolean autoReload) {
		this.scriptLibDir = scriptLibDir;
		this.autoReload = autoReload;
		init();
	}

	/**
	 * 寻找jar文件
	 * 
	 * @param scriptLibDir
	 * @return
	 */
	protected List<File> findJarFile(String scriptLibDir) {
		List<File> files = new ArrayList<File>();
		try {
			File file = new File(scriptLibDir);
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					if (f.isFile() && f.getName().endsWith(".jar")) {
						files.add(f);
					}
				}
			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
		return files;
	}

	private void init() {
		try {
			loadAllClass();
			schedule.scheduleWithFixedDelay(new ScriptMonitorTask(), 0, intervalMillis, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}

	/**
	 * 获取class文件根目录
	 * 
	 * @return
	 */
	protected final String getClassRootDir() {
		return scriptLibDir;
	}

	/**
	 * 遍历配置的目录的jar文件并加载class
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public final void loadAllClass() throws Exception {
		loadAllClassByDir(scriptLibDir);
	}

	/**
	 * 遍历目录所有jar并加载class
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	protected final void loadAllClassByDir(String scriptLibDir) throws Exception {
		List<File> jarFiles = findJarFile(scriptLibDir);
		loadAllClass(jarFiles);
	}

	private final AtomicInteger ato=new AtomicInteger();
	
	private List<File> copyToTmpDir(List<File> files) throws IOException
	{
		List<File> tmpFiles=new ArrayList<>();
		File tmpDir=null;
		for(File f:files)
		{
			if(f.exists() && f.isFile())
			{
				if(tmpDir==null)
				{
					tmpDir=FileUtil.createTmpDir("scriptFactoryTmp_"+ato.getAndIncrement());
					if(tmpDir.exists())
					{
						tmpDir.delete();
						tmpDir.mkdir();
					}
				}
				File newFile=new File(tmpDir,f.getName());
				_log.debug("copyFile "+f.getPath()+" to:"+newFile.getPath());
				Files.copy(f.toPath(), newFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES,StandardCopyOption.REPLACE_EXISTING);
				tmpFiles.add(newFile);
			}
		}
		_log.debug("copyFiles to tmpDir:"+tmpDir);
		return tmpFiles;
	}
	
	/**
	 * 查找文件中T接口的实现类
	 * @param interfaceClass
	 * @param files
	 * @param classLoader
	 * @return
	 * @throws MalformedURLException
	 */
	protected final <S> Set<S> loadAllScript(Class<S> interfaceClass,List<File> files,ScriptClassLoader classLoader) throws MalformedURLException
	{
		Set<S> allScriptClass = new HashSet<S>();
		for(File file:files)
		{
			classLoader.addURL(file.toURI().toURL());
		}
		ServiceLoader<S> load=ServiceLoader.load(interfaceClass, classLoader);
		Iterator<S> it=load.iterator();
		while(it.hasNext())
		{
			allScriptClass.add(it.next());
		}
		return allScriptClass;
	}
	
	/**
	 * 加载所有的类
	 * @param jarFiles
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	protected final void loadAllClass(List<File> jarFiles) throws Exception 
	{
		if (state == State.loading) 
		{
			return;
		}
		rwLock.writeLock().lock();
		try {
			state = State.loading;
			//标记更新时间
			Map<String, RecordFile> newLoadedRecord = new HashMap<String, RecordFile>();
			for (File file : jarFiles) 
			{
				//记录文件变化,使用源文件的修改时间
				RecordFile rf = new RecordFile(file.getPath());
				rf.setLastModifyTime(file.lastModified());
				newLoadedRecord.put(rf.getFilePath(), rf);
			}
			//复制文件到临时目录并找出脚本类,因为urlClassLoader加载后会占用文件,导致后续无法删除scriptLibDir下的文件
			List<File> waitLoadFiles=copyToTmpDir(jarFiles);
			ScriptClassLoader newClassLoader = new ScriptClassLoader();
			Set<IScript> allScript=loadAllScript(IScript.class,waitLoadFiles,newClassLoader);
			//保存脚本实现类的class
			Map<Integer, Class<? extends T>> newCodeMap=new HashMap<>();
			for(IScript script:allScript)
			{
				int code = script.getMessageCode();
				if (codeMap.containsKey(script.getMessageCode())) 
				{// 重复脚本code定义
					_log.error("find Repeat CodeScriptClass,code="+code+",addingScript:" + script.getClass() + ",existScript:"
							+ codeMap.get(code));
				} else 
				{
					@SuppressWarnings("unchecked")
					Class<? extends T> clazz=(Class<? extends T>) script.getClass();
					codeMap.put(code, clazz);
					_log.info("loaded CodeScriptClass:code="+code+",class=" + clazz);
				}
			}
			this.codeMap.clear();
			this.loadedRecord.clear();
			this.codeMap.putAll(newCodeMap);
			this.loadedRecord.putAll(newLoadedRecord);
			this.classLoader = newClassLoader;
		} finally {
			state = State.loaded;
			rwLock.writeLock().unlock();
		}
	}

	class ScriptClassLoader extends URLClassLoader {
		protected Logger log = LoggerFactory.getLogger(getClass());

		public ScriptClassLoader(URL[] urls) {
			super(urls, Thread.currentThread().getContextClassLoader());
		}

		public ScriptClassLoader(URL url) {
			this(new URL[] { url });
		}

		public ScriptClassLoader() {
			this(new URL[] {});
		}

		/**
		 * 加载类,如果是系统类则交给系统加载 如果当前类已经加载则返回类 如果当前类没有加载则定义并返回
		 */
		@Override
		protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
			Class<?> clazz = null;
			// 查找当前类加载中已加载的
			if (clazz == null) {
				clazz = findLoadedClass(className);
			}
			// 当前jar中加载
			if (clazz == null) {
				// 查找当前类加载器urls或者当前类加载器所属线程类加载器
				try {
					clazz = findClass(className);
				} catch (Exception e) {
				}
			}
			if (clazz == null) {// 系统类加载
				try {
					clazz = findSystemClass(className);
				} catch (Exception e) {

				}
			}
			String ClassLoader = null;
			if (clazz != null) {// 解析类结构
				ClassLoader = "" + clazz.getClassLoader();
				if (resolve) {
					resolveClass(clazz);
				}
			}
			log.debug("loadClass:" + className + ",resolve=" + resolve + ",Clazz=" + clazz + ",ClassLoader="+ ClassLoader);
			return clazz;
		}

		/**
		 * 查找类,这个方法一般多用于依赖类的查找,如果之前已经加载过,则重复加载会报错,所以需要添加findLoadedClass判断
		 */
		@Override
		protected Class<?> findClass(final String name) throws ClassNotFoundException {
			log.debug("findClass:"+name);
			Class<?> clazz=findLoadedClass(name);
			if (clazz != null) 
			{
				return clazz;
			}
			return super.findClass(name);
		}

		@Override
		protected void addURL(URL url) {
			super.addURL(url);
		}
	}

	/**
	 * jar记录
	 * 
	 * @author juebanlin
	 */
	class RecordFile {

		private final String filePath;
		private long lastModifyTime;

		public RecordFile(String path) {
			this.filePath = path;
		}

		public String getFilePath() {
			return filePath;
		}

		public long getLastModifyTime() {
			return lastModifyTime;
		}

		public void setLastModifyTime(long lastModifyTime) {
			this.lastModifyTime = lastModifyTime;
		}

		/**
		 * 是否存在
		 * 
		 * @return
		 */
		public boolean isExist() {
			File f = new File(filePath);
			return f.exists();
		}

		@Override
		public String toString() {
			return "JarFile [filePath=" + filePath + ", lastModifyTime=" + lastModifyTime + "]";
		}
	}

	private boolean hashChange()
	{
		rwLock.readLock().lock();
		boolean trigLoad = false;
		try {
			Map<String, File> files = new HashMap<String, File>();
			for (File file : findJarFile(scriptLibDir)) 
			{
				files.put(file.getPath(), file);
			}
			for (File file : files.values()) 
			{
				RecordFile rf = loadedRecord.get(file.getPath());
				if (rf == null) 
				{// 新增jar
					trigLoad = true;
					break;
				} else 
				{// 文件变动
					if (file.lastModified() > rf.getLastModifyTime()) 
					{
						trigLoad = true;
						break;
					}
				}
			}
			if (!trigLoad) 
			{//判断是否减少了jar
				for (String key : loadedRecord.keySet()) 
				{
					if (!files.containsKey(key)) 
					{//减少jar
						trigLoad = true;
						break;
					}
				}
			}
		} catch(Exception e){
			_log.error(e.getMessage(),e);
		}
		finally {
			rwLock.readLock().unlock();
		}
		return trigLoad;
	}
	
	
	/**
	 * jar目录监控任务,发生jar新增或者改变,则重置jarFiles并执行加载class
	 * 
	 * @author Administrator
	 */
	class ScriptMonitorTask implements Runnable {

		public void run() {
			if (!autoReload) {
				return;
			}
			if (state == State.loading) 
			{
				return;
			}
			if(hashChange())
			{
				_log.debug("trigger reload scriptLibs……");
				reload();
			}
		}
	}

	public boolean isAutoReload() {
		return autoReload;
	}

	public void setAutoReload(boolean autoReload) {
		this.autoReload = autoReload;
	}

	protected final Class<? extends T> getScriptClass(int code)
	{
		rwLock.readLock().lock();
		try {
			return codeMap.get(code);
		} finally {
			rwLock.readLock().unlock();
		}
	}

	public final State getState() {
		return state;
	}

	public final T buildInstance(int code) {
		T result=null;
		Class<? extends T> c = getStaticScriptClass(code);
		if(c==null)
		{
			c = getScriptClass(code);
		}
		if (c == null) 
		{
			_log.error("not found script,code=" + code + "(0x" + Integer.toHexString(code) + ")");
		} else 
		{
			result = newInstance(c);
		}
		return result;
	}
	
	@Override
	public T buildInstance(int code, Object... args) {
		T result=null;
		Class<? extends T> c = getStaticScriptClass(code);
		if(c==null)
		{
			c = getScriptClass(code);
		}
		if (c == null) 
		{
			_log.error("not found script,code=" + code + "(0x" + Integer.toHexString(code) + ")");
		} else 
		{
			result = newInstance(c,args);
		}
		return result;
	}

	public final void reload() {
		try {
			loadAllClass();
		} catch (Throwable e) {
			_log.error(e.getMessage(), e);
		}
	}
}