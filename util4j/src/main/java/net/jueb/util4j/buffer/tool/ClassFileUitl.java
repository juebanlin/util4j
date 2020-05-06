package net.jueb.util4j.buffer.tool;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.util4j.file.FileUtil;

public class ClassFileUitl {

	protected static Logger log=LoggerFactory.getLogger(ClassFileUitl.class);
	
	/**
	 * 获取源码目录下指定包下的类
	 * @param root
	 * @param pkg
	 * @return
	 * @throws Exception
	 */
	public static List<Class<?>> getClassInfo(String root,String pkg) throws Exception
	{
		List<Class<?>> list=new ArrayList<Class<?>>();
		String suffix=".java";
		File rootDir=new File(root);
		Set<File> files=FileUtil.findFileByDirAndSub(rootDir, new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() || pathname.getName().endsWith(suffix);
			}
		});
		URLClassLoader loader=new URLClassLoader(new URL[]{rootDir.toURI().toURL()});
		try {
			// 获取路径长度
			int clazzPathLen = rootDir.getAbsolutePath().length() + 1;
			for(File file:files)
			{
				String className = file.getAbsolutePath();
				className = className.substring(clazzPathLen, className.length() - suffix.length());
				className = className.replace(File.separatorChar, '.');
				try {
					Class<?> clazz=loader.loadClass(className);
					String pkgName=clazz.getPackage().getName();
					if(pkgName.startsWith(pkg))
					{
						list.add(clazz);
					}
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		} finally {
			loader.close();
		}
		return list;
	}
	
	/**
	 * 查找类源码文件
	 * @param soruceRootDir 源码目录
	 * @param clazz 类
	 * @return
	 */
	public static File findJavaSourceFile(String soruceRootDir,Class<?> clazz)
	{
		String url = StringUtils.replace(clazz.getPackage().toString().split(" ")[1], ".",File.separator);
		String sourceFilename = soruceRootDir+url +File.separator + clazz.getSimpleName() + ".java";
		File javaSourceFile=new File(sourceFilename);
		return javaSourceFile;
	}
}
