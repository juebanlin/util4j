package net.jueb.util4j.jvm;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import sun.management.VMManagement;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class VmUtil {
    static RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    static VMManagement vmManagement;

    static {
        try {
            Field jvm = runtime.getClass().getDeclaredField("jvm");
            jvm.setAccessible(true);
            vmManagement = (VMManagement) jvm.get(runtime);

        } catch (Exception e) {

        }
    }

    public static int getVmPid() {
        if (vmManagement != null) {
            try {
                Method pidMethod = vmManagement.getClass().getDeclaredMethod("getProcessId");
                if (!pidMethod.isAccessible()) {
                    pidMethod.setAccessible(true);
                }
                int pid = (Integer) pidMethod.invoke(vmManagement);
                return pid;
            } catch (Exception e) {
            }
        }
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        int indexOf = pid.indexOf('@');
        if (indexOf > 0) {
            pid = pid.substring(0, indexOf);
        }
        return Integer.valueOf(pid);
    }

    public static VirtualMachine getVirtualMachine() throws IOException, AttachNotSupportedException {
        return VirtualMachine.attach(getVmPid() + "");
    }
}