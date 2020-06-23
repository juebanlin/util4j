package net.jueb.util4j.queue.queueExecutor.groupExecutor.impl;

import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;
import net.jueb.util4j.queue.queueExecutor.executor.impl.RunnableQueueExecutorEventWrapper;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutor.KeyElement;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupManager;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultQueueManager extends AbstractQueueMaganer implements QueueGroupManager{

	private final Map<String,TaskQueue> queues=new HashMap<>();
	private final AtomicLong totalCompleteTask=new AtomicLong();
	
	private final Object addLock=new Object();

	private volatile KeyGroupEventListener listener;

	/**
	 * 单次处理数量,<=0则一直处理
	 */
	private int batchCount;

	public DefaultQueueManager() {
	}
	
	public DefaultQueueManager(QueueFactory queueFactory) {
		super(queueFactory);
	}

	/**
	 * @param queueFactory
	 * @param batchCount 队列被线程单次处理的数量,防止在线程不足的情况下一直消费此线程,其它队列得不到处理
	 */
	public DefaultQueueManager(QueueFactory queueFactory,int batchCount) {
		super(queueFactory);
		this.batchCount=batchCount;
	}

	public void execute(String index, Runnable task) {
		if (index==null || task == null) {
			throw new IllegalArgumentException();
		}
		getQueueExecutor(index).execute(task);
	}

	public void execute(String index, List<Runnable> tasks) {
		if (index==null || tasks == null) {
			throw new IllegalArgumentException();
		}
		getQueueExecutor(index).execute(tasks);
	}
	
	public void setGroupEventListener(KeyGroupEventListener listener)
	{
		this.listener=listener;
	}
	
	@Override
	public boolean hasQueueExecutor(String key) {
		return queues.containsKey(key);
	}

	
	public QueueExecutor getQueueExecutor(String index) {
		if (index==null) {
			throw new IllegalArgumentException();
		}
		QueueExecutor qe=queues.get(index);
		if(qe==null)
		{
			synchronized (addLock) {
				if(qe==null)
				{
					TaskQueue tq=new TaskQueue(index,getQueueFactory_().buildQueue());
					tq.setAlias("key_"+index);
					tq.setAttribute("key", index);
					queues.put(index,tq);
					return tq;
				}
			}
		}
		return qe;
	}



	/**
	 * 获取总完成任务数量
	 * @return
	 */
	public long getToalCompletedTaskCount() {
	    return totalCompleteTask.get();
	}

	@Override
	public long getToalCompletedTaskCount(String index) {
		TaskQueue tq=queues.get(index);
		if(tq!=null)
		{
			return tq.getCompletedTaskCount().get();
		}
		return 0;
	}

	public Iterator<KeyElement<QueueExecutor>> keyIterator(){
		return new Iterator<KeyElement<QueueExecutor>>() {
			final Iterator<Entry<String, TaskQueue>> map=queues.entrySet().iterator();
			@Override
			public boolean hasNext() {
				return map.hasNext();
			}
			@Override
			public KeyElement<QueueExecutor> next() {
				Entry<String, TaskQueue> e=map.next();
				return new KeyElement<QueueExecutor>() {
					@Override
					public String getKey() {
						return e.getKey();
					}
					@Override
					public QueueExecutor getValue() {
						return e.getValue();
					}
				};
			}
		};
	}
	
	public Iterator<QueueExecutor> iterator() {
		return new Iterator<QueueExecutor>() {
			final Iterator<TaskQueue> map=queues.values().iterator();
			@Override
			public boolean hasNext() {
				return map.hasNext();
			}
	
			@Override
			public QueueExecutor next() {
				return map.next();
			}
		};
	}

	/**
	 * key对应的队里产生了处理任务
	 * @param key
	 * @param handleTask
	 */
	protected void onQueueHandleTask(String key,Runnable handleTask)
	{
		KeyGroupEventListener tmp=listener;
		if(tmp!=null)
		{//任务抛给监听者
			tmp.onQueueHandleTask(key, handleTask);
		}
	}

	/**
	 * 插槽队列
	 * @author juebanlin
	 */
	private class TaskQueue extends RunnableQueueExecutorEventWrapper implements Runnable{
		/**
		 *队列索引
		 */
		private final String index;
		/**
	     * 此队列是否锁定/是否被线程占用
	     * 次属性仅供本类持有
	     */
	    private final AtomicBoolean isLock = new AtomicBoolean(false);
	    
	    private final AtomicBoolean processLock = new AtomicBoolean(false);
	    
	    /**
	     * 此队列完成的任务数量
	     */
		private final AtomicLong completedTaskCount = new AtomicLong(0);
		
		public TaskQueue(String index,Queue<Runnable> queue) {
			super(queue);
			this.index=index;
			init();
		}
		
		/**
		 * 初始化状态
		 */
		public void init(){
			isLock.set(false);
			completedTaskCount.set(0);
			super.clear();
		}
		
		public AtomicLong getCompletedTaskCount() {
			return completedTaskCount;
		}
	
		@Override
		protected void onAddBefore() {
			
		}

		@Override
		protected void onAddAfter(boolean offeredSucceed) {
			if(offeredSucceed)
			{
				if(isLock.compareAndSet(false, true))
			 	{//一个处理任务产生
					onQueueHandleTask(index,this);
			 	}
			}
		}

		protected void beforeExecute(Thread thread, Runnable task) {
			
		}
	
		protected void afterExecute(Runnable task,RuntimeException object) {
			
		}

		@Override
		public void run() {
			TaskQueue queue=this;
			if(queue.processLock.compareAndSet(false, true))
			{//如果此runnable未被执行则执行,已执行则不可再次执行(防止task事件被多个监听,有且只能有一个消费者可以获得执行能力)
				Runnable task=null;
				try {
					task=handleQueueTask(queue);
				} finally {
					queue.processLock.set(false);
					queue.isLock.set(false);
					if(task!=null && isLock.compareAndSet(false, true)){
						//如果onAddAfter没有触发,那么就由当前线程再次处理
						task.run();
					}
				}
			}
		}
		
		/**
		 * 处理队列任务
         * @param queue
         */
        private Runnable handleQueueTask(TaskQueue queue) {
        	Thread thread=Thread.currentThread();
        	int num=0;
        	for (;;) 
            {
				if(batchCount>0 && num>=batchCount){
					//停止处理队列
					if(queue.peek()!=null){
						//抛出事件,下次处理
						return ()->{
							onQueueHandleTask(index,this);
						};
					}
					break;
				}
        		Runnable task = queue.poll();
            	if(task == null)
                {//停止处理队列
            		break;
                }
            	beforeExecute(thread, task);
                boolean succeed = false;
                try {
                	num++;
                    task.run();
                    queue.getCompletedTaskCount().incrementAndGet();
                    totalCompleteTask.incrementAndGet();
                    succeed = true;
                    afterExecute(task, null);
                } catch (RuntimeException e) {
                    if (!succeed) {
                        afterExecute(task, e);
                    }
                    throw e;
                }
            }
        	return null;
        }
	}
	
	public static class Builder{
		QueueFactory queueFactory=QueueFactory.DEFAULT_QUEUE_FACTORY;
		
		public Builder setQueueFactory(QueueFactory queueFactory) {
			Objects.requireNonNull(queueFactory);
			this.queueFactory = queueFactory;
			return this;
		}
		/**
		 * 设置多生产者单消费者队列工厂
		 * @return 
		 */
		public Builder setMpScQueueFactory() {
			this.queueFactory=QueueFactory.MPSC_QUEUE_FACTORY;
			return this;
		}

		public DefaultQueueManager build()
		{
			return new DefaultQueueManager(queueFactory);
		}
	}
}