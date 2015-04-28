package net.jueb.util4j.agent.springloaded;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

public class AgentUtil {

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

	public static void main(String[] args) throws AgentLoadException, AgentInitializationException, IOException, AttachNotSupportedException {
		VirtualMachine vm=VirtualMachine.attach("7876");
		vm.loadAgent("d:/springloaded-1.2.1.RELEASE.jar");
//		vm.loadAgent("d:/myagent.jar","myagent");
		System.out.println(vm.getAgentProperties().toString());
		System.in.read();
	}
}
