package net.jueb.util4j.hotSwap.springScript;

import com.taobao.arthas.compiler.DynamicCompiler;
import org.springframework.boot.loader.LaunchedURLClassLoader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *适用于springboot项目的逻辑热更新执行
 *<pre>
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-loader</artifactId>
 </dependency>
 <dependency>
 <groupId>com.taobao.arthas</groupId>
 <artifactId>arthas-memorycompiler</artifactId>
 <version>3.1.7</version>
 </dependency>
 </pre>
 *
 */
public class SpringBootScriptRunner {
    List<String> paths=new ArrayList<>();

    public SpringBootScriptRunner(String ...jarPath){
        for (String s : jarPath) {
            paths.add(s);
        }
    }

    public SpringBootScriptRunner(){
        //默认取当前类所在的jar
        String path1=Thread.currentThread().getContextClassLoader().getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        String path2=getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        paths.add(path1);
        paths.add(path2);
    }

    public String runScript(String className,String script)throws Exception{
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
        if(clazz==null){
            return "#not found class byClassName";
        }
        if(!SpringBootScript.class.isAssignableFrom(clazz)){
            return "#not found class type for SpringBootScript,clazz:"+clazz;
        }
        SpringBootScript s=(SpringBootScript)clazz.newInstance();
        return s.run();
    }
}