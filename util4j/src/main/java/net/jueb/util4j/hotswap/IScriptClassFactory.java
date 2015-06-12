/**
 * 
 */
package net.jueb.util4j.hotswap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Map.Entry;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangyd 动态加载类 T不能做为父类加载
 */

public abstract class IScriptClassFactory<T extends IScript> {
	protected final Logger _log = LoggerFactory.getLogger(this.getClass());
	
	protected ClassFile allParent;
	protected final ConcurrentHashMap<String,ClassFile> scriptFilePaths=new ConcurrentHashMap<String,ClassFile>();
	protected final ConcurrentHashMap<Integer, Class<T>> codeMap=new ConcurrentHashMap<Integer, Class<T>>();
	protected boolean isLoading;
	
	/**
	 * 文件监测间隔时间
	 */
	protected long intervalMillis=TimeUnit.SECONDS.toMillis(10);
	
	protected static final ScheduledExecutorService schedule=Executors.newScheduledThreadPool(1, new NamedThreadFactory("ScriptFactoryMonitor",true));

	protected ScriptClassLoader classLoader;
	
	
	/**
	 * 注册全局父类
	 * @param name
	 */
	protected final void registAllParent(String className) {
		ClassFile cf=getClassFile(className);
		if(cf!=null)
		{
			allParent=cf;
			_log.debug("registallParentClass:"+allParent.getFilePath());
		}
	}
	
	/**
	 * 注册全局父类
	 */
	protected final void registAllParent(Class<? extends T> t) {
		String className=t.getClass().getName();
		registAllParent(className);
	}
	
	protected boolean validClassFilePath(String path)
	{
		if(path==null)
		{
			return false;
		}
		File file=new File(path);
		return file.exists()&&file.isFile();
	}
	
	/**
	 *	注册类
	 * @param script 脚本类
	 * @param parent 依次父类
	 */
	protected final void registClass(String className,String ...parents) {
		ClassFile cf = getClassFile(className);
		if(cf!=null)
		{
			scriptFilePaths.put(cf.getFilePath(), cf);
			_log.info("registClass:"+cf.getFilePath());
			ClassFile subClass=cf;//子类
			for(int i=0;i<parents.length;i++)
			{
				String parentClassName=parents[i];
				ClassFile parentCf=getClassFile(parentClassName);
				subClass.setParent(parentCf);//设置子类
				if(!scriptFilePaths.containsKey(parentCf.getFilePath()))
				{
					scriptFilePaths.put(parentCf.getFilePath(),parentCf);
					_log.info("registParenClass:"+parentCf.getClassName());
				}
				subClass=parentCf;//更新子类为当前父类
			}
		}
	}
	
	/**
	 * 注册类
	 * @param script 脚本类
	 * @param parent 依次父类
	 */
	@SafeVarargs
	protected final void registClass(Class<? extends T> script,Class<? extends T> ...parent) {
		if(script!=null)
		{
			String className=script.getName();
			String[] parents=new String[parent.length];
			for(int i=0;i<parent.length;i++)
			{
				Class<? extends T> pt=parent[i];
				parents[i]=pt.getName();
			}
			registClass(className,parents);
		}
	}
	
	
	/**
	 * 获取class文件根目录
	 * @return
	 */
	protected abstract String getClassRootDir();

	/**
	 * 路径解析
	 * @param className
	 * @return
	 */
	protected final ClassFile getClassFile(String className)
	{
		ClassFile cf=null;
		String filePath=null;
		if(className!=null && !className.isEmpty())
		{
			String rootPath=getClassRootDir();
			if(rootPath.endsWith("/")||rootPath.endsWith("\\"))
			{
			}else
			{
				rootPath=rootPath+File.separator;
			}
			filePath=rootPath+className.replace('.', File.separatorChar)+".class";
		}
		File file=new File(filePath);
		if(file.exists() && file.isFile())
		{
			cf=new ClassFile(className, file.getPath());
			cf.setLastModifyTime(file.lastModified());
		}
		return cf;
	}

