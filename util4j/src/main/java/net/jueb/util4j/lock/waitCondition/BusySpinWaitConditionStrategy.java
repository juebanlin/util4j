package net.jueb.util4j.lock.waitCondition;

import java.util.concurrent.TimeUnit;

public final class BusySpinWaitConditionStrategy implements WaitConditionStrategy
{
    @Override
	public <T> T waitFor(WaitCondition<T> waitCondition) throws InterruptedException {
    	waitCondition.doComplete();
    	while(!waitCondition.isComplete())
		{
			waitCondition.doComplete();
		}
		return waitCondition.result();
	}
    
    @Override
	public <T> T waitFor(WaitCondition<T> waitCondition, long timeOut, TimeUnit unit) throws InterruptedException {
		long endTime=System.nanoTime()+unit.toNanos(timeOut);
    	waitCondition.doComplete();
    	while(!waitCondition.isComplete())
		{
    		if(System.nanoTime()>=endTime)
    		{
    			break;
    		}
			waitCondition.doComplete();
		}
		return waitCondition.result();
	}

    @Override
    public void signalAllWhenBlocking()
    {
    	
    }
}
