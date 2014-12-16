package net.jueb.classLoader.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 用于获取指定包名下的所有类名.<br/>
 * 并可设置是否遍历该包名下的子包的类名.<br/>
 * 并可通过Annotation(内注)来过滤，避免一些内部类的干扰.<br/>
 * 
 * @author Sodino E-mail:sodino@qq.com
 * @version Time：2014年2月10日 下午3:55:59
 */
public class ClassUtil {
	
	
	/**
	 * 获取所在项目下的bin目录
	 * @return
	 */
	public static final File getProjectBin()
	{
		File bin=new File("bin");
		if(bin.exists() && bin.isDirectory())
		{
			return bin;
		}
		return null;
	}
	/**
	 * new File(ClassLoader.getSystemResource(".").getFile())
	 * @return
	 */
	public static final File getProjectBin2()
	{
		return new File(ClassLoader.getSystemResource(".").getFile());
	}
	
	/**
	 * 获取第三方class类所在目录地址
	 * @param clazz
	 * @return
	 */
	public static final URL getClassDir(Class<?> clazz)
	{
		return clazz.getResource("");
	}
	
	/**
	 * 获取第三方class类的文件形式
	 * @param clazz
	 * @return
	 */
	public static final File getClassFile(Class<?> clazz)
	{
		return new File(getClassDir(clazz).getFile()+clazz.getSimpleName()+".class");
	}
	
	/**
	 * 以一个class包根目录获取所有的class文件
	 * 如果该目录为c:/bin 且目录结构为c:/bin/net/jueb/util/A.class 则有net.jueb.util.A这个类
	 * @param classFolder
	 * @return
	 */
	public static final HashMap<String,File> findClassByDir(File classFolder)
	{
		HashMap<String,File> clazzs=new HashMap<String,File>();
		// 设置class文件所在根路径
		// 例如/usr/java/classes下有一个test.App类，则/usr/java/classes即这个类的根路径，而.class文件的实际位置是/usr/java/classes/test/App.class
		File clazzPath = classFolder;
		// 记录加载.class文件的数量
		if (clazzPath.exists() && clazzPath.isDirectory()) 
		{
			// 获取路径长度
			int clazzPathLen = clazzPath.getAbsolutePath().length() + 1;
			Stack<File> stack = new Stack<>();
			stack.push(clazzPath);
			// 遍历类路径
			while (stack.isEmpty() == false) 
			{
				File path = stack.pop();
				File[] classFiles = path.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.isDirectory() || pathname.getName().endsWith(".class");
					}
				});
				for (File subFile : classFiles) 
				{
					if (subFile.isDirectory()) 
					{
						stack.push(subFile);
					} else 
					{
						// 文件名称
						String className = subFile.getAbsolutePath();
						className = className.substring(clazzPathLen, className.length() - 6);
						className = className.replace(File.separatorChar, '.');
						clazzs.put(className, subFile);
					}
				}
			}
		}
		return clazzs;
	}
	
	/**
	 * 搜索目录下所有的jar文件
	 * @param dir
	 * @return 返回.jar结尾的文件集合
	 */
	public static final Set<File> findJarFileByDir(File dir)
	{
		Set<File> files=new HashSet<File>();
		if(dir==null || !dir.exists())
		{
			return files;
		}
		if(dir.isFile())
		{
			if(dir.isFile() && dir.equals(".jar"))
			{
				files.add(dir);
			}
			return files;
		}
		// 记录加载.jar文件的数量
		File clazzPath=dir;
		if (clazzPath.exists() && clazzPath.isDirectory()) 
		{//如果是目录
			// 获取路径长度
			Stack<File> stack = new Stack<File>();
			stack.push(clazzPath);
			// 遍历类路径
			while (stack.isEmpty() == false) 
			{
				File path = stack.pop();
				File[] jarFiles = path.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) 
					{// 获取所有的.jar
						return pathname.isDirectory() || pathname.getName().endsWith(".jar");
					}
				});
				for (File subFile : jarFiles) 
				{
					if (subFile.isDirectory()) 
					{//目录
						stack.push(subFile);
					} else 
					{//jar文件
						files.add(subFile);
					}
				}
			}
		}
		return files;
	}
	
	
	
	/**
	 * 搜索jar里面的class
	 * @throws IOException 
	 * */
	public static final Map<String,JarEntry> findClassByJar(JarFile jarFile) throws IOException {
		Map<String,JarEntry> map=new HashMap<String,JarEntry>();
		if(jarFile==null)
		{
			throw new RuntimeException("jarFile is Null");
		}
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		while (jarEntries.hasMoreElements()) 
		{//遍历jar的实体对象
			JarEntry jarEntry = jarEntries.nextElement();
			if(jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class"))
			{
				continue;
			}
			String jarEntryName = jarEntry.getName(); // 类似：sun/security/internal/interfaces/TlsMasterSecret.class
			String clazzName = jarEntryName.replace("/", ".");
			map.put(clazzName,jarEntry);
		}
		return map;
	}
}