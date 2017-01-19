package net.jueb.util4j.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

import net.jueb.util4j.math.CombinationUtil;

/**
 * URLClassLoader 支持jar的url以及包含class文件的目录,匿名类需要添加$才可加载
 * @author Administrator
 */
public class TestUrlClassLoader {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		File f=new File("C:/Users/Administrator/git/util4j/util4j/target/classes");
		URL url=f.toURI().toURL();
		URL[] urls=new URL[]{url};
		URLClassLoader loader=new URLClassLoader(urls);
		Class c=loader.loadClass("net.jueb.util4j.math.CombinationUtil");
		System.out.println(c);
		CombinationUtil c22=(CombinationUtil) c.newInstance();
		System.out.println(c22);
		Class c2=loader.loadClass("net.jueb.util4j.math.CombinationUtil$CombinationController");
		System.out.println(c2);
		Class c3=loader.loadClass("net.jueb.util4j.math.CombinationUtil$ForEachByteIndexController");
		System.out.println(c3);
		Enumeration<URL> ss=loader.findResources("*.class");
		System.out.println(ss.hasMoreElements());
	}
}
