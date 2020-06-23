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
        return  getAgentHook(null,true);
    }

    public synchronized static AgentHook getAgentHook(boolean deleteOndetach) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        return  getAgentHook(null,deleteOndetach);
    }

    /**
     * 指定一个../xx.jar的临时文件路径用于agent的加载
     * @param agentTmpPath String path = File.createTempFile("agent_tmp", ".jar", new   File("").getAbsoluteFile()).getPath();
     * @param deleteOndetach
     * @return
     * @throws IOException
     * @throws AttachNotSupportedException
     * @throws AgentLoadException
     * @throws AgentInitializationException
     */
    public synchronized static AgentHook getAgentHook(String agentTmpPath,boolean deleteOndetach) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        if(agentHook!=null){
            return agentHook;
        }
        File tempFile=null;
        VirtualMachine virtualMachine=null;
        FileOutputStream fos;
        try {
            InputStream resourceAsStream = AgentHookUtil.class.getClassLoader().getResourceAsStream(agentName);
            byte[] agentData=InputStreamUtils.getBytes(resourceAsStream);
            if(agentTmpPath!=null){
               try {
                   tempFile=new File(agentTmpPath);
                   tempFile.createNewFile();
               }catch (Exception e){
                   log.error(e.getMessage(),e);
               }
            }
            if(tempFile==null){
                tempFile = File.createTempFile("agent_tmp", ".jar");
            }
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
            agentHook=AgentHookImpl.getInstance();
            return agentHook;
        }finally {
            if(virtualMachine!=null){
                virtualMachine.detach();
                log.info("detach agent");
            }
            if(deleteOndetach){
                if(tempFile!=null){
                    tempFile.delete();
                    if(tempFile.exists()){
                        tempFile.deleteOnExit();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        AgentHook agentHook = getAgentHook();
        System.out.println(agentHook);
        System.out.println(agentHook.getInstrumentation());
    }
}
