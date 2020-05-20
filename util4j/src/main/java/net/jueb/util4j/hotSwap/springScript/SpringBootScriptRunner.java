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

    String path;
    ScriptUtil scriptUtil;

    public SpringBootScriptRunner (String path){
        this.path=path;
        scriptUtil=new ScriptUtil(path);
    }

    public String runScript(String className,String script)throws Exception{
        Class<?> clazz=scriptUtil.buildClass(className,script);
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