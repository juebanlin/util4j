package net.jueb.util4j.buffer.tool.demo;

import net.jueb.util4j.buffer.ByteBuffer;

public interface Dto {
	
	public void readFrom(ByteBuffer buff);
	
	public void writeTo(ByteBuffer buff);
}
