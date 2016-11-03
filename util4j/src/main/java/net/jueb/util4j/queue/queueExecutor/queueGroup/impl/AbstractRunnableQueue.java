package net.jueb.util4j.queue.queueExecutor.queueGroup.impl;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.jueb.util4j.queue.queueExecutor.RunnableQueue;

/**
* 基于事件的队列
* 当有任务添加时,子类会收到回调
* @author juebanlin
*/
public abstract class AbstractRunnableQueue extends ConcurrentLinkedQueue<Runnable> implements RunnableQueue{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2961878968488809736L;
		
		@Override
        public final boolean offer(Runnable e) {
			event_taskOfferBefore();
        	boolean bool=super.offer(e);
        	event_taskOfferAfter(bool);
        	return bool;
        }
		
		@Override
		public final boolean addAll(Collection<? extends Runnable> c) {
			event_taskOfferBefore();
        	boolean bool=super.addAll(c);
        	event_taskOfferAfter(bool);
        	return bool;
		}
		
		/**
		 * 任务添加之前
		 */
		protected abstract void event_taskOfferBefore();
		
		/**
		 * 任务添加之后
		 * @param bool
		 */
		protected abstract void event_taskOfferAfter(boolean offeredSucceed);
}