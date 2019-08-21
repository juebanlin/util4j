package net.jueb.util4j.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestProto {

	public static void main(String[] args) throws InterruptedException {
		Logger log=LoggerFactory.getLogger("");
		for(int i=0;i<100000;i++)
		{
			log.info("elk test");
			Thread.sleep(500);
		}
	}
}
