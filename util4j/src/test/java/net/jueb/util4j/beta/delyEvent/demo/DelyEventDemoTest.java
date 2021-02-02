package net.jueb.util4j.beta.delyEvent.demo;

import java.util.Scanner;

import net.jueb.util4j.beta.delyEvent.DelyEventManager;
import net.jueb.util4j.beta.delyEvent.DelyEventType;
import net.jueb.util4j.beta.delyEvent.events.DelyBuildingEvent;

public class DelyEventDemoTest {

	DelyEventManager m=new DelyEventManager();
	
	public void init() {
		m.registEventHandler(DelyEventType.BuildingEvent, this::handleBuildingEvent);
	}
	
	/**
	 * 执行建筑升级
	 */
	public void levelUpBuilding(int bid,int sec) {
		DelyBuildingEvent event=new DelyBuildingEvent();
		event.setBid(bid);
		m.addDelyEvent(DelyEventType.BuildingEvent, event, sec*1000);
		System.out.println("执行建筑升级:"+bid+",升级所需时间:"+sec+"秒");
	}
	
	public void handleBuildingEvent(DelyBuildingEvent event) {
		System.out.println("已完成建筑的升级:"+event.getBid());
	}
	
	
	public static void main(String[] args) {
		DelyEventDemoTest t=new DelyEventDemoTest();
		t.init();
		t.levelUpBuilding(11,5);
		new Scanner(System.in).nextLine();
	}
}
