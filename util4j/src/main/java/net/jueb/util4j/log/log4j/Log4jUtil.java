package net.jueb.util4j.log.log4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.spi.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 * @author Administrator
 */
public class Log4jUtil {
	
	public static Logger getLogger(Class<?> c)
	{
		return LoggerFactory.getLogger(c);
	}
	
	/**
	 * 根据配置文件路径初始化日志配置
	 * @param logConfigpath
	 */
	public static void initLogConfig(String logConfigpath)
	{
		try {
			LoggerContext context =LogManager.getContext(false);
			if(context!=null)
			{
				if(context instanceof org.apache.logging.log4j.core.LoggerContext){
					org.apache.logging.log4j.core.LoggerContext ctx=(org.apache.logging.log4j.core.LoggerContext)context;
					URI uri = new File(logConfigpath).toURI();
					ctx.setConfigLocation(uri);
					ctx.reconfigure();//重新初始化Log4j2的配置上下文
				}
			}else
			{
				ConfigurationSource source = new ConfigurationSource(new FileInputStream(logConfigpath));
				Configurator.initialize(null, source);
			}
			Logger log=LoggerFactory.getLogger(Log4jUtil.class);
			log.info("日志配置初始化完成:"+logConfigpath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	/**
	 * 根据配置文件路径初始化日志配置
	 * @param logConfigpath
	 */
	public static void reInitLogConfig(String logConfigpath)
	{
		try {
			 LoggerContext context =LogManager.getContext(false);
			if(context instanceof org.apache.logging.log4j.core.LoggerContext){
				org.apache.logging.log4j.core.LoggerContext ctx=(org.apache.logging.log4j.core.LoggerContext)context;
				ctx.setConfigLocation(new File(logConfigpath).toURI());
				ctx.reconfigure();//重新初始化Log4j2的配置上下文
				Logger log=LoggerFactory.getLogger(Log4jUtil.class);
				log.info("日志配置重新初始化完成:"+logConfigpath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
}
