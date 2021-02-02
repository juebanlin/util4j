package net.jueb.util4j.beta.delyEvent.events;

import net.jueb.util4j.beta.delyEvent.IDelyEvent;

public class DelyBuildingEvent implements IDelyEvent{

	private int bid;//建筑id

	public int getBid() {
		return bid;
	}

	public void setBid(int bid) {
		this.bid = bid;
	}

	@Override
	public String toString() {
		return "DelyBuildingEvent [bid=" + bid + "]";
	}
}
