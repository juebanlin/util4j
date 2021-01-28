package net.jueb.util4j.test;

import lombok.extern.slf4j.Slf4j;
import net.jueb.util4j.common.game.cdkey.CdkeyFactoryRandomImpl;
import net.jueb.util4j.lock.waiteStrategy.BlockingWaitConditionStrategy;
import net.jueb.util4j.lock.waiteStrategy.SleepingWaitConditionStrategy;
import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.RunnableQueue;
import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultQueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultQueueManager;
import net.jueb.util4j.queue.queueExecutor.queue.RunnableQueueWrapper;
import net.jueb.util4j.queue.taskQueue.Task;
import net.jueb.util4j.queue.taskQueue.TaskQueueExecutor;
import net.jueb.util4j.security.AesUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.jctools.queues.MpscLinkedQueue;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class TestDefaultQueues2 {

    /**
     * 一个简单的非并发阻塞计算任务
     * @return
     */
    public Runnable buildTask() {
        return ()->{
            CdkeyFactoryRandomImpl.RandomStringUtils.randomNumeric(2);
//            ThreadLocalRandom random=ThreadLocalRandom.current();
//            random.nextLong();
        };
//        ThreadLocalRandom random=ThreadLocalRandom.current();
//        String s= random.nextLong()+""+random.nextLong()+""+random.nextLong();
//        return () -> AesUtil.Base64Encode(s.getBytes());
    }

    class TpsStatus {
        /**
         * 总输入输出累计
         */
        AtomicLong totalIn = new AtomicLong();
        AtomicLong totalOut = new AtomicLong();
        /**
         * 队列输入输出累计
         */
        AtomicLong[] queue_in;
        AtomicLong[] queue_out;
        /**
         * 每个队列积压情况
         */
        AtomicLong[] queue_pendsize;

        /**
         * 每次打印的队列输出统计
         */
        AtomicLong[] queue_out_print;
        AtomicLong queue_allout_print=new AtomicLong();

        public void queueInit(int queueNum) {
            queue_in = new AtomicLong[queueNum];
            queue_out = new AtomicLong[queueNum];
            queue_pendsize=new AtomicLong[queueNum];
            queue_out_print=new AtomicLong[queueNum];
            for (int i = 0; i < queueNum; i++) {
                queue_in[i] = new AtomicLong();
                queue_out[i] = new AtomicLong();
                queue_pendsize[i] = new AtomicLong();
                queue_out_print[i] = new AtomicLong();
            }
            totalIn.set(0);
        }

        public void onQueueIn(int queueIndex) {
            totalIn.incrementAndGet();
            queue_in[queueIndex].incrementAndGet();
            queue_pendsize[queueIndex].incrementAndGet();

        }

        public void onQueueOut(int queueIndex) {
            totalOut.incrementAndGet();
            queue_out[queueIndex].incrementAndGet();
            queue_pendsize[queueIndex].decrementAndGet();
            queue_out_print[queueIndex].incrementAndGet();
            queue_allout_print.incrementAndGet();
        }

        public void print() {
            int queueNum = queue_in.length;
            long[] queue_print = new long[queueNum];
            for (int i = 0; i < queueNum; i++) {
                queue_print[i] = queue_out_print[i].getAndSet(0);
            }
            long all=queue_allout_print.getAndSet(0);
            long avg=all / queueNum;
            log.info("\n所有队列每秒共处理任务:{},队列平均每秒吞吐:{}" +
                            "\n\t 总任务in:{} out:{}" +
                            "\n\t 单个队列每秒    :{}" +
                            "\n\t 单个队列累计in  :{}"+
                            "\n\t 单个队列累计out :{}"+
                            "\n\t 单个队列积压    :{}"
                    , all,avg,
                    totalIn,totalOut,
                    queue_print,
                    queue_in,
                    queue_out,
                    queue_pendsize
                    );
        }

    }

    /**
     * @param queueCount 队列数量
     * @param queues
     */
    public void test(final long timeMillsLimit, final QueueGroupExecutor queues, final int queueCount, int threadNum) throws InterruptedException {
        ExecutorService workerpool = Executors.newFixedThreadPool(threadNum);
        final long endTime = System.currentTimeMillis() + timeMillsLimit;
        TpsStatus tpsStatus = new TpsStatus();
        tpsStatus.queueInit(queueCount);
        for (int i = 0; i < threadNum; i++) {
            workerpool.submit(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                while (System.currentTimeMillis() < endTime) {
                    long count = random.nextInt(3000)+100;
                    for (long l = 0; l < count; l++) {
                        final int queueIndex = random.nextInt(queueCount);
                        Runnable task = buildTask();
                        QueueExecutor queueExecutor = queues.getQueueExecutor(queueIndex + "");
                        tpsStatus.onQueueIn(queueIndex);
                        queueExecutor.execute(() -> {
                            try {
                                task.run();
                            }finally {
                                tpsStatus.onQueueOut(queueIndex);
                            }
                        });
                    }
                    try {
                        Thread.sleep(random.nextInt(1)+1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            tpsStatus.print();
        }, 0, 1, TimeUnit.SECONDS);
        Thread.sleep(timeMillsLimit);
        scheduledFuture.cancel(false);
    }


    protected static QueueGroupExecutor buildStageByMpMc(int threadMin, int threadMax) {
        //多生产多消费者队列(线程竞争队列)
        Queue<Runnable> bossQueue = new ConcurrentLinkedQueue<>();
        DefaultQueueGroupExecutor.Builder b = new DefaultQueueGroupExecutor.Builder();
        b.setAssistExecutor(Executors.newSingleThreadExecutor());
        return b.setMaxPoolSize(threadMax)
                .setCorePoolSize(threadMin)
                .setQueueGroupManagerr(new DefaultQueueManager(QueueFactory.MPSC_QUEUE_FACTORY))
                .setKeepAliveTime(3, TimeUnit.SECONDS)
                .setBossQueue(bossQueue)
                .setWaitConditionStrategy(new BlockingWaitConditionStrategy()).build();
    }

    public static void main(String[] args) throws InterruptedException {
        TestDefaultQueues2 tq = new TestDefaultQueues2();
        Thread.sleep(3000);
        System.out.println("队列测试开始");
        /**
         * 多队列多线程测试
         */
        QueueGroupExecutor ft = buildStageByMpMc(2, 8);
        System.out.println("#########1");
        tq.test(1000 * 60, ft, 8, 8);//1000W随机分配到10个队列
        QueueExecutor queueExecutor = ft.getQueueExecutor("0");
        log.info(queueExecutor.handleCount()+"-"+queueExecutor.maxProcessCount());
//        所有队列每秒共处理任务:6486921,队列平均每秒吞吐:648692
//        总任务in:329957870 out:329957448
//        单个队列每秒    :[647878, 648773, 650353, 649312, 648036, 649389, 648005, 648428, 649274, 647470]
//        单个队列累计in  :[32998175, 32992722, 32993563, 32995935, 32995988, 33000259, 33004883, 32990645, 32998104, 32987947]
//        单个队列累计out :[32998127, 32992691, 32993539, 32995904, 32995987, 33000262, 33004871, 32990645, 32998012, 32987921]
//        单个队列积压    :[71, 52, 60, 52, 17, 16, 19, 36, 99, 39]

//        所有队列每秒共处理任务:7097526,队列平均每秒吞吐:887190
//        总任务in:425484376 out:425484337
//        单个队列每秒    :[887458, 887239, 887940, 887287, 886649, 886094, 888684, 886178]
//        单个队列累计in  :[53174881, 53180889, 53193796, 53171070, 53189552, 53197105, 53193505, 53184004]
//        单个队列累计out :[53174895, 53180897, 53193809, 53171074, 53189558, 53197108, 53193496, 53183991]
//        单个队列积压    :[3, 10, 6, 4, 11, 5, 21, 18]

        Thread.sleep(10000000);
    }
}