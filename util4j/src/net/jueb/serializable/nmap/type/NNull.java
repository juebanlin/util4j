package net.jueb.serializable.nmap.type;

import net.jueb.serializable.nmap.falg.Flag;

public final class NNull extends NType<Object>{

	public NNull() 
	{
		super(null,Flag.Head.NNull,Flag.End.NNull);
	}

	@Override
	public byte[] getBytes() {
		return getFlagEnd();
	}
	
	@Override
	public byte[] getObjectBytes() {
		return getFlagEnd();
	}

	@Override
	public String getString() {
		return "NULL";
	}

}
