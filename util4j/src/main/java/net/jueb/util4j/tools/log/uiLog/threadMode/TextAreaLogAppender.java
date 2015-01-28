package net.jueb.util4j.tools.log.uiLog.threadMode;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextAreaLogAppender extends LogAppender {

    private JTextArea textArea;
    private JScrollPane scroll;
    private boolean isScroll;

    /**
     * 默认的构造
     * @param textArea 记录器名称，该记录器输出的日志信息将被截取并输出到指定的JTextArea组件
     * @param scroll JTextArea组件使用的滚动面板，因为在JTextArea中输出日志时，默认会使垂直滚动条自动向下滚动，若不需要此功能，此参数可省略
     * @throws IOException 
     */
    public TextAreaLogAppender(JTextArea textArea, JScrollPane scroll) throws IOException {
        super("textArea");
        this.textArea = textArea;
        this.scroll = scroll;
        if(this.scroll!=null)
        {
        	this.isScroll=true;
        }
    }
    /**
     * 使用代码配置日志
     * @param textArea
     * @param scroll
     * @param appenderName 打印器名字
     * @param partten 打印格式 ：[%d{yyyy/MM/dd HH\:mm\:ss\:SSS}][%p]\:%n%m%n
     * @throws IOException
     */
    public TextAreaLogAppender(JTextArea textArea, JScrollPane scroll,String appenderName,String partten) throws IOException {
        super(appenderName,partten);
        this.textArea = textArea;
        this.scroll = scroll;
        if(this.scroll!=null)
        {
        	this.isScroll=true;
        }
    }
    public TextAreaLogAppender(JTextArea textArea) throws IOException {
        super("textArea");
        this.textArea = textArea;
    }
    /**
     * 使用代码配置日志输出
     * @param textArea
     * @param appenderName 打印器名字
     * @param partten 打印格式 ：[%d{yyyy/MM/dd HH\:mm\:ss\:SSS}][%p]\:%n%m%n
     * @throws IOException
     */
    public TextAreaLogAppender(JTextArea textArea,String appenderName,String partten) throws IOException {
    	super(appenderName,partten);
        this.textArea = textArea;
    }
	@Override
	public void doOutLog(final String log) {
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() { //向UI线程发消息。
			    @Override
				public void run() {
			    	textArea.append(log+"\n");
			        if(isScroll)
			        {//使垂直滚动条自动向下滚动
			        	scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
			        }
			    }
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
