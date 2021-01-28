package net.jueb.util4j.queue.queueExecutor.groupExecutor.impl;

import lombok.extern.slf4j.Slf4j;
import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;
import net.jueb.util4j.queue.queueExecutor.executor.impl.RunnableQueueExecutorEventWrapper;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutor.KeyElement;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupManager;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class DefaultQueueManager extends AbstractQueueMaganer implements QueueGroupManager{

	private final Map<String,TaskQueue> queues=new HashMap<>();

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
		long value=0;
		for (TaskQueue queue : queues.values()) {
			value+=queue.getCompletedTaskCount();
		}
	    return value;
	}

	@Override
	public long getToalCompletedTaskCount(String index) {
		TaskQueue tq=queues.get(index);
		if(tq!=null)
		{
			return tq.getCompletedTaskCount();
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
	protected void onQueueProcessTaskBuild(String key, Runnable handleTask)
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
	private class TaskQueue extends RunnableQueueExecutorEventWrapper{

		public final static int STATUS_IDLE=0;
		public final static int STATUS_WAIT_PROCESS=1;
		public final static int STATUS_PROCESS=2;

		private final AtomicInteger status=new AtomicInteger(STATUS_IDLE);

		/**
		 *队列索引
		 */
		private final String index;

	    /**
	     * 此队列完成的任务数量
	     */
		private long completedTaskCount;
		/**
		 * 任务唤起次数
		 */
		private long wakeCount;

		/**
		 * 单次最大处理任务数量
		 */
		private long maxProcessSize;

		public TaskQueue(String index,Queue<Runnable> queue) {
			super(queue);
			this.index=index;
			init();
		}
		
		/**
		 * 初始化状态
		 */
		public void init(){
			super.clear();
			completedTaskCount=0;
			wakeCount=0;
			maxProcessSize=0;
		}
		
		public long getCompletedTaskCount() {
			return completedTaskCount;
		}

		@Override
		public long handleCount() {
			return wakeCount;
		}

		@Override
		public long maxProcessCount() {
			return maxProcessSize;
		}

		@Override
		protected void onAddBefore() {
			
		}

		@Override
		protected void onAddAfter(boolean offeredSucceed) {
			if(offeredSucceed)
			{
				if(status.get()!=STATUS_IDLE){
					return;
				}
				wakeIfIdle();
			}
		}


		private void wakeIfIdle(){
			if(status.compareAndSet(STATUS_IDLE,STATUS_WAIT_PROCESS)){
				onQueueProcessTaskBuild(index,this::processQueueTasks);
				wakeCount++;
			}
		}

		/**
		 * 处理任务
		 * 此方法被委托线程执行
		 */
		protected void processQueueTasks(){
			if(!status.compareAndSet(STATUS_WAIT_PROCESS,STATUS_PROCESS)){
				if(status.compareAndSet(STATUS_PROCESS,STATUS_PROCESS)){
					log.error("队列processStart状态错误,其它线程正在处理,{}:{}",index,status);
					return;
				}
				log.error("队列processStart状态错误,{}:{}",index,status);
//				wakeIfIdle();
				return;
			}
			try {
				TaskQueue queue=this;
				doProcessQueueTasks(queue);
			}finally {
				if(!status.compareAndSet(STATUS_PROCESS,STATUS_IDLE)){
					log.error("队列processEnd状态错误,{}:{}",index,status);
//					wakeIfIdle();
				}
			}
		}

		/**
		 * 处理队列任务
		 * @param queue
		 */
		private void doProcessQueueTasks(TaskQueue queue) {
			Thread thread=Thread.currentThread();
			int num=0;
			try {
				for (;;)
				{
					if(batchCount>0 && num>=batchCount){
						//停止处理队列
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
						queue.completedTaskCount++;
						succeed = true;
						afterExecute(task, null);
					} catch (RuntimeException e) {
						if (!succeed) {
							afterExecute(task, e);
						}
						throw e;
					}
				}
			}finally {
				if(num>maxProcessSize){
					maxProcessSize=num;
				}
			}
		}

		protected void beforeExecute(Thread thread, Runnable task) {

		}

		protected void afterExecute(Runnable task,RuntimeException object) {

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