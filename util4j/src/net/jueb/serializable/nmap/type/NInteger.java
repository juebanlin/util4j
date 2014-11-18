package net.jueb.serializable.nmap.type;

import net.jueb.serializable.nmap.falg.Flag;

public class NInteger extends NType<Integer>{
	public NInteger(int i) {
		super(i, Flag.Head.NInteger, Flag.End.NInteger);
	}

	@Override
	public byte[] getObjectBytes() {
		return tb.IntegerToByteArray(obj);
	}

	@Override
	public String getString() {
		return Integer.toString(obj);
	}

}
