package net.jueb.util4j.tools.convert;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class BytesUtil {

	/**
	 * 将字节流转换成十六进制字符串
	 * @param by
	 * @return
	 */
	public static final String printBytes(byte by[])
	{
		return printBytes(by, 0, by.length);
	}
	
	
	/**
	 * 将字节流转换成十六进制字符串
	 * @param by 数据
	 * @param offset 起始位置
	 * @param length 从起始位置开始的长度
	 * @return
	 */
	public static final String printBytes(byte by[], int offset, int length)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		int rows = length / 16;//打印的行数
		int ac = length % 16;//剩余的字节数
		for (int i = 0; i < rows; ++i)
			ps.printf("%02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %c%c%c%c%c%c%c%c%c%c%c%c%c%c%c%c\n", //
					by[offset + (16 * i) + 0], //
					by[offset + (16 * i) + 1], //
					by[offset + (16 * i) + 2], //
					by[offset + (16 * i) + 3], //
					by[offset + (16 * i) + 4], //
					by[offset + (16 * i) + 5], //
					by[offset + (16 * i) + 6], //
					by[offset + (16 * i) + 7], //
					by[offset + (16 * i) + 8], //
					by[offset + (16 * i) + 9], //
					by[offset + (16 * i) + 10], //
					by[offset + (16 * i) + 11], //
					by[offset + (16 * i) + 12], //
					by[offset + (16 * i) + 13], //
					by[offset + (16 * i) + 14], //
					by[offset + (16 * i) + 15], //
					toc(by[offset + (16 * i) + 0]), //
					toc(by[offset + (16 * i) + 1]), //
					toc(by[offset + (16 * i) + 2]), //
					toc(by[offset + (16 * i) + 3]), //
					toc(by[offset + (16 * i) + 4]), //
					toc(by[offset + (16 * i) + 5]), //
					toc(by[offset + (16 * i) + 6]), //
					toc(by[offset + (16 * i) + 7]), //
					toc(by[offset + (16 * i) + 8]), //
					toc(by[offset + (16 * i) + 9]), //
					toc(by[offset + (16 * i) + 10]), //
					toc(by[offset + (16 * i) + 11]), //
					toc(by[offset + (16 * i) + 12]), //
					toc(by[offset + (16 * i) + 13]), //
					toc(by[offset + (16 * i) + 14]), //
					toc(by[offset + (16 * i) + 15]));
		for (int i = 0; i < ac; i++)
			ps.printf("%02X ", by[offset + rows * 16 + i]);
		for (int i = 0; ac > 0 && i < 16 - ac; i++)
			ps.printf("%s", "   ");
		for (int i = 0; i < ac; i++)
			ps.printf("%c", toc(by[offset + rows * 16 + i]));
		return bos.toString();
	}
/** 返回可打印字符. */
	private static final char toc(byte chr)
	{
		return (chr > 0x20 && chr < 0x7F) ? (char) chr : '.';
	}
}
