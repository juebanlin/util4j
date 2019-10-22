package net.jueb.util4j.hotSwap.memoryScript.impl;
import net.jueb.util4j.hotSwap.memoryScript.MemoryCompiler;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleMemoryCompiler implements MemoryCompiler {

    /**
     * 装载字符串成为java可执行文件
     *
     * @param className className
     * @param javaCodes javaCodes
     * @return Class
     */
    public synchronized Class<?> compile(String className, String javaCodes) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileManager manager = new MemoryFileManager(compiler.getStandardFileManager(null, null, null));
        List<String> options = new ArrayList<>();
        options.addAll(Arrays.asList("-classpath", System.getProperty("java.class.path")));
        List<JavaFileObject> files = new ArrayList<>();
        files.add(new MemoryJavaFileObject(className, javaCodes));
        compiler.getTask(null, manager, null, options, null, files).call();
        return manager.getClassLoader(null).loadClass(className);
    }
}
