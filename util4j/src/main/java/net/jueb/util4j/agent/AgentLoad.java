package net.jueb.util4j.agent;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

public class AgentLoad {

	public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException, InterruptedException {
		System.out.println("业务系统被执行!");
		System.out.println("当前业务系统使用加载器:"+Thread.currentThread().getContextClassLoader().toString());
		System.out.println("业务系统动态加载agent:D:/agent.jar");
		//获取当前jvm的进程pid
		String pid = ManagementFactory.getRuntimeMXBean().getName();  
        int indexOf = pid.indexOf('@');  
        if (indexOf > 0)  
        {  
            pid = pid.substring(0, indexOf);  
        }  
        //获取当前jvm
        VirtualMachine vm=VirtualMachine.attach(pid);
        //当前jvm加载代理jar包,参数1是jar包路径地址,参数2是给jar包代理类传递的参数
        String agentArg="my agent";
        String javaAgent="D:/agent.jar";
//        String dllAgent="D:/agent.dll";
//        String dllAgentPath = "/AgentSample/Debug/AgentSample.dll";
        String jarAgentPath = "/AgentSample/Debug/AgentSample.jar";
        jarAgentPath="springloaded-1.2.1.RELEASE.jar";
        vm.loadAgent(javaAgent,agentArg);//加载agent的jar文件路径和agent方法参数
//        vm.loadAgentLibrary(dllAgent, agentArg);//加载dll类型的agent和agent方法参数
//        vm.loadAgentPath(dllAgentPath, agentArg);//根据当前程序启动路径加载相对路径的dll类型agent文件
        vm.loadAgentPath(jarAgentPath, agentArg);//根据当前程序启动路径加载相对路径的jar类型agent文件
        Thread.sleep(1000);
        vm.detach();
        System.out.println("业务系统动态加载agent:D:/123.jar完成!");
        System.out.println(Arrays.toString(MyAgent.inst.getAllLoadedClasses()));
        System.in.read();
	}
}
