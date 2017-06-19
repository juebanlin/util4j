package net.jueb.util4j.hotSwap.classFactory;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.jueb.util4j.hotSwap.classProvider.DynamicClassProvider;
import net.jueb.util4j.hotSwap.classProvider.DynamicClassProvider.State;
import net.jueb.util4j.hotSwap.classSources.ClassSource;

/**
 * 动态加载jar内的脚本,支持包含匿名内部类 T不能做为父类加载 T尽量为接口类型,
 * 因为只有接口类型的类才没有逻辑,才可以不热加载,并且子类可选择实现.
 * 此类提供的脚本最好不要长期保持引用,由其是热重载后,原来的脚本要GC必须保证引用不存在
 * 通过监听脚本源实现代码的加载
 */
public abstract class ScriptClassProvider<T extends IScript> extends StaticScriptClassFactory<T> {

	/**
	 * 脚本库目录
	 */
	protected final DynamicClassProvider classProvider;

	/**
	 * 是否自动重载变更代码
	 */
	protected volatile boolean autoReload;
	
	protected final Map<Integer, Class<? extends T>> codeMap = new ConcurrentHashMap<Integer, Class<? extends T>>();
	private final ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock();

	protected ScriptClassProvider(ClassSource classSource,boolean autoReload) {
		this(new DynamicClassProvider(classSource,autoReload));
	}

	protected ScriptClassProvider(DynamicClassProvider classProvider) {
		this.classProvider=classProvider;
		init();
	}
	
	private void init() {
		reload();//主动加载
		classProvider.addListener(this::onLoaded);//被动加载监听器
	}

	/**
	 * 加载完成
	 */
	protected void onLoaded()
	{
		try {
			loadClasses();
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
	}


	protected final void loadClasses()throws Exception
	{
		rwLock.writeLock().lock();
		try {
			Set<Class<?>> classes=classProvider.getLoadedClasses();
			Set<Class<? extends T>> scriptClass=findScriptClass(classes);
			Map<Integer, Class<? extends T>> newCodeMap = findInstanceAbleScript(scriptClass);
			this.codeMap.clear();
			this.codeMap.putAll(newCodeMap);
		} finally {
			rwLock.writeLock().unlock();
		}
	}
	
	/**
	 * 找出脚本类
	 * @param clazzs
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	private Set<Class<? extends T>> findScriptClass(Set<Class<?>> clazzs)
			throws InstantiationException, IllegalAccessException {
		Set<Class<? extends T>> scriptClazzs = new HashSet<Class<? extends T>>();
		for (Class<?> clazz : clazzs) {
			if (IScript.class.isAssignableFrom(clazz)) {
				Class<T> scriptClazz = (Class<T>) clazz;
				if(acceptClass(scriptClazz))
				{
					scriptClazzs.add(scriptClazz);
				}
			}
		}
		return scriptClazzs;
	}
	
	/**
	 * 是否接受此类
	 * @param clazz
	 * @return
	 */
	protected boolean acceptClass(Class<T> clazz)
	{
		return true;
	}

	/**
	 * 查找可实例化的脚本
	 * @param scriptClazzs
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private Map<Integer, Class<? extends T>> findInstanceAbleScript(Set<Class<? extends T>> scriptClazzs)
			throws InstantiationException, IllegalAccessException {
		Map<Integer, Class<? extends T>> codeMap = new ConcurrentHashMap<Integer, Class<? extends T>>();
		for (Class<? extends T> scriptClazz : scriptClazzs) 
		{
			boolean isAbstractOrInterface = Modifier.isAbstract(scriptClazz.getModifiers())|| Modifier.isInterface(scriptClazz.getModifiers());// 是否是抽象类
			if (!isAbstractOrInterface) {// 可实例化脚本
				T script = null;
				try {
					script=scriptClazz.newInstance();
				} catch (Exception e) {
					_log.error("can't newInstance ScriptClass:" + scriptClazz);
					continue;
				}
				int code = script.getMessageCode();
				if (codeMap.containsKey(script.getMessageCode())) {// 重复脚本code定义
					_log.error("find Repeat ScriptClass,code="+code+",addingScript:" + script.getClass() + ",existScript:"
							+ codeMap.get(code));
				} else {
					codeMap.put(code, scriptClazz);
					_log.info("regist ScriptClass:code="+code+",class=" + scriptClazz);
				}
			}
		}
		return codeMap;
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
		return classProvider.getState();
	}

	private Class<? extends T> getClass(int code)
	{
		Class<? extends T> c = getStaticScriptClass(code);
		if(c==null)
		{
			c = getScriptClass(code);
		}
		return c;
	}
	
	public final T buildInstance(int code) {
		T result=null;
		Class<? extends T> c = getClass(code);
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
	public final T buildInstance(int code, Object... args) {
		T result=null;
		Class<? extends T> c = getClass(code);
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
			loadClasses();
		} catch (Throwable e) {
			_log.error(e.getMessage(), e);
		}
	}
	
	public final boolean hasCode(int code)
	{
		return getClass(code)!=null;
	}
	
	public boolean isAutoReload() {
		return autoReload;
	}

	public void setAutoReload(boolean autoReload) {
		this.autoReload = autoReload;
	}
}