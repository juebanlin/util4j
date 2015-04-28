package net.jueb.util4j.agent.springloaded;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;

import net.jueb.util4j.classLoader.util.ClassUtil;
import net.jueb.util4j.classLoader.util.UrlClassLoaderUtil;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;

/**
 * 可行方法1：-javaagent:"plugins/springloaded-1.2.1.RELEASE.jar" -noverify
 * @author Administrator
 */
public class TestSpringLoaded {

	public static void main(String[] args) throws AttachNotSupportedException,
			IOException, AgentLoadException, AgentInitializationException,
			InterruptedException {
		AgentAttacheClient.getCurrentVm().loadAgent("plugins/springloaded-1.2.1.RELEASE.jar");
		TestSpringLoacedClass t1=new TestSpringLoacedClass();
		for(int i=0;i<100;i++)
		{
			TestSpringLoacedClass t2=new TestSpringLoacedClass();
			t1.say();
			t2.say();
			Thread.sleep(1000);
		}
	}
}
