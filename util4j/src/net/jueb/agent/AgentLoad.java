package net.jueb.agent;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

public class AgentLoad {

	public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException, InterruptedException {
		//获取当前jvm的进程pid
		String pid = ManagementFactory.getRuntimeMXBean().getName();  
        int indexOf = pid.indexOf('@');  
        if (indexOf > 0)  
        {  
            pid = pid.substring(0, indexOf);  
        }  
        System.out.println("当前JVM Process ID: " + pid); 
        //获取当前jvm
        VirtualMachine vm=VirtualMachine.attach(pid);
        //当前jvm加载代理jar包,参数1是jar包路径地址,参数2是给jar包代理类传递的参数
        vm.loadAgent("D:/123.jar","my agent:123.jar");
        Thread.sleep(1000);
        vm.detach();
	}
}
