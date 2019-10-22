package net.jueb.util4j.hotSwap.memoryScript;

public interface MemoryCompiler {

    /**
     * @param className example: com.rgt.slg.gameserver.script.MemoryCompiler
     * @param javaCodes 源码
     * @return
     * @throws Exception
     */
    Class<?> compile(String className, String javaCodes) throws Exception;
}
