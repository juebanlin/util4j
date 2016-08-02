package net.jueb.util4j.beta.tools.log.uiLog.normalThread;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

public abstract class LogAppenderThread extends Thread{
	private WriterAppender wp;
	private ConcurrentLinkedQueue<String> logs=new ConcurrentLinkedQueue<String>();//日志队列
	
	public LogAppenderThread(String name,String pattern) {
		wp=new WriterAppender(new PatternLayout(pattern), new UiLogWriter());
		wp.setName(name);
//		wp.setLayout(new PatternLayout(pattern));
//		wp.setWriter(new UiLogWriter());
		Logger.getRootLogger().addAppender(wp);
	}
	
	@Override
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
	}
	
	private class UiLogWriter extends Writer
	{

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			String log=new String(cbuf, off, len);
			logs.add(log);
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
	
	/**
	 * 建议使用javax.swing.SwingUtilities.invokeAndWait
	 * @param log
	 */
	protected abstract void doOutLog(String log);
}
