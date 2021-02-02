package net.jueb.util4j.hotSwap.classFactory.generic;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.hotSwap.classProvider.IClassProvider;
import net.jueb.util4j.hotSwap.classProvider.IClassProvider.State;

/**
 * 动态加载jar内的脚本,支持包含匿名内部类 T不能做为父类加载 T尽量为接口类型,
 * 因为只有接口类型的类才没有逻辑,才可以不热加载,并且子类可选择实现.
 * 此类提供的脚本最好不要长期保持引用,由其是热重载后,原来的脚本要GC必须保证引用不存在
 * 通过监听脚本源实现代码的加载
 */
public abstract class GenericScriptProvider<S extends IGenericScript> implements IGenericScriptFactory<S>{
	protected final Logger _log = LoggerFactory.getLogger(this.getClass());
	/**
	 * 脚本库目录
	 */
	protected final IClassProvider classProvider;

	/**
	 * 是否自动重载变更代码
	 */
	protected volatile boolean autoReload;
	
	private ClassData classData;
	
	
	protected GenericScriptProvider(IClassProvider classProvider) {
		Objects.requireNonNull(classProvider);
		this.classProvider=classProvider;
		init();
	}
	
	private void init() {
		reload();//主动加载
		classProvider.addListener(this::classProviderListener);//被动加载监听器
	}

	private class ClassData{
		final Map<Integer, Class<? extends S>> intMap = new HashMap<Integer, Class<? extends S>>();
		final Map<String, Class<? extends S>> stringMap = new HashMap<String, Class<? extends S>>();
	}
	
	protected class ClassRegister{
		final Map<Integer, Class<? extends S>> intMap = new HashMap<Integer, Class<? extends S>>();
		final Map<String, Class<? extends S>> stringMap = new HashMap<String, Class<? extends S>>();
		
		public void regist(int key,Class<? extends S> clazz)
		{
			Class<? extends S> old=intMap.getOrDefault(key,null);
			if(old!=null)
			{
				_log.error("find Repeat key ScriptClass,key="+key+",addingScript:" + clazz + ",existScript:"+ old);
			}
			intMap.put(key, clazz);
			_log.info("regist int mapping ScriptClass:key="+key+",class=" + clazz);
		}
		
		public void regist(String key,Class<? extends S> clazz)
		{
			Class<? extends S> old=stringMap.getOrDefault(key,null);
			if(old!=null)
			{
				_log.error("find Repeat name ScriptClass,key="+key+",addingScript:" + clazz + ",existScript:"+ old);
			}else
			{
				stringMap.put(key, clazz);
				_log.info("regist String mapping ScriptClass:key="+key+",class=" + clazz);
			}
		}
	}
	
