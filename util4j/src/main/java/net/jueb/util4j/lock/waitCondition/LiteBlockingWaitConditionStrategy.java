package net.jueb.util4j.lock.waitCondition;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class LiteBlockingWaitConditionStrategy implements WaitConditionStrategy
{
    private final Lock lock = new ReentrantLock();
    private final Condition processorNotifyCondition = lock.newCondition();
    private final AtomicBoolean signalNeeded = new AtomicBoolean(false);

    @Override
	public <T> T waitFor(WaitCondition<T> waitCondition) throws InterruptedException {
    	waitCondition.doComplete(); 
    	if (!waitCondition.isComplete())
         {
             lock.lock();
             try
             {
                 do
                 {
                     signalNeeded.getAndSet(true);
                     if (waitCondition.isComplete())
                     {
                         break;
                     }
                     waitCondition.doComplete();
                     processorNotifyCondition.await();
                 }
                 while (!waitCondition.isComplete());
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
            	 signalNeeded.getAndSet(true);
            	 if (!waitCondition.isComplete())
                 {
            		 processorNotifyCondition.await(timeOut,unit);
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
        if (signalNeeded.getAndSet(false))
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
}
