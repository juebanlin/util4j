package net.jueb.util4j.queue.queueExecutor.groupExecutor.impl;

import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;


/**
 * @Classname QueueUtil
 * @Description
 * @Date 2021/2/2 3:00 下午
 * @Created by helin
 */
public class QueueUtil {

    static ThreadLocal<QueueExecutor> loc_QueueExecutor=new ThreadLocal();

    /**
     * 获取当前线程所在的队列执行器
     * @return
     */
    public static QueueExecutor getExecutor(){
        return loc_QueueExecutor.get();
    }

    static void setExecutor(QueueExecutor queueExecutor){
        loc_QueueExecutor.set(queueExecutor);
    }

    static void clearExecutor(){
        loc_QueueExecutor.remove();
    }
}
