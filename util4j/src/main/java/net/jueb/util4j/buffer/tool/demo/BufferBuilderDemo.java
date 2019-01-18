package net.jueb.util4j.buffer.tool.demo;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.CharsetUtil;
import net.jueb.util4j.buffer.tool.BufferBuilder;

public class BufferBuilderDemo extends AbstractBufferBuilder{

	protected Logger log=LoggerFactory.getLogger(getClass());

	public static final String writeMethodName="writeTo";
	public static final String readMethodName="readFrom";
	
	/**
	 * (@Override)?([\s]+|[\r\n\t])(public)([\s]+|[\r\n|\r|\t])(void)([\s]+|[\r\n|\r|\t])(writeToSql)([\s]*|[\r\n|\r|\t])(\()([\s]*|[\r\n|\r|\t])(ByteBuffer)([\s]+|[\r\n|\r|\t])(buffer)([\s]*|[\r\n|\r|\t])(\))([\s]*|[\r\n|\r|\t])(\{)([\s\S]*)(\})
	 */
	public static final String MATCH_WRITE="(@Override)?([\\s]+|[\\r\\n\\t])(public)([\\s]+|[\\r\\n|\\r|\\t])(void)([\\s]+|[\\r\\n|\\r|\\t])("+writeMethodName+")([\\s]*|[\\r\\n|\\r|\\t])(\\()([\\s]*|[\\r\\n|\\r|\\t])(ByteBuffer)([\\s]+|[\\r\\n|\\r|\\t])(buffer)([\\s]*|[\\r\\n|\\r|\\t])(\\))([\\s]*|[\\r\n|\\r|\\t])(\\{)([\\s\\S]*)(\\})";
	public static final String MATCH_READ="(@Override)?([\\s]+|[\\r\\n\\t])(public)([\\s]+|[\\r\\n|\\r|\\t])(void)([\\s]+|[\\r\\n|\\r|\\t])("+readMethodName+")([\\s]*|[\\r\\n|\\r|\\t])(\\()([\\s]*|[\\r\\n|\\r|\\t])(ByteBuffer)([\\s]+|[\\r\\n|\\r|\\t])(buffer)([\\s]*|[\\r\\n|\\r|\\t])(\\))([\\s]*|[\\r\n|\\r|\\t])(\\{)([\\s\\S]*)(\\})";

	public static String BEGIN_FLAG = "//auto sql write begin";
	public static String END_FLAG = "//auto sql write end";
	
	public void build(String soruceRootDir,String pkg)throws Exception
	{
		BufferBuilder bb=new BufferBuilder("net.jueb.util4j.buffer.ArrayBytesBuff", "writeTo", "readFrom");
		//属性过滤器
		bb.addFieldSkipFilter((field)->{
			String name = field.getName();
			return name.contains("$SWITCH_TABLE$");
		});
		//其它类型
		bb.addTypeHandler((ctx)->{
			Class<?> type=ctx.varType();
			if(Date.class.isAssignableFrom(type))
			{
				String ClassName=type.getSimpleName();
				ctx.write().append("\t").append(ctx.varBuffer()+".writeLong("+ctx.varName()+".getTime());").append("\n");
				ctx.read().append("\t").append(ctx.varName() +"=new "+ClassName+"();").append("\n");
				ctx.read().append("\t").append(ctx.varName() + ".setTime("+ctx.varBuffer()+".readLong());").append("\n");
				return true;
			}
			return false;
		});
		
		List<Class<?>> fileList = getClassInfo(soruceRootDir, pkg);
		for(Class<?> clazz:fileList)
		{
			if(!Dto.class.isAssignableFrom(clazz))
			{
				continue;
			}
			StringBuilder writeSb=new StringBuilder();
			StringBuilder readSb=new StringBuilder();
			bb.build(clazz,writeSb,readSb);
			writeSb.append("\n");
			writeSb.append(readSb.toString());
			File javaSourceFile=findJavaSourceFile(soruceRootDir, clazz);
			String javaSource=fillCode(javaSourceFile, writeSb);
			FileUtils.writeByteArrayToFile(javaSourceFile,javaSource.getBytes(CharsetUtil.UTF_8));
		}
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
		int start=javaSource.indexOf(BEGIN_FLAG);
		int end=javaSource.indexOf(END_FLAG);
		if(start>0&& end>0)
		{
			String head=javaSource.substring(0, start+BEGIN_FLAG.length());
			String til=javaSource.substring(end, javaSource.length());
			javaSource=head+"\n"+bufferCode.toString()+"\n"+til;
		}
		return javaSource;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		String path=System.getProperty("user.dir")+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator;
		String pkg="net.jueb.util4j.buffer.tool.demo";
		BufferBuilderDemo buildUtils = new BufferBuilderDemo();
		buildUtils.build(path,pkg);
	}
}
