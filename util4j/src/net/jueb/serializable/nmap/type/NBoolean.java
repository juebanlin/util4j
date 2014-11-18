package net.jueb.serializable.nmap.type;

import net.jueb.serializable.nmap.falg.Flag;

public final class NBoolean extends NType<Boolean>{

	public NBoolean(Boolean obj) 
	{
		super(obj,obj?Flag.Head.NTrue:Flag.Head.NFalse,obj?Flag.End.NTrue:Flag.End.NFalse);
	}

	@Override
	public byte[] getObjectBytes() {
		return new byte[]{};
	}

	@Override
	public String getString() {
		return obj.toString();
	}

}
