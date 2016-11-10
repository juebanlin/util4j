package net.jueb.util4j.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.jueb.util4j.bytesStream.bytes.HexUtil;

/**

类说明:字节缓冲区
*/

public final class SimpleBytesBuff implements Cloneable
{
	/**当前读的位置*/
	private int readPos;
	/**当前写的位置*/
	private int writePos;
	/**内容数组*/
	private byte[] data;
	/**数字缓冲的数组*/
	private byte[] numData = new byte[8];

	/**构造一个缓冲区,默认长度是1024*/
	public SimpleBytesBuff()
	{
		this(50);
	}
	/**构造指定的长度的缓冲区*/
	public SimpleBytesBuff(int count)
	{
		data = new byte[count];
	}
	/**用指定的数组构造缓冲区*/
	public SimpleBytesBuff(byte[] data)
	{
		this(data, 0, data.length);
	}
	/**用指定的数组,开始位置和长度构造缓冲区*/
	public SimpleBytesBuff(byte[] data, int start, int len)
	{
		this.data = data;
		readPos = start;
		writePos = start + len;
	}
	/**写逻辑值*/
	public void writeBoolean(boolean b)
	{
		writeByte(b ? 1 : 0);
	}
	/**写字节序列*/
	public void writeByte(int value)
	{
		writeNumber(value, 1);
	}
	/**写整型数组*/
	public void writeIntArr(int[] value)
	{
		for(int n:value)
		{
			writeNumber(n, 4);
		}
	}
	/**写整型数组*/
	public void writeIntArr(int[] value, boolean flag)
	{
		if(flag)
			writeNumber(value.length, 4);
		for(int n:value)
		{
			writeNumber(n, 4);
		}
	}
	/**写整型数组,start跳过的个数*/
	public void writeIntArr(int[] value, int  start)
	{
		int count=0;
		for(int n:value)
		{
			count++;
			if(start>=count) continue;
			
			writeNumber(n, 4);
		}
	}
	/**写字节序列*/
	public void writeBytes(byte[] bytes)
	{
		writeBytes(bytes, 0, bytes.length);
	}
	/**写字节序列*/
	public void writeBytes(byte[] bytes, int offset, int count)
	{
		ensureCapacity(writePos + count);
		for (int i = 0; i < count; i++)
			data[writePos++] = bytes[offset++];
	}
	/**写一个短整数*/
	public void writeShort(int value)
	{
		writeNumber(value, 2);
	}
	/**写一个字符*/
	public void writeChar(char value)
	{
		writeNumber(value, 2);
	}
	/**写一个整数*/
	public void writeInt(int value)
	{
		writeNumber(value, 4);
	}
	
	public void writeFloat(float value)
	{
		writeInt(Float.floatToRawIntBits(value));
	}
	
	/**写一个长整数*/
	public void writeLong(long value)
	{
		writeNumber(value, 8);
	}
	
	public void writeDouble(double value)
	{
		writeLong(Double.doubleToRawLongBits(value));
	}
	
