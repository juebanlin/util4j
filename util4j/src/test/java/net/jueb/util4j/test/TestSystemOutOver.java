package net.jueb.util4j.test;

import java.io.PrintStream;

import org.apache.logging.log4j.LogManager;

import net.jueb.util4j.log.log4j.Log4jUtil;

public class TestSystemOutOver {

	public void test() {
		System.out.println("hello world");
		System.setOut(new PrintStream(System.out) {
    		@Override
    		public void println(String x) {
    			StackTraceElement[] ste=Thread.currentThread().getStackTrace();
    			String line=ste[2].toString();
    			LogManager.getFormatterLogger("console to log").info(line+" - "+x);
    			LogManager.getFormatterLogger(Thread.currentThread()).info(line + " - " + x);
    		}
    	});
		System.out.println("hello world");
	}
	
	public static void main(String[] args) {
		Log4jUtil.initLogConfig("log4j2.xml");
		new TestSystemOutOver().test();
	}
}
