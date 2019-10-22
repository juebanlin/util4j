package net.jueb.util4j.hotSwap.memoryScript;

@FunctionalInterface
public interface MemoryScript<R,I>{

    R run(I arg);
}
