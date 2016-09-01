package net.jueb.util4j.lock.waitCondition;

import java.util.concurrent.TimeUnit;

public final class PhasedBackoffWaitConditionStrategy implements WaitConditionStrategy
{
    private static final int SPIN_TRIES = 10000;
    private final long spinTimeoutNanos;
    private final long yieldTimeoutNanos;
    private final WaitConditionStrategy fallbackStrategy;

    public PhasedBackoffWaitConditionStrategy(long spinTimeout,
                                     long yieldTimeout,
                                     TimeUnit units,
                                     WaitConditionStrategy fallbackStrategy)
    {
        this.spinTimeoutNanos = units.toNanos(spinTimeout);
        this.yieldTimeoutNanos = spinTimeoutNanos + units.toNanos(yieldTimeout);
        this.fallbackStrategy = fallbackStrategy;
    }

    /**
     * Block with wait/notifyAll semantics
     */
    public static PhasedBackoffWaitConditionStrategy withLock(long spinTimeout,
                                                     long yieldTimeout,
                                                     TimeUnit units)
    {
        return new PhasedBackoffWaitConditionStrategy(spinTimeout, yieldTimeout,
                                             units, new BlockingWaitConditionStrategy());
    }

    /**
     * Block with wait/notifyAll semantics
     */
    public static PhasedBackoffWaitConditionStrategy withLiteLock(long spinTimeout,
                                                         long yieldTimeout,
                                                         TimeUnit units)
    {
        return new PhasedBackoffWaitConditionStrategy(spinTimeout, yieldTimeout,
                                             units, new LiteBlockingWaitConditionStrategy());
    }

    /**
     * Block by sleeping in a loop
     */
    public static PhasedBackoffWaitConditionStrategy withSleep(long spinTimeout,
                                                      long yieldTimeout,
                                                      TimeUnit units)
    {
        return new PhasedBackoffWaitConditionStrategy(spinTimeout, yieldTimeout,
                                             units, new SleepingWaitConditionStrategy(0));
    }


	@Override
	public <T> T waitFor(WaitCondition<T> waitCondition) throws InterruptedException{
        long startTime = 0;
        int counter = SPIN_TRIES;
        waitCondition.doComplete();
        do
        {
            if (waitCondition.isComplete())
            {
                return waitCondition.result();
            }
            if (0 == --counter)
            {
                if (0 == startTime)
                {
                    startTime = System.nanoTime();
                }
                else
                {
                    long timeDelta = System.nanoTime() - startTime;
                    if (timeDelta > yieldTimeoutNanos)
                    {
                        return fallbackStrategy.waitFor(waitCondition);
                    }
                    else if (timeDelta > spinTimeoutNanos)
                    {
                        Thread.yield();
                    }
                }
                counter = SPIN_TRIES;
            }
        }
        while (true);
	}

    @Override
	public <T> T waitFor(WaitCondition<T> waitCondition, long timeOut, TimeUnit unit) throws InterruptedException {
		long startTime = 0;
	    int counter = SPIN_TRIES;
	    long endTime=System.nanoTime()+unit.toNanos(timeOut);
	    do
	    {
	    	waitCondition.doComplete();
	        if (waitCondition.isComplete())
	        {
	            break;
	        }
	        if(System.nanoTime()>=endTime)
	        {
	        	break;
	        }
	        if (0 == --counter)
	        {
	            if (0 == startTime)
	            {
	                startTime = System.nanoTime();
	            }
	            else
	            {
	                long timeDelta = System.nanoTime() - startTime;
	                if (timeDelta > yieldTimeoutNanos)
	                {
	                    return fallbackStrategy.waitFor(waitCondition,timeOut,unit);
	                }
	                else if (timeDelta > spinTimeoutNanos)
	                {
	                    Thread.yield();
	                }
	            }
	            counter = SPIN_TRIES;
	        }
	    }
	    while (true);
	    return waitCondition.result();
	}

	@Override
    public void signalAllWhenBlocking()
    {
        fallbackStrategy.signalAllWhenBlocking();
    }
}
