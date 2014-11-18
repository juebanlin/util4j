package net.jueb.serializable.nmap.type;

import java.util.Map;
import net.jueb.serializable.nmap.falg.Flag;

public class NMap extends NType<Map<NType<?>,NType<?>>>{

	public NMap(Map<NType<?>, NType<?>> obj) {
		super(obj, Flag.Head.NMap, Flag.End.NMap);
	}
	
	@Override
	public byte[] getBytes() {
		int size=obj.size();//加入当前map的第一层大小
		return addByteArray(getFlagHead(),tb.IntegerToByteArray(size),getObjectBytes(),getFlagEnd());
	}
	
	@Override
	public byte[] getObjectBytes() {
		byte[] data=new byte[]{};
		for(NType<?> key:obj.keySet())
		{//不管key和value是什么类型，只管取数据,如果类型是NMap，会自动嵌套调用它getBytes
			data=addByteArray(data,key.getBytes(),obj.get(key).getBytes());
		}
		return data;
	}

	@Override
	public String getString() {
		return obj.toString();
	}

}
