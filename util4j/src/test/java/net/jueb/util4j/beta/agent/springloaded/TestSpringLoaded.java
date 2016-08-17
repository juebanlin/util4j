package net.jueb.util4j.beta.agent.springloaded;

import java.io.IOException;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

/**
 * 可行方法1：-javaagent:"plugins/springloaded-1.2.1.RELEASE.jar" -noverify
 * 可执行方法2：vm运行参数前面使用-noverify关闭字节码校验,在main函数中调用vm.loadAgent("d:/springloaded-1.2.1.RELEASE.jar");加载代理
 * @author Administrator
 */
public class TestSpringLoaded {

	public static void main(String[] args) throws AttachNotSupportedException,
			IOException, AgentLoadException, AgentInitializationException,
			InterruptedException {
		VirtualMachine vm=AgentUtil.getCurrentVm();
		//先载入agent
		vm.loadAgent("d:/springloaded-1.2.1.RELEASE.jar","myagent");
		System.out.println("加载agent成功");
		vm.detach();
		TestSpringLoacedClass t1=new TestSpringLoacedClass();
		for(int i=0;i<10000;i++)
		{
			TestSpringLoacedClass t2=new TestSpringLoacedClass();
			t1.say2();
			t2.say2();
			Thread.sleep(1000);
		}
	}
}
