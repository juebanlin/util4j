package net.jueb.util4j.queue.queueExecutor;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.jueb.util4j.queue.queueExecutor.RunnableQueue;

/**
* 基于事件的队列
* 当有任务添加时,子类会收到回调
* @author juebanlin
*/
public class RunnableQueueWrapper implements RunnableQueue{
	
	private final Queue<Runnable> queue;
	
	public RunnableQueueWrapper(Queue<Runnable> queue) {
		Objects.requireNonNull(queue);
		this.queue=queue;
	}
	
	@Override
	public boolean add(Runnable e) {
		return queue.add(e);
	}

	@Override
	public Runnable remove() {
		return queue.remove();
	}

	@Override
	public Runnable poll() {
		return queue.poll();
	}

	@Override
	public Runnable element() {
		return queue.element();
	}

	@Override
	public Runnable peek() {
		return queue.peek();
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return queue.contains(o);
	}

	@Override
	public Iterator<Runnable> iterator() {
		return queue.iterator();
	}

	@Override
	public Object[] toArray() {
		return queue.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return queue.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		return queue.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return queue.containsAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return queue.removeAll(c);
	}

	@Override
	public boolean removeIf(Predicate<? super Runnable> filter) {
		return queue.removeIf(filter);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return queue.retainAll(c);
	}
	
	@Override
	public void clear() {
		queue.clear();
	}

	@Override
	public Spliterator<Runnable> spliterator() {
		return queue.spliterator();
	}

	@Override
	public Stream<Runnable> stream() {
		return queue.stream();
	}

	@Override
	public Stream<Runnable> parallelStream() {
		return queue.parallelStream();
	}

	@Override
	public void forEach(Consumer<? super Runnable> action) {
		queue.forEach(action);
	}

	@Override
    public final boolean offer(Runnable e) {
		event_taskOfferBefore();
        boolean bool=queue.offer(e);
        event_taskOfferAfter(bool);
        return bool;
    }
		
	@Override
	public final boolean addAll(Collection<? extends Runnable> c) {
		event_taskOfferBefore();
        boolean bool=queue.addAll(c);
        event_taskOfferAfter(bool);
        return bool;
	}
		
	/**
	* 任务添加之前
	 */
	protected void event_taskOfferBefore(){
		
	}
		
	/**
	 * 任务添加之后
	 * @param bool
	 */
	protected void event_taskOfferAfter(boolean offeredSucceed){
		
	}
}