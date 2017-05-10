package net.jueb.util4j.beta.agent;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
/**
 * 
agentlib:libname[=options] 
 用于装载本地lib包；
 其中libname为本地代理库文件名，默认搜索路径为环境变量PATH中的路径，options为传给本地库启动时的参数，多个参数之间用逗号分隔。在Windows平台上jvm搜索本地库名为libname.dll的文件，在linux上jvm搜索本地库名为libname.so的文件，搜索路径环境变量在不同系统上有所不同，比如Solaries上就默认搜索LD_LIBRARY_PATH。
 比如：-agentlib:hprof
 用来获取jvm的运行情况，包括CPU、内存、线程等的运行数据，并可输出到指定文件中；windows中搜索路径为JRE_HOME/bin/hprof.dll。

-agentpath:pathname[=options] 
 按全路径装载本地库，不再搜索PATH中的路径；其他功能和agentlib相同；更多的信息待续，在后续的JVMTI部分会详述。

-javaagent:jarpath[=options] 

 指定jvm启动时装入java语言基础设施代理。jarpath文件中的mainfest文件必须有Premain-Class（启动前捆绑时需要）， Agent-Class（运行时捆绑时需要）属性。代理类也必须实现公共的静态public static void premain(String agentArgs, Instrumentation inst)方法（和main方法类似）。当jvm初始化时，将按代理类的说明顺序调用premain方法；具体参见java.lang.instrument软件包的描述。

s * @author juebanlin
 *
 */
public class AgentLoad {
	public static void main(String[] args) throws IOException,
			AttachNotSupportedException, AgentLoadException,
			AgentInitializationException, InterruptedException {
		// 获取当前jvm的进程pid
		String pid = ManagementFactory.getRuntimeMXBean().getName();
		int indexOf = pid.indexOf('@');
		if (indexOf > 0) {
			pid = pid.substring(0, indexOf);
		}
		System.out.println("当前JVM Process ID: " + pid);
		// 获取当前jvm
		VirtualMachine vm = VirtualMachine.attach(pid);
		// 当前jvm加载代理jar包,参数1是jar包路径地址,参数2是给jar包代理类传递的参数
		vm.loadAgent("D:/123.jar", "my agent:123.jar");
		Thread.sleep(1000);
		vm.detach();
	}
}
