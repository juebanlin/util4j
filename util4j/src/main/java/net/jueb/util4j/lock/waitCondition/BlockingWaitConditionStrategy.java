package net.jueb.util4j.lock.waitCondition;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class BlockingWaitConditionStrategy implements WaitConditionStrategy
{
    private final Lock lock = new ReentrantLock();
    private final Condition processorNotifyCondition = lock.newCondition();

    @Override
	public <T> T waitFor(WaitCondition<T> waitCondition) throws InterruptedException {
    	waitCondition.doComplete();
    	if(!waitCondition.isComplete())
		{
    		lock.lock();
            try
            {
                while (!waitCondition.isComplete())
                {//条件是否成立
                    processorNotifyCondition.await();
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
	public <T> T waitFor(WaitCondition<T> waitCondition, long timeOut, TimeUnit unit) throws InterruptedException {
    	waitCondition.doComplete();
    	if(!waitCondition.isComplete())
		{
    		lock.lock();
            try
            {
                if(!waitCondition.isComplete())
                {//条件是否成立
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
