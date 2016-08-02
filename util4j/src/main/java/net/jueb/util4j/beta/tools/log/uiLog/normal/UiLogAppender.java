package net.jueb.util4j.beta.tools.log.uiLog.normal;

import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

public abstract class UiLogAppender extends WriterAppender{

	/**
	 * 
	 * @param name 附着器名字
	 * @param pattern 格式[%d{yyyy/MM/dd HH\:mm\:ss\:SSS}][%p]\:%n%m%n
	 * @param textArea 输出的目标空间
	 * @param scroll 可以为null
	 */
	public UiLogAppender(String name,String pattern) {
		super.setName(name);
		super.setLayout(new PatternLayout(pattern));
		super.setWriter(new UiLogWriter());
	}
	
	protected abstract void doOutLog(String log);
	
	public static void initRootLogger(UiLogAppender appender)
	{
		Logger.getRootLogger().addAppender(appender);
	}
	
	private class UiLogWriter extends Writer
	{
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			String log=new String(cbuf, off, len);
			doOutLog(log);
		}

		@Override
		public void flush() throws IOException {
			// TODO Auto-generated method stub
		}

		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub
		}
		
	}
}
