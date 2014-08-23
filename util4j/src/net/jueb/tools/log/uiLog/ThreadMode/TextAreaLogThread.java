/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jueb.tools.log.uiLog.ThreadMode;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * 注意：appender的名字要在配置文件中
 * log4j.rootLogger=info,myconsol,textArea
 * 类描述：
 * 不间断地扫描输入流
 * 将扫描到的字符流显示在JTextArea上
 * @author 杨胜寒
 * @date 2011-12-20 创建
 * @version 1.0
 */
public class TextAreaLogThread extends LogAppender {

    private JTextArea textArea;
    private JScrollPane scroll;

    /**
     * 默认的构造
     * @param textArea 记录器名称，该记录器输出的日志信息将被截取并输出到指定的JTextArea组件
     * @param scroll JTextArea组件使用的滚动面板，因为在JTextArea中输出日志时，默认会使垂直滚动条自动向下滚动，若不需要此功能，此参数可省略
     * @throws IOException 
     */
    public TextAreaLogThread(JTextArea textArea, JScrollPane scroll) throws IOException {
        super("textArea");
        this.textArea = textArea;
        this.scroll = scroll;
    }
    /**
     * 使用代码配置日志
     * @param textArea
     * @param scroll
     * @param appenderName 打印器名字
     * @param partten 打印格式 ：[%d{yyyy/MM/dd HH\:mm\:ss\:SSS}][%p]\:%n%m%n
     * @throws IOException
     */
    public TextAreaLogThread(JTextArea textArea, JScrollPane scroll,String appenderName,String partten) throws IOException {
        super(appenderName,partten);
        this.textArea = textArea;
        this.scroll = scroll;
    }
    public TextAreaLogThread(JTextArea textArea) throws IOException {
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
    public TextAreaLogThread(JTextArea textArea,String appenderName,String partten) throws IOException {
    	super(appenderName,partten);
        this.textArea = textArea;
    }

    @Override
    public void run() {
        // 不间断地扫描输入流
        Scanner scanner = new Scanner(reader);
        // 将扫描到的字符流输出到指定的JTextArea组件
        while (scanner.hasNextLine()) {
            try {
	                //睡眠
//	                Thread.sleep(100);
	                String line = scanner.nextLine();
	                textArea.append(line);
	                textArea.append("\n");
	                line = null;
	                if(scroll!=null)
	                {//使垂直滚动条自动向下滚动
	                	scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
	                }
                } catch (Exception ex) {
            }
        }
    }
}
