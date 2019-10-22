package net.jueb.util4j.hotSwap.memoryScript;

import net.jueb.util4j.hotSwap.memoryScript.impl.CharSequenceCompiler;
import net.jueb.util4j.hotSwap.memoryScript.impl.SimpleMemoryCompiler;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 如果出現JavacProcessingEnvironment文件需要maven導入tools.jar
 */
public class MemoryScriptRunner {

    static SimpleMemoryCompiler compiler1=new SimpleMemoryCompiler();
    static CharSequenceCompiler<MemoryScript> compiler2 = new CharSequenceCompiler<>(MemoryScriptRunner.class.getClassLoader(), new ArrayList<>());


    public static <R,I> R runScript1(String className, String javaCodes,I arg) throws Exception {
        Class<MemoryScript<R,I>> scripClazz= (Class<MemoryScript<R,I>>) compiler1.compile(className,javaCodes);
        MemoryScript<R,I> script=scripClazz.newInstance();
        return script.run(arg);
    }

    public static <R,I> R runScript2(String qName, String javaCodes,I arg) throws Exception {
        Class<MemoryScript<R,I>> scripClazz= (Class<MemoryScript<R,I>>) compiler2.compile(qName,javaCodes);
        MemoryScript<R,I> script=scripClazz.newInstance();
        return script.run(arg);
    }

    public static void test(){
        System.out.println("script ok");
    }

    public static void main(String[] args) throws Exception {
        Scanner sc=new Scanner(System.in);
        System.out.println("read:");
        StringBuffer sb=new StringBuffer();
        for(;;)
        {
            String javaCode=sc.nextLine();
            if("#exit".equals(javaCode)){
                break;
            }
            sb.append(javaCode);
        }
        System.out.println(sb.toString());
        String scriptResult1= MemoryScriptRunner.runScript1("net.jueb.util4j.hotSwap.memoryScript.ScriptDemo",sb.toString(),"compiler1");
        String scriptResult2= MemoryScriptRunner.runScript2("net.jueb.util4j.hotSwap.memoryScript.ScriptDemo",sb.toString(),"compiler2");
        System.out.println("scriptResult1:"+scriptResult1);
        System.out.println("scriptResult2:"+scriptResult2);
    }

    /**
     *
     *
<code>
     package net.jueb.util4j.hotSwap.memoryScript;
     public class ScriptDemo implements MemoryScript<String,String> {
    @Override
    public String run(String arg) {
    MemoryScriptRunner.test();
    return "script result,arg:"+arg;
    }
    }
     </code>

     */

}
