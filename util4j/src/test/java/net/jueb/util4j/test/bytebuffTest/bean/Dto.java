package net.jueb.util4j.test.bytebuffTest.bean;

import net.jueb.util4j.test.bytebuffTest.ByteBuffer;

public interface Dto {

	public void readFrom(ByteBuffer buffer);
	
	public void writeTo(ByteBuffer buffer);
}
