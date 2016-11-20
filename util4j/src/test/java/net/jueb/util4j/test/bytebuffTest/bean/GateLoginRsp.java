package net.jueb.util4j.test.bytebuffTest.bean;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.jueb.util4j.test.bytebuffTest.ByteBuffer;

public class GateLoginRsp implements Dto{

	private long roleId;
	private String name;
	private long money;
	private RoleSexEnum sex;
	private String headIcon;
	private Map<Integer,Integer> bag=new HashMap<>();
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getMoney() {
		return money;
	}
	public void setMoney(long money) {
		this.money = money;
	}
	public RoleSexEnum getSex() {
		return sex;
	}
	public void setSex(RoleSexEnum sex) {
		this.sex = sex;
	}
	public String getHeadIcon() {
		return headIcon;
	}
	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}
	public Map<Integer, Integer> getBag() {
		return bag;
	}
	public void setBag(Map<Integer, Integer> bag) {
		this.bag = bag;
	}
	@Override
	public String toString() {
		return "GateLoginRsp [roleId=" + roleId + ", name=" + name + ", money=" + money + ", sex=" + sex + ", headIcon="
				+ headIcon + ", bag=" + bag + "]";
	}
	
	@Override
	public void readFrom(ByteBuffer buffer) {
		this.roleId=buffer.readLong();
		this.name=buffer.readUTF();
		this.money=buffer.readLong();
		this.sex=RoleSexEnum.valueOf(buffer.readByte());
		this.headIcon=buffer.readUTF();
		Map<Integer,Integer> item=new HashMap<>();
		int size=buffer.readInt();
		for(int i=0;i<size;i++)
		{
			item.put(buffer.readInt(),buffer.readInt());
		}
		bag=item;
	}
	
	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeLong(roleId);
		buffer.writeUTF(name);
		buffer.writeLong(money);
		buffer.writeByte(sex.getValue());
		buffer.writeUTF(headIcon);
		int size=bag.size();
		buffer.writeInt(size);
		for(Entry<Integer, Integer> e:bag.entrySet())
		{
			buffer.writeInt(e.getKey());
			buffer.writeInt(e.getValue());
		}
	}
}
