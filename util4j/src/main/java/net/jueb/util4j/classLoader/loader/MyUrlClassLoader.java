package net.jueb.util4j.classLoader.loader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 *@author juebanlin
 *@email juebanlin@gmail.com
 *@createTime 2015年4月12日 下午2:56:45
 *打破双亲委托加载顺序,让子类先加载,父类后加载
 **/
public class MyUrlClassLoader extends URLClassLoader{

	public MyUrlClassLoader() {
		super(new URL[]{});
	}
	
	public MyUrlClassLoader(URL[] urls) {
		super(urls);
	}
	
	public final void addURL(URL url)
	{
		if(url!=null)
		{
			super.addURL(url);
		}
	}
	
	public final void addURL(URL[] urls)
	{
		if(urls!=null)
		{
			for(URL url:urls)
			{
				addURL(url);
			}
		}
	}
	
	
	/**
	 * 加载类,如果是系统类则交给系统加载
	 * 如果当前类已经加载则返回类
	 * 如果当前类没有加载则定义并返回
	 */
	@Override
	protected Class<?> loadClass(String className, boolean resolve)
			throws ClassNotFoundException {
		System.out.println("开始加载类:"+className);
		Class<?> clazz=null;
		if(className.startsWith("java.")||className.startsWith("javax."))
		{//如果是系统类加载器
			System.out.println("交给系统类加载器加载:"+className);
//			clazz=super.loadClass(name);
			clazz=findSystemClass(className);
			if (clazz != null) 
			{//解析类结构
				if (resolve)
					resolveClass(clazz);
				return (clazz);
			}
			return clazz;
		}
		if(clazz==null)
		{
			System.out.println("交给当前类加载器加载:"+className);
			clazz=findLoadedClass(className);
			if (clazz != null) 
			{//解析类结构
				if (resolve) 
				{
					resolveClass(clazz);
				}
			}
		}
		if(clazz==null)
		{
			System.out.println("交给当前类加载器定义:"+className);
			try {
				clazz=findClass(className);
			} catch (Exception e) {
				//如果该类没有加载过，并且不属于必须由该类加载器加载之列都委托给系统加载器进行加载。
				System.out.println("交给当前线程类加载器加载:"+className);
				clazz=Thread.currentThread().getContextClassLoader().loadClass(className);
			}
		}
		if(clazz==null)
		{
			System.out.println("交给当前系统类加载器加载:"+className);
			clazz=findSystemClass(className);
		}
		System.out.println("类:"+clazz+"被"+clazz.getClassLoader()+"成功加载!");
		return clazz;
	}
	
	/**
	 * 查找url路径列表中类文件并声明定义类
	 */
	@Override
	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		System.out.println("当前类加载器classpath中查找并定义类:"+name);
		return super.findClass(name);
	}
	
	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
		URL url=null;
		//		URL url=new File("C:/Users/Administrator/git/GameProjects/snake/bin").toURI().toURL();
		url=new File("C:/Users/Administrator/Desktop/snake.jar").toURI().toURL();
		
		MyUrlClassLoader cl=new MyUrlClassLoader(new URL[]{url});
		System.out.println(Arrays.toString(cl.getURLs()));
		//第一次加载
		Class<?> clazz=cl.loadClass("net.jueb.game.snake.Start");
		System.out.println(clazz);
		Runnable task=(Runnable)clazz.newInstance();
		//第二次加载
		Class<?> clazz2=cl.loadClass("net.jueb.game.snake.Start");
		System.out.println(clazz2);
		Runnable task2=(Runnable)clazz2.newInstance();
		System.out.println(cl.findResource("META-INF/MANIFEST.MF"));
	}
}
