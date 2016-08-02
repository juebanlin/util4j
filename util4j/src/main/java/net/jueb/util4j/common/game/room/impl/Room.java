package net.jueb.util4j.common.game.room.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.jueb.util4j.common.game.room.defined.IRoom;
import net.jueb.util4j.common.game.room.defined.ISubRoom;

public class Room implements IRoom{

	private final long id;
	private final String name;
	protected final Map<Long,ISubRoom> subRooms=new HashMap<Long,ISubRoom>();
	protected final AtomicLong subSeq=new AtomicLong(1000);
	protected final ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock();
	public Room(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Override
	public final long getId() {
		return id;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public Collection<ISubRoom> getSubRooms() {
		rwLock.readLock().lock();
		try {
			return subRooms.values();
		} finally {
			rwLock.readLock().unlock();
		}
	}

	@Override
	public ISubRoom createSubRoom(String roomName) {
		return buildSubRoom(roomName);
	}
	
	protected ISubRoom buildSubRoom(String roomName)
	{
		rwLock.writeLock().lock();
		try {
			SubRoom subRoom=new SubRoom(this, subSeq.incrementAndGet(),roomName);
			subRooms.put(subRoom.getId(), subRoom);
			return subRoom;
		} finally {
			rwLock.writeLock().unlock();
		}
	}

	@Override
	public ISubRoom findSubRoom(long roomId) {
		rwLock.readLock().lock();;
		try {
			return subRooms.get(roomId);
		} finally {
			rwLock.readLock().unlock();
		}
	}
}
