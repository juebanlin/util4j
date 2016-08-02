package net.jueb.util4j.common.game.room.defined;

import java.util.Collection;

public interface IRoom{
	
	/**
	 * 房间ID
	 * @return
	 */
	public long getId();
	
	/**
	 * 房间名字
	 * @return
	 */
	public String getName();
	
	/**
	 * 获取子房间,子房间必须有parent
	 * @return
	 */
	public <S extends ISubRoom> Collection<S> getSubRooms();
	
	/**
	 * 创建子房间
	 * @param roomName
	 * @return
	 */
	public ISubRoom createSubRoom(String roomName);
	
	/**
	 * 查找子房间
	 * @param roomName
	 * @return
	 */
	public ISubRoom findSubRoom(long roomId);
}
