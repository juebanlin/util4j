package net.jueb.util4j.queue.queueExecutor.groupExecutor.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;
import net.jueb.util4j.queue.queueExecutor.executor.impl.RunnableQueueExecutorEventWrapper;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.IndexQueueGroupManager;

public class ArrayIndexQueueManager extends AbstractQueueMaganer implements IndexQueueGroupManager{
	/**
	 * 最大队列插槽数
	 */
	public static final int MAX_SOLT_COUNT = 0xFFFF;

	/**
	 * 队列插槽
	 */
	private final SoltQueue[] solts = new SoltQueue[MAX_SOLT_COUNT + 1];

	/**
	 * 队列别名
	 */
	private final String[] soltAlias = new String[solts.length];
	
	private final AtomicLong totalCompleteTask=new AtomicLong();
	
	private volatile IndexGroupEventListener listener;

	public ArrayIndexQueueManager() {
		init();
	}
	
	public ArrayIndexQueueManager(QueueFactory queueFactory) {
		super(queueFactory);
		init();
	}
	
	protected void init()
    {
		for(int i=0;i<solts.length;i++)
    	{
    		solts[i]=new SoltQueue(i,getQueueFactory_().buildQueue());
    	}
    }
	
	/**
	 * 转换为插槽索引
	 * 
	 * @param solt
	 * @return
	 */
	@Deprecated
	protected final int convertIndex_old(short solt) {
		byte a = (byte) (solt >> 8 & 0xFF);// 高8位
		byte b = (byte) (solt & 0xFF);// 低8位
		int value = ((int) (a)) << 8 | (int) (b);
		return value & 0xffff;
	}
	/**
	 * 转换为插槽索引
	 * @param solt
	 * @return
	 */
	protected final int convertIndex(short solt) {
		return solt & 0xFFFF;
	}

	public Iterator<QueueExecutor> iterator() {
		return new Iterator<QueueExecutor>() {
			int i = 0;

			@Override
			public boolean hasNext() {
				return i < solts.length;
			}

			@Override
			public QueueExecutor next() {
				return solts[i++];
			}
		};
	}

	public void execute(short solt, Runnable task) {
		if (task == null) {
			throw new RuntimeException("task is null");
		}
		getQueueExecutor(solt).execute(task);
	}

	public void execute(short solt, List<Runnable> tasks) {
		if (tasks == null) {
			throw new RuntimeException("tasks is null");
		}
		getQueueExecutor(solt).execute(tasks);
	}

	public void setAlias(short solt, String alias) {
		soltAlias[convertIndex(solt)] = alias;
	}

	public String getAlias(short solt) {
		return soltAlias[convertIndex(solt)];
	}

	public QueueExecutor getQueueExecutor(short solt) {
		int index = convertIndex(solt);
		return solts[index];
	}
	
	public void setGroupEventListener(IndexGroupEventListener listener)
	{
		this.listener=listener;
	}
	
	protected void onQueueHandleTask(short solt,Runnable handleTask)
	{
		IndexGroupEventListener tmp=listener;
		if(tmp!=null)
		{
			tmp.onQueueHandleTask(solt, handleTask);
		}
	}
	
	public long getToalCompletedTaskCount(short solt) {
		int index = convertIndex(solt);
		return solts[index].getCompletedTaskCount().get();
	}

	/**
	 * 获取总完成任务数量
	 * @return
	 */
	public long getToalCompletedTaskCount() {
        return totalCompleteTask.get();
    }
	
	/**
	 * 插槽队列
	 * @author juebanlin
	 */
	private class SoltQueue extends RunnableQueueExecutorEventWrapper{
		/**
		 * 队列索引
		 */
		private final int soltIndex;
		/**
	     * 此队列是否锁定/是否被线程占用
	     * 次属性仅供本类持有
	     */
	    private final AtomicBoolean isLock = new AtomicBoolean(false);
	    /**
	     * 此队列完成的任务数量
	     */
		private final AtomicLong completedTaskCount = new AtomicLong(0);
		
		public SoltQueue(int soltIndex,Queue<Runnable> queue) {
			super(queue,"SoltQueue-"+soltIndex);
			this.soltIndex=soltIndex;
			init();
		}
		
		@Override
		public String getQueueName() {
			return getAlias((short) soltIndex);
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
			// TODO Auto-generated method stub
		}

		@Override
		protected void onAddAfter(boolean offeredSucceed) {
			if(offeredSucceed)
			{
				if(isLock.compareAndSet(false, true))
			 	{//一个处理任务产生
					onQueueHandleTask((short)soltIndex,new SoltQueueProcessTask(this));
			 	}
			}
		}

		private class SoltQueueProcessTask implements Runnable{
	    	SoltQueue queue;
	    	public SoltQueueProcessTask(SoltQueue queue) {
	    		this.queue=queue;
			}
			@Override
			public void run() {
				try {
					handleQueueTask(queue);
				} finally {
					queue.isLock.set(false);
				}
			}
			
			 /**
	         * 处理队列任务
	         * @param queue
	         */
	        private void handleQueueTask(SoltQueue queue) {
	        	Thread thread=Thread.currentThread();
	        	for (;;) 
	            {
	        		Runnable task = queue.poll();
	            	if(task == null)
	                {//停止处理队列
	            		break;
	                }
	            	beforeExecute(thread, task);
	                boolean succeed = false;
	                try {
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
	        }
	    }
		
		protected void beforeExecute(Thread thread, Runnable task) {
			
		}

		protected void afterExecute(Runnable task,RuntimeException object) {
			
		}
	}
	
	public static class Builder{
		public ArrayIndexQueueManager build()
		{
			//TODO
			return null;
		}
	}
}