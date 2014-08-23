package net.jueb.tools.log.uiLog;

import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public  class UiLogThreadFactory {
	
	/**
	 * 使用配置文件配置的打印器输出到textArea
	 * @param textArea
	 * @return
	 * @throws IOException
	 */
	public static TextAreaLogThread getTextAreaLogAppender(JTextArea textArea) throws IOException
	{
		return new TextAreaLogThread(textArea);
	}
	/**
	 * 使用代码配置的打印器输出到控件
	 * @param textArea
	 * @param appenderName
	 * @param partten 打印格式 ：[%d{yyyy/MM/dd HH\:mm\:ss\:SSS}][%p]\:%n%m%n
	 * @return
	 * @throws IOException
	 */
	public static TextAreaLogThread getTextAreaLogAppender(JTextArea textArea,String appenderName,String partten) throws IOException
	{
		return new TextAreaLogThread(textArea, appenderName, partten);
	}
	/**
	 * 使用配置文件配置的打印器输出到textArea,并自动滚动
	 * @param textArea
	 * @param scroll
	 * @return
	 * @throws IOException
	 */
	public static TextAreaLogThread getTextAreaLogAppender(JTextArea textArea,JScrollPane scroll) throws IOException
	{
		return new TextAreaLogThread(textArea, scroll);
	}
	/**
	 * 使用代码配置的打印器输出到textArea并自动滚动
	 * @param textArea
	 * @param scroll
	 * @param appenderName
	 * @param partten 打印格式 ：[%d{yyyy/MM/dd HH\:mm\:ss\:SSS}][%p]\:%n%m%n
	 * @return
	 * @throws IOException
	 */
	public static TextAreaLogThread getTextAreaLogAppender(JTextArea textArea,JScrollPane scroll,String appenderName,String partten) throws IOException
	{
		return new TextAreaLogThread(textArea,scroll, appenderName, partten);
	}
	/**
	 * 使用代码配置的打印器输出到标签
	 * @param label
	 * @return
	 * @throws IOException 
	 */
	public static LabelLogThread getLabelLogAppender(JLabel label) throws IOException
	{
		return new LabelLogThread(label);
	}
	/**
	 * 使用代码配置的打印器输出到标签
	 * @param label
	 * @param appenderName
	 * @param partten 打印格式 ：[%d{yyyy/MM/dd HH\:mm\:ss\:SSS}][%p]\:%n%m%n
	 * @throws IOException 
	 */
	public static LabelLogThread getLabelLogAppender(JLabel label,String appenderName,String partten) throws IOException
	{
		return new LabelLogThread(label, appenderName, partten);
	}
}
