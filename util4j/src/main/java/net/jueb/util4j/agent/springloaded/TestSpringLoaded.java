package net.jueb.util4j.agent.springloaded;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;

import org.springsource.loaded.agent.SpringLoadedAgent;

import net.jueb.util4j.agent.MyAgent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

public class TestSpringLoaded {

	public static void main(String[] args) throws AttachNotSupportedException, IOException, AgentLoadException, AgentInitializationException, InterruptedException {
		//获取当前jvm的进程pid
		String pid = ManagementFactory.getRuntimeMXBean().getName();  
		int indexOf = pid.indexOf('@');  
		if (indexOf > 0)  
		  {  
		       pid = pid.substring(0, indexOf);  
		   }  
		    //获取当前jvm
		  VirtualMachine vm=VirtualMachine.attach(pid);
		  String agentArg="spring agent";
	      String javaAgent="C:/Users/Administrator/.m2/repository/org/springframework/springloaded/1.2.1.RELEASE/springloaded-1.2.1.RELEASE.jar";
	      vm.loadAgent(javaAgent,agentArg);//加载agent的jar文件路径和agent方法参数
	      Thread.sleep(1000);
	      vm.detach();
	        System.out.println("动态加载agent完成!");
	        System.out.println(Arrays.toString(SpringLoadedAgent.getInstrumentation().getAllLoadedClasses()));
	        System.in.read();
	}
}
