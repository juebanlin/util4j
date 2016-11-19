package net.jueb.util4j.test.bytebuffTest;


import java.nio.charset.StandardCharsets;

import net.jueb.util4j.buffer.ArrayBytesBuff;
import net.jueb.util4j.bytesStream.bytes.HexUtil;

/**

类说�?:字节缓冲�?
*/

public final class ByteBuffer extends ArrayBytesBuff
{
	public ByteBuffer() {
		super();
	}

	public ByteBuffer(byte[] data) {
		super(data);
	}

	public ByteBuffer(int capacity) {
		super(capacity);
	}

	public void writeUTF(String str)
	{
		if(str==null)
		{
			writeByte(0);
		}else
		{
			writeByte(1);
			byte[] data=str.getBytes(StandardCharsets.UTF_8);
			writeInt(data.length);
			writeBytes(data);
		}
	}
	
	public String readUTF()
	{
		String str=null;
		byte has=readByte();
		if(has!=0)
		{
			int len=readInt();
			byte[] data=new byte[len];
			readBytes(data);
			str=new String(data,StandardCharsets.UTF_8);
		}
		return str;
	}
	
	public static void main(String[] args) {
		ByteBuffer buffer=new ByteBuffer(10);
		buffer.writeInt(10);
		buffer.writeInt(10);
		buffer.writeInt(10);
		buffer.writeInt(10);
		buffer.writeInt(10);
		buffer.writeUTF("123sd12s1nisan啊大大我的期望的");
		System.out.println(buffer.readableBytes());//20 可读字节
		System.out.println(buffer.capacity());//30字节容量
		System.out.println(buffer.length());//20占用字节�?
		System.out.println(buffer.readerIndex());//0当前可读位置
		byte[] data=buffer.getBytes();
		System.out.println(HexUtil.prettyHexDump(data));//20字节
		System.out.println(HexUtil.prettyHexDump(buffer.getRawBytes()));//30字节
	}
}