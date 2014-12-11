package net.jueb.agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.concurrent.atomic.AtomicLong;
import com.sun.tools.attach.AttachNotSupportedException;
public class MyAgent {

	public static AtomicLong num=new AtomicLong();
	
	public static void agentmain(String arg,Instrumentation inst) throws AttachNotSupportedException, IOException
	{
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
