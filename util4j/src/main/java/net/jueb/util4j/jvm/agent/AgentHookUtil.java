package net.jueb.util4j.jvm.agent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import lombok.extern.slf4j.Slf4j;
import net.jueb.util4j.bytesStream.InputStreamUtils;
import net.jueb.util4j.file.FileUtil;
import net.jueb.util4j.jvm.VmUtil;

import java.io.*;
import java.net.URL;

@Slf4j
public class AgentHookUtil {

    public static String agentName="tools/util4jAgent.jar";

    private static AgentHook agentHook;

    public synchronized static AgentHook getAgentHook() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        if(agentHook!=null){
            return agentHook;
        }
        File tempFile=null;
        VirtualMachine virtualMachine=null;
        FileOutputStream fos;
        try {
            InputStream resourceAsStream = AgentHookUtil.class.getClassLoader().getResourceAsStream(agentName);
            byte[] agentData=InputStreamUtils.getBytes(resourceAsStream);
            tempFile = File.createTempFile("agent_tmp", ".jar");
            fos=new FileOutputStream(tempFile);
            fos.write(agentData);
            fos.close();
            resourceAsStream.close();
            String path=tempFile.toString();
            System.out.println("loadAgentUsePath:"+path);
            log.info("loadAgentUsePath:"+path);
            virtualMachine = VmUtil.getVirtualMachine();
            String arg=AgentHookImpl.class.getName();
            virtualMachine.loadAgent(path,arg);
            agentHook=AgentHookImpl.agentHook;
            return agentHook;
        }finally {
            if(tempFile!=null){
                tempFile.delete();
            }
            if(virtualMachine!=null){
                virtualMachine.detach();
            }
        }
    }

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        AgentHook agentHook = getAgentHook();
        System.out.println(agentHook);
        System.out.println(agentHook.getInstrumentation());
    }
}
