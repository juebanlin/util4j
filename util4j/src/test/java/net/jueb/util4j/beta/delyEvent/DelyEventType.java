package net.jueb.util4j.beta.delyEvent;

import net.jueb.util4j.beta.delyEvent.events.DelyBuildingEvent;

/**
 * 延迟事件类型
 * @author Administrator
 */
public enum DelyEventType {
	/**
	 * 建造升级事件
	 */
	BuildingEvent(1,DelyBuildingEvent.class),
	;
	
	private final Class<? extends IDelyEvent> dataClass;
	private final int value;
	
	private DelyEventType(int value,Class<? extends IDelyEvent> dataClass) {
		this.value=value;
		this.dataClass=dataClass;
	}
	
	public Class<? extends IDelyEvent> getDataClass(){
		return dataClass;
	}
	
	public int getValue() {
		return value;
	}

	public static DelyEventType valueOf(int value) {
		for(DelyEventType t:values())
		{
			if(t.value==value)
			{
				return t;
			}
		}
		return null;
	}
}
