package net.jueb.serializable.nmap.type;

import java.io.UnsupportedEncodingException;

import net.jueb.serializable.nmap.falg.Flag;

public class NUTF8String extends NType<String>{

	public NUTF8String(String obj) {
		super(obj, Flag.Head.NUTF8String, Flag.End.NUTF8String);
	}

	@Override
	public byte[] getObjectBytes() {
		try {
			return obj.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new byte[]{};
		}
	}

	@Override
	public String getString() {
		return obj;
	}

}