	protected final T build(int code, Class<?>[] cs, Object[] initargs) {
		T result=null;
		Class<T> c = codeMap.get(code);
		if(c==null)
		{
			_log.error("nor found code=" + Integer.toHexString(code));
		}else
		{
			try {
				result= c.getConstructor(cs).newInstance(initargs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 根据小消息头构建
	 * @param code
	 * @return
	 */
	protected final T build(int code) {
		T result=null;
		Class<T> c = codeMap.get(code);
		if(c==null)
		{
			_log.error("nor found script,code=" + code);
		}else
		{
			try {
				result= c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	// 加载所有的类
	@SuppressWarnings("unchecked")
	public final void loadAllClass(){
		isLoading=true;
		classLoader = new ScriptClassLoader();
		if(allParent!=null)
		{
			try {//异常捕捉,不中断后面的执行
				Class<?> allParentClazz = classLoader.findClass(allParent.getClassName(),getFileBytes(allParent));
				_log.info("loadAllParent class:" + allParentClazz.getName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		final ConcurrentHashMap<Integer, Class<T>> codeMap=new ConcurrentHashMap<Integer, Class<T>>();
		for (Entry<String,ClassFile> entry : scriptFilePaths.entrySet()) {
			ClassFile classFile = entry.getValue();
			try {
				Class<T> scriptClass=null;
				Class<?> clazz=loadClass(classLoader, classFile);//共同的父类
				if(clazz!=null)
				{//类型转换
					scriptClass=(Class<T>)clazz;
				}
				if(scriptClass!=null)
				{
					boolean isAbstract=Modifier.isAbstract(scriptClass.getModifiers());//是否是抽象类
					if(isAbstract)
					{//抽象类
						_log.info("loaded abstractScript:" + classFile.getFilePath());
					}else
					{
						T t=scriptClass.newInstance();
						codeMap.put(t.getMessageCode(),scriptClass);
						_log.info("loaded codeScript:" + classFile.getFilePath());
					}
					classFile.setLastModifyTime(new File(classFile.getFilePath()).lastModified());//更新时间
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.codeMap.putAll(codeMap);
		isLoading=false;
	}
	
	/**
	 * @param dc
	 * @param classFile
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	protected final Class<?> loadClass(ScriptClassLoader dc,ClassFile classFile) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		Class<?> clazz=null;
		if(classFile!=null)
		{
			ClassFile parent=classFile.getParent();
			if(parent!=null)
			{//返回加载父类
				loadClass(dc, parent);
				_log.info("loadParentClass:"+classFile.getClassName()+"==>"+parent.getClassName());
			}
			boolean isLoaded=dc.hasLoaded(classFile.getClassName());
			if(!isLoaded)
			{
				clazz=dc.findClass(classFile.getClassName(),getFileBytes(classFile));
			}else
			{
				clazz=dc.loadClass(classFile.getClassName());
			}
		}
		return clazz;
	}
	protected void start()
	{
		try {
			loadAllClass();
			schedule.scheduleWithFixedDelay(new ScriptMonitorTask(),0, intervalMillis, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 从本地读取文件
	@SuppressWarnings("resource")
	protected byte[] getFileBytes(ClassFile classFile) throws IOException {
		File file = new File(classFile.getFilePath());
		long len = file.length();
		byte raw[] = new byte[(int) len];
		FileInputStream fin = new FileInputStream(file);
		int r = fin.read(raw);
		if (r != len) {
			throw new IOException("Can't read file " + classFile.getFilePath() + " all, " + r + " != " + len);
		}
		fin.close();
		return raw;
	}

	class ScriptClassLoader extends ClassLoader {
		
		private final Set<String> loadedClass=new HashSet<String>();
		
		public Class<?> findClass(String className,byte[] b) throws ClassNotFoundException {
			Class<?> clazz=defineClass(className, b, 0, b.length);
			if(clazz!=null)
			{
				loadedClass.add(clazz.getName());
			}
			return clazz;
		}
		public boolean hasLoaded(String className)
		{
			return loadedClass.contains(className);
		}
	}
	class ClassFile {

		private final String className;
		private ClassFile parent;//上级父类
		private final String filePath;
		private long lastModifyTime;
		public ClassFile(String className,String path) {
			this.className=className;
			this.filePath = path;
		}

		public String getClassName() {
			return className;
		}

		public String getFilePath() {
			return filePath;
		}

		public ClassFile getParent() {
			return parent;
		}

		public void setParent(ClassFile parent) {
			this.parent = parent;
		}
		
		public long getLastModifyTime() {
			return lastModifyTime;
		}

		public void setLastModifyTime(long lastModifyTime) {
			this.lastModifyTime = lastModifyTime;
		}

		@Override
		public String toString() {
			return "ClassFile [className=" + className + ", parent=" + parent
					+ ", filePath=" + filePath + "]";
		}
	}
	
	/**
	 * 脚本监视任务
	 * @author Administrator
	 */
	class ScriptMonitorTask implements Runnable{

		@Override
		public void run() {
			try {
				if(isLoading)
				{
					return;
				}
				boolean reload=false;
				for (Entry<String,ClassFile> entry : scriptFilePaths.entrySet()) {
					ClassFile classFile = entry.getValue();
					File file = new File(classFile.getFilePath());
					if (file.lastModified() > classFile.getLastModifyTime()) {
						reload = true;
						break;
					}
				}
				if(allParent!=null)
				{
					File allParentFile = new File(allParent.getFilePath());
					if (allParentFile.lastModified() > allParent.getLastModifyTime()) {
						reload = true;
					}
				}
				if(reload && !isLoading)
				{
					_log.debug("reload allClass……");
					loadAllClass();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
