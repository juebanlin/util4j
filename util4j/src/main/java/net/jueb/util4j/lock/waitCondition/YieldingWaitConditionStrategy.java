package net.jueb.util4j.lock.waitCondition;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class YieldingWaitConditionStrategy implements WaitConditionStrategy
{
	protected final Logger log=LoggerFactory.getLogger(getClass());
    private static final int SPIN_TRIES = 1000;

    @Override
	public <T> T waitFor(WaitCondition<T> waitCondition) throws InterruptedException {
    	waitCondition.doComplete();
    	int counter = SPIN_TRIES;
    	while (!waitCondition.isComplete())
        {
            counter = applyWaitMethod(counter);
            waitCondition.doComplete();
        }
    	return waitCondition.result();
	}
    
    @Override
	public <T> T waitFor(WaitCondition<T> waitCondition, long timeOut, TimeUnit unit) throws InterruptedException {
    	long endTime=System.nanoTime()+unit.toNanos(timeOut);
    	int counter = SPIN_TRIES;
    	waitCondition.doComplete();
    	while (!waitCondition.isComplete())
        {
    		if(System.nanoTime()>=endTime)
    		{
    			break;
    		}
            counter = applyWaitMethod(counter);//等待
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
        if (0 == counter)
        {
            Thread.yield();
        }
        else
        {
            --counter;
        }
        return counter;
    }
}
