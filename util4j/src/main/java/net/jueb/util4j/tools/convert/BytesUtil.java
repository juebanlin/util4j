package net.jueb.util4j.tools.convert;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class BytesUtil {

	/**
	 * 打印数组信息
	 * @param bytes
	 */
	public static final void showBytesHexStr(byte bytes[])
	{
		System.out.println(bytesHexStr(bytes));
	}
	
	/**
	 * 将字节流转换成十六进制字符串
	 * @param by
	 * @return
	 */
	public static final String bytesHexStr(byte bytes[])
	{
		return bytesHexStr(bytes, 0, bytes.length);
	}
	
	
	/**
	 * 将字节流转换成十六进制字符串
	 * @param bytes 数据
	 * @param offset 起始位置
	 * @param length 从起始位置开始的长度
	 * @return
	 */
	public static final String bytesHexStr(byte bytes[], int offset, int length)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		int rows = length / 16;//打印的行数
		int ac = length % 16;//剩余的字节数
		for (int i = 0; i < rows; ++i)
			ps.printf("%02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c\n", //
					bytes[offset + (16 * i) + 0], //
					bytes[offset + (16 * i) + 1], //
					bytes[offset + (16 * i) + 2], //
					bytes[offset + (16 * i) + 3], //
					bytes[offset + (16 * i) + 4], //
					bytes[offset + (16 * i) + 5], //
					bytes[offset + (16 * i) + 6], //
					bytes[offset + (16 * i) + 7], //
					bytes[offset + (16 * i) + 8], //
					bytes[offset + (16 * i) + 9], //
					bytes[offset + (16 * i) + 10], //
					bytes[offset + (16 * i) + 11], //
					bytes[offset + (16 * i) + 12], //
					bytes[offset + (16 * i) + 13], //
					bytes[offset + (16 * i) + 14], //
					bytes[offset + (16 * i) + 15], //
					toc(bytes[offset + (16 * i) + 0]), //
					toc(bytes[offset + (16 * i) + 1]), //
					toc(bytes[offset + (16 * i) + 2]), //
					toc(bytes[offset + (16 * i) + 3]), //
					toc(bytes[offset + (16 * i) + 4]), //
					toc(bytes[offset + (16 * i) + 5]), //
					toc(bytes[offset + (16 * i) + 6]), //
					toc(bytes[offset + (16 * i) + 7]), //
					toc(bytes[offset + (16 * i) + 8]), //
					toc(bytes[offset + (16 * i) + 9]), //
					toc(bytes[offset + (16 * i) + 10]), //
					toc(bytes[offset + (16 * i) + 11]), //
					toc(bytes[offset + (16 * i) + 12]), //
					toc(bytes[offset + (16 * i) + 13]), //
					toc(bytes[offset + (16 * i) + 14]), //
					toc(bytes[offset + (16 * i) + 15]));
		for (int i = 0; i < ac; i++)
			ps.printf("%02X ", bytes[offset + rows * 16 + i]);
		for (int i = 0; ac > 0 && i < 16 - ac; i++)
			ps.printf("%s", "   ");
		for (int i = 0; i < ac; i++)
			ps.printf("%c", toc(bytes[offset + rows * 16 + i]));
		return bos.toString();
	}
/** 返回可打印字符. */
	private static final char toc(byte chr)
	{
		return (chr > 0x20 && chr < 0x7F) ? (char) chr : '.';
	}
}
