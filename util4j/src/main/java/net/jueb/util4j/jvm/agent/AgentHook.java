package net.jueb.util4j.jvm.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public interface AgentHook {

    Instrumentation getInstrumentation();

    void updateClassFileTransformer(ClassFileTransformer classFileTransformer);
}