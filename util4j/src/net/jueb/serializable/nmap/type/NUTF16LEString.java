package net.jueb.serializable.nmap.type;

import java.io.UnsupportedEncodingException;

import net.jueb.serializable.nmap.falg.Flag;

public class NUTF16LEString extends NType<String>{

	public NUTF16LEString(String obj) {
		super(obj, Flag.Head.NString, Flag.End.NString);
	}

	@Override
	public byte[] getObjectBytes() {
		try {
			return obj.getBytes("UTF-16LE");
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
