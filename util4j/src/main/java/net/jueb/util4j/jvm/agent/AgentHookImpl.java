package net.jueb.util4j.jvm.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AgentHookImpl implements Consumer<Instrumentation>, Supplier<ClassFileTransformer>, AgentHook {

    private static Instrumentation INST;

    static AgentHook agentHook;

    private final static ClassFileTransformer classFileTransformer = new ClassFileTransformer() {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            if (runTimeClassFileTransformer != null) {
                return runTimeClassFileTransformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            }
            return classfileBuffer;
        }
    };
    public static ClassFileTransformer runTimeClassFileTransformer;

    private AgentHookImpl(){
        agentHook=this;
    }

    @Override
    public final void accept(Instrumentation instrumentation) {
        INST = instrumentation;
    }

    @Override
    public final void updateClassFileTransformer(ClassFileTransformer classFileTransformer) {
        this.runTimeClassFileTransformer = classFileTransformer;
    }

    @Override
    public ClassFileTransformer get() {
        return classFileTransformer;
    }

    @Override
    public Instrumentation getInstrumentation() {
        return INST;
    }
}