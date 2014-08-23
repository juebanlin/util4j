package net.jueb.tools.log.uiLog;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

/**
 * 此对象之间用户log4j的日志输出到UI，不单独使用线程
 * @author Administrator
 *
 */
public class JTextAreaLogAppender extends WriterAppender{
	
	private JTextAreaLogWriter jw;
	/**
	 * 
	 * @param name 附着器名字
	 * @param pattern 格式[%d{yyyy/MM/dd HH\:mm\:ss\:SSS}][%p]\:%n%m%n
	 * @param textArea 输出的目标空间
	 * @param scroll 可以为null
	 */
	public JTextAreaLogAppender(String name,String pattern,JTextArea textArea, JScrollPane scroll) {
		jw=new JTextAreaLogWriter(textArea, scroll);
		this.name=name;
		this.layout=new PatternLayout(pattern);//布局
	}
	@Override
	public void activateOptions() {
		setWriter(jw);
	}
	public static void initRootLogger(String name,String pattern,JTextArea textArea, JScrollPane scroll)
	{
		Logger.getRootLogger().addAppender(new JTextAreaLogAppender(name, pattern, textArea, scroll));
	}
}
