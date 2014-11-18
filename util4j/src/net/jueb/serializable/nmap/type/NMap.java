package net.jueb.serializable.nmap.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.jueb.serializable.nmap.falg.Flag;

/**
 * 内部封装一个hashmap
 * @author Administrator
 *
 */
public class NMap extends NType<Map<NType<?>,NType<?>>> implements Map<NType<?>, NType<?>>{

	public NMap() {
		super(new HashMap<NType<?>, NType<?>>(), Flag.Head.NMap, Flag.End.NMap);
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

	@Override
	public int size() {
		return obj.size();
	}

	@Override
	public boolean isEmpty() {
		return obj.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return obj.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return obj.containsValue(value);
	}

	@Override
	public NType<?> get(Object key) {
		return obj.get(key);
	}

	@Override
	public NType<?> put(NType<?> key, NType<?> value) {
		if(key==this || value==this)
		{
			throw new RuntimeException("不能将map自身作为key或者value");
		}
		return obj.put(key, value);
	}

	@Override
	public NType<?> remove(Object key) {
		return obj.remove(key);
	}

	@Override
	public void putAll(Map<? extends NType<?>, ? extends NType<?>> m) {
		obj.putAll(m);
	}

	@Override
	public void clear() {
		obj.clear();
	}

	@Override
	public Set<NType<?>> keySet() {
		return obj.keySet();
	}

	@Override
	public Collection<NType<?>> values() {
		return obj.values();
	}

	@Override
	public Set<java.util.Map.Entry<NType<?>, NType<?>>> entrySet() {
		return obj.entrySet();
	}

}
