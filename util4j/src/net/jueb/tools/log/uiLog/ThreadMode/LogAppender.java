/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jueb.tools.log.uiLog.ThreadMode;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;


public abstract class LogAppender extends Thread {

    private PipedReader reader;
    private Layout layout;
    private Appender appender;
    private LinkedBlockingQueue<String> logs=new LinkedBlockingQueue<String>();//日志队列
    
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
    
    @Override
    public final void run() {
    	//创建日志提前输出线程
    	Thread outLogs=new Thread(){
        	public void run() {
        		while(true)
        		{
        			if(logs.isEmpty())
        			{//如果日志队列为空
        				try {
							Thread.sleep(100);//睡眠
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
        				continue ;
        			}else
        			{
        				String log=logs.poll();
        				doOutLog(log);
        			}
        		}
        	};
        };
        outLogs.start();//启动该线程
        //继续当前的日志收集线程
    	@SuppressWarnings("resource")
		Scanner scanner = new Scanner(reader);
        while (scanner.hasNextLine()) {
            try {
//	             Thread.sleep(100);//睡眠
	             String log = scanner.nextLine();
	             this.logs.add(log);//加入日志
                } catch (Exception e) {
                	e.printStackTrace();
            }    
        }
    }
    
    
    
    
    /**
     * 父类的2个线程，一个线程负责收集日志并队列，另一个线程则负责取出日志并输出
     * 该方法是具体的输出方法
     * @param log 该日志未换行处理
     */
    protected abstract void doOutLog(String log);
    
}
