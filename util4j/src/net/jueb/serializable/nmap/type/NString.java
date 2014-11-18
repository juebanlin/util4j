package net.jueb.serializable.nmap.type;

import net.jueb.serializable.nmap.falg.Flag;

public class NString extends NType<String>{

	public NString(String obj) {
		super(obj, Flag.Head.NString, Flag.End.NString);
	}

	@Override
	public byte[] getObjectBytes() {
		return obj.getBytes();
	}

	@Override
	public String getString() {
		return obj;
	}

}
