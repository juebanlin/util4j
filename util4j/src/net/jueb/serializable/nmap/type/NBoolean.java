package net.jueb.serializable.nmap.type;

import net.jueb.serializable.nmap.falg.Flag;
import net.jueb.tools.convert.typebytes.TypeBytesInputStream;

/**
 * 用头表示
 * @author Administrator
 *
 */
public final class NBoolean extends NType<Boolean>{

	public NBoolean(Boolean obj) 
	{
		super(obj,obj?Flag.Head.NTrue:Flag.Head.NFalse,obj?Flag.End.NTrue:Flag.End.NFalse);
	}

	@Override
	public byte[] getBytes() {
		return addByteArray(getFlagHead(),getFlagEnd());
	}
	
	@Override
	public byte[] getObjectBytes() {
		return new byte[]{getFlagHead()};
	}

	@Override
	public String getString() {
		return obj.toString();
	}

	@Override
	protected NType<Boolean> decoder(TypeBytesInputStream ti) throws Exception {
		if(ti.available()!=0)
		{
			return new NBoolean(ti.readBoolean());
		}
		return null;
	}


}
