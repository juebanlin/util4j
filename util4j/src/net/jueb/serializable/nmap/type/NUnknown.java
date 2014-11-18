package net.jueb.serializable.nmap.type;

/**
 * 未知类型，不占空间
 * @author Administrator
 */
public final class NUnknown extends NType<Object>{

	public NUnknown() 
	{
		super(null,new byte[]{},new byte[]{});
	}

	@Override
	public byte[] getBytes() {
		return new byte[]{};
	}
	
	@Override
	public byte[] getObjectBytes() {
		return new byte[]{};
	}

	@Override
	public String getString() {
		return "NUnknown";
	}

}
