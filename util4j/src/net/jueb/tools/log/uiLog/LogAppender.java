/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jueb.tools.log.uiLog;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

/**
 * 
 * 类描述：
 * 重置log4j的Appender的Writer
 * @author 杨胜寒
 * @date 2011-12-20 创建
 * @version 1.0
 */
public abstract class LogAppender extends Thread {

    protected PipedReader reader;
    protected Layout layout;
    protected Appender appender;
    
    /**
     * 使用代码创建打印器，
     * 默认为ConsolAppender打印器
     * 和PatternLayout布局
     * @param appenderName 打印器名字
     * @param pattern 打印格式 ：[%d{yyyy/MM/dd HH\:mm\:ss\:SSS}][%p]\:%n%m%n
     * @throws IOException
     */
    public LogAppender(String appenderName,String pattern) throws IOException {
        layout=new PatternLayout(pattern);//布局
        appender=new ConsoleAppender(layout);//
        appender.setName(appenderName);
        //删除同名的，防止配置文件中存在
        if(Logger.getRootLogger().getAppender(appenderName)!=null)
        {
        	 Logger.getRootLogger().removeAppender(appenderName);
        }
		Logger.getRootLogger().addAppender(appender);
        // 定义一个未连接的输入流管道
        reader = new PipedReader();
        // 定义一个已连接的输出流管理，并连接到reader
        Writer writer = new PipedWriter(reader);
        // 设置 appender 输出流
        ((WriterAppender) appender).setWriter(writer);
    }
    /**
     * 使用Log4j配置文件指定的打印器
     * @param appenderName 配置文件中打印器的名字
     * @throws IOException
     */
    public LogAppender(String appenderName) throws IOException {
        Logger root = Logger.getRootLogger();
        // 获取子记录器的输出源 
        appender=root.getAppender(appenderName);
        layout=appender.getLayout();
        // 定义一个未连接的输入流管道
        reader = new PipedReader();
        // 定义一个已连接的输出流管理，并连接到reader
        Writer writer = new PipedWriter(reader);
        // 设置 appender 输出流
        ((WriterAppender) appender).setWriter(writer);
    }
}
