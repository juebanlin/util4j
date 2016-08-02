package net.jueb.util4j.common.game.room.defined;

public interface ISubRoom  extends IRoom{
	
	/**
	 * 获取父类房间,父类房间可能是RootRoom也可能是SubRoom
	 * @return
	 */
	public IRoom getParent();
}
