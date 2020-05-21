package net.jueb.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Manifest-Version: 1.0
 * Can-Redefine-Classes: true
 * Agent-Class: net.jueb.agent.JavaAgent
 * Can-Retransform-Classes: true
 */
public class JavaAgent {

   public static Instrumentation INST;

    public static void agentmain(String args, Instrumentation inst){
        INST=inst;
        System.out.println("agentmain----agentArgs:"+args);
        Class clazz=null;
        for (Class allLoadedClass : inst.getAllLoadedClasses()) {
            if(allLoadedClass.getName().equals(args)){
                clazz=allLoadedClass;break;
            }
        }
        System.out.println("agentmain----,clazz:"+clazz);
        if(clazz!=null){
            try {
                Method initMethod=clazz.getDeclaredMethod("init", new Class[]{Instrumentation.class});
                initMethod.setAccessible(true);
                Object result = initMethod.invoke(null, inst);
                System.out.println("agentmain------,initSuccess");
                if(inst.isRetransformClassesSupported() && result!=null){
                    ClassFileTransformer classFileTransformer=(ClassFileTransformer)result;
                    inst.addTransformer(classFileTransformer,true);
                    System.out.println("agentmain------,hookClassFileTransformer success");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}