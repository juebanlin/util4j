package net.jueb.util4j.test.classLoader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class TestDirAndJar {

	public void test1() throws Exception
	{
		String path="C:/Users/juebanlin/git/util4j/util4j/target/classes";
		File file=new File(path);
		URL url=file.toURI().toURL();
		URLClassLoader u=new URLClassLoader(new URL[]{url});
		Class c=u.loadClass("net.jueb.util4j.validator.RegexValidator");
		System.out.println(c);//URLClassLoader支持协议里面的资源
	}
	
	public static void main(String[] args) throws Exception {
		TestDirAndJar t=new TestDirAndJar();
		t.test1();
	}
}
