package net.jueb.util4j.lock.waitCondition;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TimeoutBlockingWaitConditionStrategy implements WaitConditionStrategy
{
    private final Lock lock = new ReentrantLock();
    private final Condition processorNotifyCondition = lock.newCondition();
    private final long timeoutInNanos;

    public TimeoutBlockingWaitConditionStrategy(final long timeout, final TimeUnit units)
    {
        timeoutInNanos = units.toNanos(timeout);
    }
    
    
    @Override
	public <T> T waitFor(WaitCondition<T> waitCondition) throws InterruptedException{
    	 long nanos = timeoutInNanos;
    	 waitCondition.doComplete();
    	 if (!waitCondition.isComplete())
         {
             lock.lock();
             try
             {
                 while (!waitCondition.isComplete())
                 {
                	 waitCondition.doComplete();
                     nanos = processorNotifyCondition.awaitNanos(nanos);
                     if (nanos <= 0)
                     {
                         break;
                     }
                 }
             }
             finally
             {
                 lock.unlock();
             }
         }
         return waitCondition.result();
	}

    @Override
	public <T> T waitFor(WaitCondition<T> waitCondition, long timeOut, TimeUnit unit) throws InterruptedException {
		 waitCondition.doComplete();
		 if (!waitCondition.isComplete())
	     {
	         lock.lock();
	         try
	         {
	             if (!waitCondition.isComplete())
	             {
	            	 processorNotifyCondition.await(timeOut, unit);
	            	 waitCondition.doComplete();
	             }
	         }
	         finally
	         {
	             lock.unlock();
	         }
	     }
	     return waitCondition.result();
	}


	@Override
    public void signalAllWhenBlocking()
    {
        lock.lock();
        try
        {
            processorNotifyCondition.signalAll();
        }
        finally
        {
            lock.unlock();
        }
    }
}
