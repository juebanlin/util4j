package net.jueb.util4j.buffer.tool.demo;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;

import net.jueb.util4j.buffer.tool.ClassFileUitl;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.CharsetUtil;

public class JsonFieldBuilderDemo{

	@Target({ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public @interface JsonField {

	}

	public interface JsonFieldTable {

	}
	
	protected Logger log=LoggerFactory.getLogger(getClass());

	public static String BEGIN_FLAG_MATCH = "//([\\s]+)?JsonField_Begin";
	public static String END_FLAG_MATCH = "//([\\s]+)?JsonField_End";
	//开始结束标记
	public static String BEGIN_FLAG = "//JsonField_Begin";
	public static String END_FLAG = "//JsonField_End";
	
	
	public void build(String path,String pkg,Class<?> buildClassType)throws Exception
	{
		List<Class<?>> fileList = ClassFileUitl.getClassInfo(path,pkg);
		for(Class<?> clazz:fileList)
		{
			if(!buildClassType.isAssignableFrom(clazz))
			{
				continue;
			}
			StringBuilder appender=new StringBuilder();
			for(Field f:clazz.getDeclaredFields())
			{
				if(f.getAnnotation(JsonField.class)==null)
				{
					continue;
				}
				buildGetSet(f, appender);
			}
			File javaSourceFile=ClassFileUitl.findJavaSourceFile(path, clazz);
			String javaSource=fillCode(javaSourceFile, appender);
			FileUtils.writeByteArrayToFile(javaSourceFile,javaSource.getBytes(CharsetUtil.UTF_8));
			log.info("complete==>"+clazz.getName());
		}
	}
	
	public void buildGetSet(Field f,StringBuilder appender) {
		String name=f.getName();
		String type=f.getGenericType().getTypeName();
		String mothdName = name.substring(0, 1).toUpperCase() + name.substring(1);
		//setBuild
		appender.append("\t").append("public String getJson" + mothdName + "() {\n");
		appender.append("\t").append(type+" var=get"+mothdName+"();\n");
		appender.append("\t").append("if(var==null) {return null;}\n");
		appender.append("\t").append("return new com.google.gson.Gson().toJson(var);\n");
		appender.append("\t").append("}\n");
		appender.append("\n");
		//getBuild
		appender.append("\t").append("public void setJson" + mothdName + "(String json) {\n");
		appender.append("\t").append("if(json==null||json.isEmpty()){return ;}\n");
		appender.append("\t").append("java.lang.reflect.Type type=new com.google.gson.reflect.TypeToken<"+type+">(){}.getType();\n");
		appender.append("\t").append("set"+mothdName+"(new com.google.gson.Gson().fromJson(json, type));\n");
		appender.append("\t").append("}\n");
		appender.append("\n");
	}
	
	/**
	 * 填充代码
	 * @param javaFile
	 * @param bufferCode
	 * @return
	 * @throws IOException
	 */
	public String fillCode(File javaFile,StringBuilder bufferCode) throws IOException
	{
		String javaSource=FileUtils.readFileToString(javaFile, CharsetUtil.UTF_8);
		javaSource=javaSource.replaceAll(BEGIN_FLAG_MATCH,BEGIN_FLAG);
		javaSource=javaSource.replaceAll(END_FLAG_MATCH,END_FLAG);
		String startFlag=BEGIN_FLAG;
		String endFlag=END_FLAG;
		int start=javaSource.indexOf(startFlag);
		int end=javaSource.indexOf(endFlag);
		if(start>0&& end>0)
		{
			String head=javaSource.substring(0, start+startFlag.length());
			String til=javaSource.substring(end, javaSource.length());
			javaSource=head+"\n"+bufferCode.toString()+"\n"+til;
		}else
		{
			log.error("not found match:"+javaFile.getName());
		}
		return javaSource;
	}
	
	public static void main(String[] args) throws Exception {
		String path=System.getProperty("user.dir")+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator;
		String pkg="net.jueb.util4j.buffer.tool.demo";
		JsonFieldBuilderDemo buildUtils = new JsonFieldBuilderDemo();
		buildUtils.build(path,pkg,JsonFieldTable.class);
	}
}
