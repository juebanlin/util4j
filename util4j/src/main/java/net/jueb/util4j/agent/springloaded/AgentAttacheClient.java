package net.jueb.util4j.agent.springloaded;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

public class AgentAttacheClient {

	public static void attachAgent(String vmPid, File agent) {
		VirtualMachine vm;
		try {
			vm = VirtualMachine.attach(vmPid);
			vm.loadAgent(agent.getAbsolutePath());
			vm.detach();
			System.out.println("动态加载agent完成!");
		} catch (AttachNotSupportedException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (AgentLoadException e) {
			e.printStackTrace();
		} catch (AgentInitializationException e) {
			e.printStackTrace();
		}
	}

	public static VirtualMachine getCurrentVm() {
		// 获取当前jvm的进程pid
		String pid = ManagementFactory.getRuntimeMXBean().getName();
		int indexOf = pid.indexOf('@');
		if (indexOf > 0) {
			pid = pid.substring(0, indexOf);
		}
		// 获取当前jvm
		try {
			return VirtualMachine.attach(pid);
		} catch (AttachNotSupportedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		File agent = new File("plugins/springloaded-1.2.1.RELEASE.jar");
		AgentAttacheClient.attachAgent("6340", agent);
	}
}
