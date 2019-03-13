package net.jueb.util4j.beta.delyEvent;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

/**
 * 延时事件管理器
 * @author Administrator
 */
public class DelyEventManager {

	private final Map<DelyEventType,DelyEventHandler<? extends IDelyEvent>> handlers=new HashMap<>();
	
	/**
	 * 注册事件处理器
	 * @param type
	 * @param handler
	 */
	public <T extends IDelyEvent> void registEventHandler(int type,DelyEventHandler<T> handler) {
		registEventHandler(type, handler);
	}
	
	/**
	 * 注册事件处理器
	 * @param type
	 * @param handler
	 */
	public <T extends IDelyEvent> void registEventHandler(DelyEventType type,DelyEventHandler<T> handler) {
		handlers.put(type, handler);
	}
	
	/**
	 * 事件通知
	 * @param type 事件类型
	 * @param eventData 事件数据
	 */
	public void notify(int type,String jsonEventData) {
		notify(DelyEventType.valueOf(type), jsonEventData);
	}
	
	/**
	 * 事件通知
	 * @param type 事件类型
	 * @param eventData 事件数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void notify(DelyEventType type,String jsonEventData) {
		IDelyEvent event=new Gson().fromJson(jsonEventData, type.getDataClass());
		DelyEventHandler handler=handlers.get(type);
		handler.handle(event);
	}
	
	/**
	 * 增加延迟事件
	 * @param type
	 * @param eventData
	 */
	public <T extends IDelyEvent> void addDelyEvent(DelyEventType type,T eventData,long time) {
		addDelyEvent_JDK(type, eventData, time);
	}
	
	/**
	 * jdk模拟实现
	 * @param type
	 * @param eventData
	 */
	protected <T extends IDelyEvent> void addDelyEvent_JDK(DelyEventType type,T eventData,long time) {
		DelyEventManager m=this;
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
				}
				String json=new Gson().toJson(eventData);
				m.notify(type, json);
			}
		}.start();
	}
}
