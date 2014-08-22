package net.jueb.tools.log;

import org.apache.log4j.Logger;

public class Log {

	public static Logger rootLog;
	
	static{
		rootLog=Logger.getRootLogger();
	}
	
	private Log() {
		
	}
	/**
	 * @param name ==>this.getClass().getName()
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static Logger getLog(String name)
	{
		return rootLog.getLogger(name);
	}
}
