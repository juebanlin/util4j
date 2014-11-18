package net.jueb.serializable.nmap.type;

import java.util.Arrays;

import net.jueb.serializable.nmap.falg.Flag;

/**
 * 字节数组以头+长度+数据+尾巴
 * @author Administrator
 *
 */
public class NByteArray extends NType<byte[]>{

	public NByteArray(byte[] obj) {
		super(obj, Flag.Head.NByteArray, Flag.End.NByteArray);
	}
	@Override
	public byte[] getBytes() {
		return addByteArray(getFlagHead(),tb.IntegerToByteArray(obj.length),getFlagEnd());
	}
	@Override
	public byte[] getObjectBytes() {
		return obj;
	}
	@Override
	public String getString() {
		return "ByteArray["+Arrays.toString(obj)+"]";
	}

}
