/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jueb.tools.log.uiLog.ThreadMode;

import java.io.IOException;
import java.util.Scanner;

import javax.swing.JLabel;

/**
 * 
 * 类描述：
 * 不间断地扫描输入流
 * 将扫描到的字符流显示在JLabel上
 * @author 杨胜寒
 * @date 2011-12-20 创建
 * @version 1.0
 */
public class LabelLogThread extends LogAppender {

    private JLabel label;

    /**
     * 默认的构造
     * @param label 记录器名称，该记录器输出的日志信息将被截取并输出到指定的JLabel组件
     * @throws IOException 
     */
    public LabelLogThread(JLabel label) throws IOException {
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
    public LabelLogThread(JLabel label,String appenderName,String partten) throws IOException {
    	super(appenderName,partten);
        this.label = label;
    }

    @Override
    public void run() {
        // 不间断地扫描输入流
        Scanner scanner = new Scanner(reader);
        // 将扫描到的字符流显示在指定的JLabel上
        while (scanner.hasNextLine()) {
            try {
                //睡眠
                Thread.sleep(100);
                String line = scanner.nextLine();
                label.setText(line);
                line = null;
                 } catch (Exception ex) {
                //异常信息不作处理
            }
        }
    }
}
