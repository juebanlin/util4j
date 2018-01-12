package net.jueb.util4j.queue.queueExecutor.executor.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;
import net.jueb.util4j.queue.queueExecutor.queue.RunnableQueueEventWrapper;

/**
 * 具有事件和调度器功能的队列
 * @author juebanlin
 */
public abstract class RunnableQueueExecutorEventWrapper extends RunnableQueueEventWrapper implements QueueExecutor {


	public RunnableQueueExecutorEventWrapper(Queue<Runnable> queue) {
		super(queue);
	}

	@Override
	public final void execute(Runnable task) {
		offer(task);
	}

	@Override
	public final void execute(List<Runnable> tasks) {
		addAll(tasks);
	}
	
	private String alias;
	private final Set<String> tags=new HashSet<>();
	private final Map<String,Object> attributes=new HashMap<String,Object>();
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void addTag(String tag) {
		tags.add(tag);
	}
	
	public void removeTag(String tag) {
		tags.remove(tag);
	}
	
	public boolean hasTag(String tag) {
		return tags.contains(tag);
	}
	
	public Set<String> getTags(){
		return tags;
	}
	
	public boolean hasAttribute(String key) {
		return attributes.containsKey(key);
	}

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public Object removeAttribute(String key) {
		return attributes.remove(key);
	}

	public void clearAttributes() {
		attributes.clear();
	}
}