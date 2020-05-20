package net.jueb.util4j.hotSwap.springScript;

import com.taobao.arthas.compiler.DynamicCompiler;
import org.springframework.boot.loader.LaunchedURLClassLoader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScriptUtil {

    List<String> paths=new ArrayList<>();

    public ScriptUtil(String ...jarPath){
        for (String s : jarPath) {
            paths.add(s);
        }
    }

    public ScriptUtil(){
        //默认取当前类所在的jar
        String path1=Thread.currentThread().getContextClassLoader().getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        String path2=getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        paths.add(path1);
        paths.add(path2);
    }

    public Class<?> buildClass(String className,String script)throws Exception{
        URL[] urls=new URL[paths.size()];
        for (int i = 0; i < paths.size(); i++) {
            String path=paths.get(i);
            URL url=new File(path).toURI().toURL();
            urls[i]=url;
        }
        LaunchedURLClassLoader classLoader = new LaunchedURLClassLoader(urls, Thread.currentThread().getContextClassLoader());
        DynamicCompiler dynamicCompiler = new DynamicCompiler(classLoader);
        dynamicCompiler.addSource(className,script);
        Map<String,Class<?>> map=dynamicCompiler.build();
        Class<?> clazz=map.get(className);
        return clazz;
    }

    public byte[] buildClassBytes(String className,String script)throws Exception{
        URL[] urls=new URL[paths.size()];
        for (int i = 0; i < paths.size(); i++) {
            String path=paths.get(i);
            URL url=new File(path).toURI().toURL();
            urls[i]=url;
        }
        LaunchedURLClassLoader classLoader = new LaunchedURLClassLoader(urls, Thread.currentThread().getContextClassLoader());
        DynamicCompiler dynamicCompiler = new DynamicCompiler(classLoader);
        dynamicCompiler.addSource(className,script);
        Map<String, byte[]> map = dynamicCompiler.buildByteCodes();
        byte[] clazz=map.get(className);
        return clazz;
    }
}