	/**写一个字符串*/
	public void writeString(String s)
	{
		if (s == null || s.length() == 0)
		{
			writeShort(0);
		}
		else
		{
            if (s.length() > 32767)
                throw new IllegalArgumentException("short string over flow");
			int count = s.length();
			writeShort(count);
			for (int i = 0; i < count; i++)
				writeChar(s.charAt(i));
		}
	}
	/**写一个短字符串*/
	public void writeShortString(String s)
	{
		if (s == null || s.length() == 0)
		{
			writeByte(0);
		}
		else
		{
			if (s.length() > 255)
				throw new IllegalArgumentException("short string over flow");
			int count = s.length();
			writeByte(count);
			for (int i = 0; i < count; i++)
				writeChar(s.charAt(i));
		}
	}
	/**
	 * 写UTF
	 */
	public void writeUTF(String s)
	{
		if (s == null)
			s = "";
		int strlen = s.length();
		int utflen = 0;
		for (int i = 0; i < strlen; i++)
		{
			char c = s.charAt(i);
			if (c < 127)
				utflen++;
			else if (c > 2047)
				utflen += 3;
			else
				utflen += 2;
		}
		if (utflen > 65535)
			throw new IllegalArgumentException("the string is too long:" + strlen);

		ensureCapacity(utflen + 2 + writePos);
		writeShort(utflen);
		for (int i = 0; i < strlen; i++)
		{
			char c = s.charAt(i);
			if (c < 127)
			{
				data[writePos++] = (byte)c;
			}
			else if (c > 2047)
			{
				data[writePos++] = (byte)(0xE0 | ((c >> 12) & 0x0F));
				data[writePos++] = (byte)(0x80 | ((c >> 6) & 0x3F));
				data[writePos++] = (byte)(0x80 | ((c >> 0) & 0x3F));
			}
			else
			{
				data[writePos++] = (byte)(0xC0 | ((c >> 6) & 0x1F));
				data[writePos++] = (byte)(0x80 | ((c >> 0) & 0x3F));
			}
		}
	}
	/**
	 * 写一个ByteBuffer
	 */
	public void writeByteBuffer(SimpleBytesBuff buffer)
	{
		writeByteBuffer(buffer, buffer.available());
	}
	/**
	 * 写一个ByteBuffer的指定长度的内容
	 */
	public void writeByteBuffer(SimpleBytesBuff buffer,int count)
	{
		count = Math.min(count, buffer.available());
		ensureCapacity(length() + count);
		for (int i = 0; i < count; i++)
			data[writePos++] = buffer.data[buffer.readPos++];
    }
	/**读逻辑值*/
	public boolean readBoolean()
	{
		return readByte() != 0;
	}
	/**读一个字节*/
	public int readByte()
	{
		return data[readPos++];
	}
	/**读一个无符号字节*/
	public int readUnsignedByte()
	{
		return data[readPos++] & 0xff;
	}
	/**读指定长度的字节数组*/
	public byte[] readBytes(int count)
	{
		byte[] dest = new byte[count];
		for (int i = 0; i < count; i++)
			dest[i] = data[readPos++];
		return dest;
	}
	/**读一个短整数*/
	public int readShort()
	{
		return (short)(readNumber(2) & 0xffff);
	}
	/**读一个字符*/
	public char readChar()
	{
		return (char)(readNumber(2) & 0xffff);
	}
	/**读一个无符号短整数*/
	public int readUnsignedShort()
	{
		return (int)(readNumber(2) & 0xffff);
	}
	/**读一个整数*/
	public int readInt()
	{
		return (int)(readNumber(4) & 0xffffffff);
	}
	/**读一个长整数*/
	public long readLong()
	{
		return readNumber(8);
	}
	
	public double readFloat()
	{
		return Float.intBitsToFloat(readInt());
	}
	
	public double readDouble()
	{
		return Double.longBitsToDouble(readLong());
	}
	
	/**读出一个短字符串*/
	public String readShortString()
	{
		int len = readUnsignedByte();
        if (len == 0)
            return "";
		StringBuffer buff = new StringBuffer(len);
		for (int i = 0; i < len; i++)
		{
			buff.append(readChar());
		}
		return buff.toString();
	}
	/**读出一个字符串*/
	public String readString()
	{
		int len = readUnsignedShort();
		if (len == 0)
			return "";
		StringBuffer buff = new StringBuffer(len);
		for (int i = 0; i < len; i++)
		{
			buff.append(readChar());
		}
		return buff.toString();
	}
	/**
	 * 读UTF
	 */
	public String readUTF()
	{
//		int b_length = this.length();
		int utflen = readUnsignedShort();
		if (utflen == 0)
			return "";
		char[] charArray = new char[utflen];
		int count = 0;
		int b1 = 0, b2 = 0, b3 = 0;
		int endpos = readPos + utflen;
		while (readPos < endpos)
		{
			b1 = data[readPos++] & 0xff;
			if (b1 < 127)
			{
				charArray[count++] = (char)b1;
			}
			else if ((b1 >>5) == 7)
			{
				b2 = data[readPos++];
				b3 = data[readPos++];
				charArray[count++] = (char)((b1 & 0xf) << 12 | (b2 & 0x3f) << 6 | (b3 & 0x3f));
			}
			else
			{
				b2 = data[readPos++];
				charArray[count++] = (char)((b1 & 0x1f) << 6 | (b2 & 0x3f));
			}
		}
		return new String(charArray, 0, count);
	}
	/**从输入流读出*/
	public void readFrom(InputStream in)throws IOException
	{
		readFrom(in, capacity() - length());
	}
	/**从输入流读出*/
	public void readFrom(InputStream in, int len)throws IOException
	{
		ensureCapacity(writePos + len);
		for (int i = 0; i < len; i++)
			data[writePos++] = (byte)in.read();
	}
	/**将缓冲区的内容写到输出流*/
	public void writeTo(OutputStream out)throws IOException
	{
		int count = available();
		for (int i = 0; i < count; i++)
			out.write(data[readPos++]);
	}
	/**缓冲区大小*/
	public int capacity()
	{
		return data.length;
	}
	/**缓冲区内容长度*/
	public int length()
	{
		return writePos;
	}
    public int position()
    {
        return readPos;
    }
    public void position(int pos)
    {
		if (pos >= 0 && pos <= writePos)
			readPos = pos;
		else
			throw new IllegalArgumentException("position out of range:" + pos);
    }
	/**获得字节内容*/
	public byte[] getBytes()
	{
		byte[] bytes = new byte[length()];
		System.arraycopy(data, 0, bytes, 0, bytes.length);
		return bytes;
	}
	/**获得字节内容*/
	public byte[] getRawBytes()
	{
		return data;
	}
	/**有效字节数*/
	public int available()
	{
		return writePos - readPos;
	}
	/**清除缓冲区*/
	public void clear()
	{
		writePos = readPos = 0;
	}
	/**重置缓冲区,使它可以重新被读*/
	public void reset()
	{
		readPos = 0;
	}
	/**
	 * 压缩缓冲区
	 */
	public void pack()
	{
		if (readPos == 0)
			return;
		int count = available();
		for (int i = 0; i < count; i++)
		{
			data[i] = data[readPos++];
		}
		readPos = 0;
		writePos = count;
	}
	/**把字节序列转换成字符串*/
	public String toString()
	{
		return new String(data, 0, writePos);
	}
	/**把自己的内容复制一个新的ByteBuffer*/
	public Object clone()
	{
		SimpleBytesBuff buff = new SimpleBytesBuff(writePos);
		System.arraycopy(data, 0, buff.data, 0, writePos);
		buff.writePos = writePos;
		buff.readPos = readPos;
		return buff;
	}
	
