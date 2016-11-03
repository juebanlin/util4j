package net.jueb.util4j.common.game.room.impl;
import net.jueb.util4j.common.game.room.defined.ISubRoom;

final class SubRoom extends Room implements ISubRoom{

	private final Room parent;
	
	SubRoom(Room parent,long id, String name) {
		super(id, name);
		this.parent=parent;
	}

	@Override
	public Room getParent() {
		return parent;
	}
}
