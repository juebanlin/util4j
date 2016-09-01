package net.jueb.util4j.lock.waitCondition;

import java.util.concurrent.TimeUnit;

public interface WaitConditionStrategy
{
   
    <T> T waitFor(WaitCondition<T> waitCondition) throws InterruptedException;

    <T> T waitFor(WaitCondition<T> waitCondition,long timeOut,TimeUnit unit) throws InterruptedException;
    
    void signalAllWhenBlocking();
}
