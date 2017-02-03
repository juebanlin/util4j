package net.jueb.util4j.jvm;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

public class JvmUtil {
	
	/**
	 * 获取当前JVM进程ID
	 * @return
	 */
	public static int getPid()
	{
		String pid = ManagementFactory.getRuntimeMXBean().getName();
		int indexOf = pid.indexOf('@');
		if (indexOf > 0) {
			pid = pid.substring(0, indexOf);
		}
		return Integer.parseInt(pid);
	}
	
	/**
	 * 获取当前JVM
	 * @return
	 * @throws IOException 
	 * @throws AttachNotSupportedException 
	 */
	public static VirtualMachine getVirtualMachine() throws AttachNotSupportedException, IOException
	{
		String pid = ManagementFactory.getRuntimeMXBean().getName();
		int indexOf = pid.indexOf('@');
		if (indexOf > 0) {
			pid = pid.substring(0, indexOf);
		}
		return VirtualMachine.attach(pid);
	}
}
