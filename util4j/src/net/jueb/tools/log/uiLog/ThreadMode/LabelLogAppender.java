package net.jueb.tools.log.uiLog.ThreadMode;
import java.io.IOException;
import javax.swing.JLabel;

public class LabelLogAppender extends LogAppender {

    private JLabel label;

    /**
     * 默认的构造
     * @param label 记录器名称，该记录器输出的日志信息将被截取并输出到指定的JLabel组件
     * @throws IOException 
     */
    public LabelLogAppender(JLabel label) throws IOException {
        super("label");
        this.label = label;
    }
    /**
     * 使用代码配置日志输出
     * @param label
     * @param appenderName 打印器名字
     * @param partten 打印格式 ：[%d{yyyy/MM/dd HH\:mm\:ss\:SSS}][%p]\:%n%m%n
     * @throws IOException
     */
    public LabelLogAppender(JLabel label,String appenderName,String partten) throws IOException {
    	super(appenderName,partten);
        this.label = label;
    }

    @Override
	public void doOutLog(String log) {
		label.setText(log);
	}
}
