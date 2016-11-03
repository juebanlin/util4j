package net.jueb.util4j.beta.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.concurrent.atomic.AtomicLong;
import com.sun.tools.attach.AttachNotSupportedException;
public class MyAgent {

	public static AtomicLong num=new AtomicLong();
	
	public static Instrumentation inst;
	
	public static void agentmain(String arg,Instrumentation inst) throws AttachNotSupportedException, IOException
	{
		MyAgent.inst=inst;
		System.out.println("agent代理程序类[MyAgent]被执行!");
		System.out.println("当前代理程序类使用加载器:"+Thread.currentThread().getContextClassLoader().toString());
		num.incrementAndGet();
		System.out.println(num.longValue());
		System.out.println(arg);
		Class<?>[] classes = inst.getAllLoadedClasses();
        for(Class<?> cls :classes)
        {
            System.out.println(cls.getName());
        }
	}
	public static void agentmain(String arg)
	{
		System.out.println(num.longValue());
		System.out.println(arg);
	}
}
