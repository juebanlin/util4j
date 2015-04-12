package net.jueb.util4j.classLoader.loader.test;

import org.apache.log4j.Logger;

/**
 *@author juebanlin
 *@email juebanlin@gmail.com
 *@createTime 2015年4月12日 下午8:03:20
 **/
public class TestObj implements Runnable{

	Logger log=Logger.getLogger(getClass());
	private int version=1;
	
	@Override
	public void run() {
		String info="version="+version+",classLoader="+getClass().getClassLoader();
		log.info(info);
	}
	
}
