package net.jueb.util4j.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自命名线程工厂类
 * @author Administrator
 */
public class NamedThreadFactory implements ThreadFactory {
	private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

	private final AtomicInteger mThreadNum = new AtomicInteger(1);

	private final String mPrefix;

	private final boolean mDaemo;
	private final int mPriority;

	private final ThreadGroup mGroup;

	public NamedThreadFactory() {
		this("pool-" + POOL_SEQ.getAndIncrement(), false);
	}

	public NamedThreadFactory(String prefix) {
		this(prefix, false);
	}

	public NamedThreadFactory(String prefix, boolean daemo) {
		this(prefix,daemo,Thread.NORM_PRIORITY);
	}

	public NamedThreadFactory(String prefix, boolean daemo,int priority) {
		mPrefix = prefix + "-thread-";
		mDaemo = daemo;
		mPriority=priority;
		SecurityManager s = System.getSecurityManager();
		mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s
				.getThreadGroup();
	}

	public Thread newThread(Runnable runnable) {
		String name = mPrefix + mThreadNum.getAndIncrement();
		Thread ret = new Thread(mGroup, runnable, name, 0);
		ret.setDaemon(mDaemo);
		ret.setPriority(mPriority);
		return ret;
	}

	public ThreadGroup getThreadGroup() {
		return mGroup;
	}
}