	/**
	 * 加载完成
	 */
	private void classProviderListener()
	{
		try {
			load();
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 加载
	 * @throws Exception
	 */
	protected final void load()throws Exception
	{
		synchronized (this) {
			Set<Class<?>> classes=classProvider.getLoadedClasses();
			initScriptClasses(classes);
			onScriptLoaded(classes);
		}
	}
	
	/**
	 * 脚本类的加载不交给子类控制
	 * @param classes
	 * @throws Exception
	 */
	private void initScriptClasses(Set<Class<?>> classes)throws Exception
	{
		ClassRegister classRegister=new ClassRegister();
		for (Class<?> clazz : classes) {
			onClassFind(clazz, classRegister);
		}
		Set<Class<? extends S>> scriptClass=findScriptClass(classes);
		Set<Class<? extends S>> instanceAbleScript = findInstanceAbleScript(scriptClass);
		for(Class<? extends S> clazz:instanceAbleScript)
		{
			onScriptClassFind(clazz, classRegister);//交给子类处理
		}
		ClassData cd=new ClassData();
		cd.intMap.putAll(classRegister.intMap);
		cd.stringMap.putAll(classRegister.stringMap);
		_log.info("loadScriptClass complete,id mapping size:"+cd.intMap.size()+",name mapping size:"+cd.stringMap.size());
		this.classData=cd;
	}
	
	/**
	 * 找出脚本类
	 * @param clazzs
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	private Set<Class<? extends S>> findScriptClass(Set<Class<?>> clazzs)
			throws InstantiationException, IllegalAccessException {
		Set<Class<? extends S>> scriptClazzs = new HashSet<Class<? extends S>>();
		for (Class<?> clazz : clazzs) {
			if (isScriptClass(clazz)) {
				Class<S> scriptClazz = (Class<S>) clazz;
				scriptClazzs.add(scriptClazz);
			}
		}
		return scriptClazzs;
	}
	
	/**
	 * 是否是脚本类
	 * @param clazz
	 * @return
	 */
	protected boolean isScriptClass(Class<?> clazz)
	{
		return IGenericScript.class.isAssignableFrom(clazz);
	}
	
	/**
	 * 查找可实例化的脚本
	 * @param scriptClazzs
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	protected Set<Class<? extends S>> findInstanceAbleScript(Set<Class<? extends S>> scriptClazzs)
			throws InstantiationException, IllegalAccessException {
		Set<Class<? extends S>> result=new HashSet<>();
		for (Class<? extends S> scriptClazz : scriptClazzs) 
		{
			if(isInstanceAble(scriptClazz))
			{
				result.add(scriptClazz);
			}
		}
		return result;
	}
	
	protected boolean isInstanceAble(Class<? extends S> clazz)
	{
		return getInstacne(clazz)!=null;
	}

	protected boolean isAbstractOrInterface(Class<?> clazz)
	{
		return Modifier.isAbstract(clazz.getModifiers())|| Modifier.isInterface(clazz.getModifiers());// 是否是抽象类
	}
	
	protected <C> C getInstacne(Class<C> clazz)
	{
		C instacne=null;
		if (!isAbstractOrInterface(clazz)) {// 可实例化脚本
			try {
				instacne=clazz.newInstance();
			} catch (Exception e) {
				_log.error("can't newInstance Class:" + clazz,e);
			}
		}
		return instacne;
	}
	
	/**
	 * 发现类
	 * @param clazz 类型
	 * @param classRegister 注册器
	 */
	protected void onClassFind(Class<?> clazz ,ClassRegister classRegister) {
		
	}
	
	/**
	 * 发现可实例化脚本类
	 * @param clazz 类型
	 * @param classRegister 注册器
	 */
	protected abstract void onScriptClassFind(Class<? extends S> clazz ,ClassRegister classRegister);
	
	
	/**
	 * 当脚本加载完成后调用此方法,子类可继续过滤查找其它类
	 * @param classes 
	 */
	protected void onScriptLoaded(Set<Class<?>> loadedClasses)throws Exception
	{
		
	}

	public final boolean isAutoReload() {
		return autoReload;
	}

	public final void setAutoReload(boolean autoReload) {
		this.autoReload = autoReload;
	}

	public final State getState() {
		return classProvider.getState();
	}
	
	protected final Class<? extends S> getScriptClass(int id)
	{
		return classData.intMap.get(id);
	}
	
	protected final Class<? extends S> getScriptClass(String name)
	{
		return classData.stringMap.get(name);
	}
	
	protected final S newInstance(Class<? extends S> c,Object... args) {
		S result = null;
		try {
			Class<?>[] cs=new Class<?>[args.length];
			for(int i=0;i<args.length;i++)
			{
				cs[i]=args[i].getClass();
			}
			result = c.getConstructor(cs).newInstance(args);
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
		return result;
	}
	
	protected final S newInstance(Class<? extends S> c) {
		S result = null;
		try {
			result = c.newInstance();
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
		return result;
	}

	public S buildInstance(int id) {
		S result=null;
		Class<? extends S> c = getScriptClass(id);
		if (c == null) 
		{
			_log.error("not found script,id=" + id);
		}else
		{
			result=newInstance(c);
		}
		return result;
	}
	
	@Override
	public S buildInstance(int id, Object... args) {
		S result=null;
		Class<? extends S> c = getScriptClass(id);
		if (c == null) 
		{
			_log.error("not found script,id=" + id);
		}else
		{
			result=newInstance(c,args);
		}
		return result;
	}
	
	public S buildInstance(String name) {
		S result=null;
		Class<? extends S> c = getScriptClass(name);
		if (c == null) 
		{
			_log.error("not found script,name=" + name);
		}else
		{
			result=newInstance(c);
		}
		return result;
	}
	
	@Override
	public S buildInstance(String name, Object... args) {
		S result=null;
		Class<? extends S> c = getScriptClass(name);
		if (c == null) 
		{
			_log.error("not found script,name=" + name);
		}else
		{
			result=newInstance(c,args);
		}
		return result;
	}

	public final void reload() {
		try {
			load();
		} catch (Throwable e) {
			_log.error(e.getMessage(), e);
		}
	}
}