	/**
	 * 复制一份
	 * @return
	 */
	public SimpleBytesBuff copy()
	{
		SimpleBytesBuff newBuff = new SimpleBytesBuff(writePos);
		newBuff.writePos = writePos;
		newBuff.readPos = readPos;
		System.arraycopy(data, 0, newBuff.data, 0, writePos);
		return newBuff;
	}

	/**写字节序列到缓冲区*/
	private void write(byte[] bytes, int offset, int len, int start)
	{
		ensureCapacity(start + len);
		System.arraycopy(bytes, offset, data, start, len);
	}
	/**冲缓冲区读出字节序列*/
	@SuppressWarnings("unused")
	private void read(byte[] bytes, int offset, int len, int start)
	{
		System.arraycopy(data, start, bytes, offset, len);
	}
	/**保证缓冲区的大小*/
	private void ensureCapacity(int count)
	{
		if (count > data.length)
		{
			byte[] tmp = new byte[count*3/2];
			System.arraycopy(data, 0, tmp, 0, writePos);
			data = tmp;
		}
	}
	
	
	/**写一个数字到缓冲区*/
	private void writeNumber(long value, int bytes)
	{
		for (int i = 0; i < bytes; i++)
		{
			numData[i] = (byte)(value >> i * 8);
		}
		write(numData, 0, bytes, writePos);
		writePos += bytes;
	}
	/**从缓冲区读一个数字到数字缓冲区*/
	private long readNumber(int bytes)
	{
		for (int i = 0; i < bytes; i++)
			numData[i] = data[readPos++];
		long value = 0;
		for (int i = 0; i < bytes; i++)
		{
			value |= (long)(numData[i] & 0xff) << (i * 8);
		}
		return value;
	}
	
	/**
	 * 读取给定长度字节序列，但不移动读取游标位置
	 * @param count
	 * @return byte[]
	 */
	public byte[] readByteNoMove(int count) {
		byte[] tmp = new byte[count];
		
		System.arraycopy(data, readPos, tmp, 0, count);
		
		return tmp;
	}
	public static void main(String[] args) {
		SimpleBytesBuff buffer=new SimpleBytesBuff(10);
		buffer.writeInt(10);
		buffer.writeInt(10);
		buffer.writeInt(10);
		buffer.writeInt(10);
		buffer.writeInt(10);
		buffer.writeUTF("123sd12s1nisan啊大大我的期望的");
		System.out.println(buffer.available());//20 可读字节
		System.out.println(buffer.capacity());//30字节容量
		System.out.println(buffer.length());//20占用字节数
		System.out.println(buffer.position());//0当前可读位置
		byte[] data=buffer.getBytes();
		System.out.println(HexUtil.prettyHexDump(data));//20字节
		System.out.println(HexUtil.prettyHexDump(buffer.getRawBytes()));//30字节
	}
}