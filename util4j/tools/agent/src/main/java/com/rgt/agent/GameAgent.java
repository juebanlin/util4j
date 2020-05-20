package com.rgt.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Manifest-Version: 1.0
 * Can-Redefine-Classes: true
 * Agent-Class: cn.think.in.java.clazz.loader.asm.agent.AgentMainTraceAgent
 * Can-Retransform-Classes: true
 */
public class GameAgent {

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
                Constructor constructor = clazz.getDeclaredConstructors()[0];
                constructor.setAccessible(true);
                Object hook= constructor.newInstance();
                System.out.println("agentmain------,getAgentHook:"+hook);
                Consumer<Instrumentation> consumer= (Consumer<Instrumentation>) hook;
                consumer.accept(inst);
                System.out.println("agentmain------,hookInstrumentation success");
                Supplier<ClassFileTransformer> supplier= (Supplier<ClassFileTransformer>) hook;
                ClassFileTransformer classFileTransformer = supplier.get();
                if(inst.isRetransformClassesSupported()){
                    inst.addTransformer(classFileTransformer,true);
                    System.out.println("agentmain------,hookClassFileTransformer success");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}