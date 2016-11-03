package net.jueb.util4j.lock.waitCondition;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SleepingWaitConditionStrategy implements WaitConditionStrategy
{
	protected final Logger log=LoggerFactory.getLogger(getClass());
    private static final int DEFAULT_RETRIES = 200;

    private final int retries;

    public SleepingWaitConditionStrategy()
    {
        this(DEFAULT_RETRIES);
    }

    public SleepingWaitConditionStrategy(int retries)
    {
        this.retries = retries;
    }
    
    @Override
	public <T> T waitFor(WaitCondition<T> waitCondition) throws InterruptedException {
    	int counter = retries;
    	waitCondition.doComplete();
    	while (!waitCondition.isComplete())
        {
            counter = applyWaitMethod(counter);
            waitCondition.doComplete();
        }
        return waitCondition.result();
	}

    @Override
	public <T> T waitFor(WaitCondition<T> waitCondition, long timeOut, TimeUnit unit) throws InterruptedException {
		int counter = retries;
		long endTime=System.nanoTime()+unit.toNanos(timeOut);
		waitCondition.doComplete();
		while (!waitCondition.isComplete())
	    {
			if(System.nanoTime()>=endTime)
			{
				break;
			}
	        counter = applyWaitMethod(counter);
	        waitCondition.doComplete();
	    }
	    return waitCondition.result();
	}

	@Override
    public void signalAllWhenBlocking()
    {
    }
    
    private int applyWaitMethod(int counter)
    {
        if (counter > 100)
        {
            --counter;
        }
        else if (counter > 0)
        {
            --counter;
            Thread.yield();
        }
        else
        {
            LockSupport.parkNanos(1L);
        }
        return counter;
    }
}
