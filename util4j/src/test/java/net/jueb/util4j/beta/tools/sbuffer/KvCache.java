package net.jueb.util4j.beta.tools.sbuffer;

import java.util.Map;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import net.jueb.util4j.buffer.ArrayBytesBuff;

public class KvCache {
	
	ArrayBytesBuff byteBuffe=new ArrayBytesBuff();
	DB db=DBMaker.heapDB().make();
	Map<Integer, Byte> map=db.hashMap("map1").keySerializer(Serializer.INTEGER).valueSerializer(Serializer.BYTE).createOrOpen();
	Map<Integer, Byte> map2=db.treeMap("map2").keySerializer(Serializer.INTEGER).valueSerializer(Serializer.BYTE).createOrOpen();
	
	public void put(int index,byte value)
	{
		map2.put(index, value);
//		byteBuffe.writeInt(index);
//		byteBuffe.writeByte(value);
		
	}
	
	public Byte get(int index)
	{
		return map2.getOrDefault(index, null);
//		byteBuffe.reset();
//		while(byteBuffe.readableBytes()>0)
//		{
//			if(byteBuffe.readInt()==index)
//			{
//				return byteBuffe.readByte();
//			}else
//			{
//				byteBuffe.skipBytes(1);
//			}
//		}
//		return null;
	}
}
