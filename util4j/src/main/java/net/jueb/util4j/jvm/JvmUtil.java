package net.jueb.util4j.jvm;

import java.lang.management.ManagementFactory;

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
}
