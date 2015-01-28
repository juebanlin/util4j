package net.jueb.util4j.tools.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;

/**
 * 将log4j的日志记录对象记录的日志保存到内存数组
 * 此记录器内部维护着一个appender,该输出器自己有一个日志格式
 * 由于使用的是同一个Logger对象，所以输出的级别是一样的，不能修改日志级别
 * @author juebanlin
 *
 */
public class MemoryLog {
	
	private  Logger log;//配置文件log4j.properties放src下会自动找到
	
	//日志数据输出流==存储日志的内存字节数组
	private  ByteArrayOutputStream logDataStream;
	private OutputStream os;
	
	//默认日志格式
	private  String defaultPattern="MemoryLog==>[%p]:%l{%m}%n";
	private Layout lay;
	
	//添加一个日志输出器
	private	WriterAppender appender;
	
	//当前日志输出器是否关闭
	private boolean appenderOff;
	
	
	/**
	 * 按"[%p]:%l{%m}%n"日志格式为Logger对象增加一个到内存数组的日志记录器
	 * @param log
	 */
	public MemoryLog(Logger log) {
		this.log=log;
		//初始化内存数组
		logDataStream=new ByteArrayOutputStream();
		os=logDataStream;
		//设置日志格式
		lay=new org.apache.log4j.PatternLayout(this.defaultPattern);
		//设置日志输出器
		appender = new WriterAppender(lay,os);
		//设置输出器名称
		appender.setName(this.getClass().getName());
		log.setAdditivity(true);//覆盖记录，默认就是true
		//把输出到内存的日志输出器添加进去
		log.addAppender(appender);
	}
	/**
	 * 指定日志记录器对象和输出到内存数组中的日志格式
	 * @param log 
	 * @param pattern
	 */
	public MemoryLog(Logger log,String pattern) {
		this.log=log;
		defaultPattern=pattern;
		//初始化内存数组
		logDataStream=new ByteArrayOutputStream();
		os=logDataStream;
		//设置日志格式
		lay=new org.apache.log4j.PatternLayout(defaultPattern);
		//设置日志输出器
		appender = new WriterAppender(lay,os);
		//设置输出器名称
		appender.setName(this.getClass().getName());
		log.setAdditivity(true);//覆盖记录，默认就是true
		//把输出到内存的日志输出器添加进去
		log.addAppender(appender);
	}
	
	
	/**
	 * 清空内存日志数据,释放占用的内存空间
	 * @return
	 */
	public void clearLogData()
	{
		try {
			logDataStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取内存日志数据
	 * @return
	 */
	public  byte[] getLogData()
	{
		return logDataStream.toByteArray();
	}
	/**
	 * 显示日志信息
	 * @return
	 */
	public  String getLogString()
	{
		try {
			return new String(logDataStream.toByteArray(),"UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 关闭日志记录
	 * @param arg
	 */
	public  void off()
	{
		if(!appenderOff)
		{//如果
			log.removeAppender(this.getClass().getName());
			appender.close();
		}
	}
	/**
	 * 开启日志记录
	 * @param arg
	 */
	public  void on()
	{
		//重新设置日志输出器
		appender = new WriterAppender(lay,os);
		//设置记录器名称
		appender.setName(this.getClass().getName());
		log.setAdditivity(true);//覆盖记录，默认就是true
		//把输出到内存的日志输出器添加进去
		log.addAppender(appender);
	}
	public static void main(String[] args) {
		Logger log=Logger.getLogger("log");
		MemoryLog mlog=new MemoryLog(log);
		log.error("error1");
		mlog.off();
		log.error("error2");
		mlog.on();
		log.error("error3");
		System.out.println(mlog.getLogString());
	}
}
