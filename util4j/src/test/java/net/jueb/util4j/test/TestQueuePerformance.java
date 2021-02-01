package net.jueb.util4j.test;

import lombok.extern.slf4j.Slf4j;
import net.jueb.util4j.common.game.cdkey.CdkeyFactoryRandomImpl;
import net.jueb.util4j.lock.waiteStrategy.BlockingWaitConditionStrategy;
import net.jueb.util4j.lock.waiteStrategy.SleepingWaitConditionStrategy;
import net.jueb.util4j.lock.waiteStrategy.YieldingWaitConditionStrategy;
import net.jueb.util4j.queue.queueExecutor.QueueFactory;
import net.jueb.util4j.queue.queueExecutor.executor.QueueExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultQueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultQueueManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.adapter.ThreadPoolQueueGroupExecutor;
import org.jctools.queues.atomic.MpmcAtomicArrayQueue;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class TestQueuePerformance {

    public static interface TestQueues{
         Executor getQueue(String ket);
    }

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
    public void test(final long timeMillsLimit, final TestQueues queues, final int queueCount, int threadNum) throws InterruptedException {
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
                        Executor queueExecutor = queues.getQueue(queueIndex + "");
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


    protected static QueueGroupExecutor buildStageByMpMc1(int threadMin, int threadMax) {
        //多生产多消费者队列(线程竞争队列)
        Queue<Runnable> bossQueue = new ConcurrentLinkedQueue<>();
        DefaultQueueGroupExecutor.Builder b = new DefaultQueueGroupExecutor.Builder();
        b.setAssistExecutor(Executors.newSingleThreadExecutor());
        return b.setMaxPoolSize(threadMax)
                .setCorePoolSize(threadMin)
                .setQueueGroupManagerr(new DefaultQueueManager(QueueFactory.MPSC_QUEUE_FACTORY))
                .setKeepAliveTime(10, TimeUnit.SECONDS)
                .setBossQueue(bossQueue)
//                .setAssistExecutor(Executors.newSingleThreadExecutor())
                .setWaitConditionStrategy(new BlockingWaitConditionStrategy()).build();
    }
    protected static QueueGroupExecutor buildStageByMpMc2(int threadMin, int threadMax) {
        //多生产多消费者队列(线程竞争队列)
        Queue<Runnable> bossQueue = new MpmcAtomicArrayQueue<>(Short.MAX_VALUE);//容量=积压上限=取决于队列总数+其它非队列事件任务
        DefaultQueueGroupExecutor.Builder b = new DefaultQueueGroupExecutor.Builder();
        b.setAssistExecutor(Executors.newSingleThreadExecutor());
        return b.setMaxPoolSize(threadMax)
                .setCorePoolSize(threadMin)
                .setQueueGroupManagerr(new DefaultQueueManager(QueueFactory.MPSC_QUEUE_FACTORY))
                .setKeepAliveTime(10, TimeUnit.SECONDS)
                .setBossQueue(bossQueue)
//                .setAssistExecutor(Executors.newSingleThreadExecutor())
                .setWaitConditionStrategy(new SleepingWaitConditionStrategy()).build();
    }

    protected static QueueGroupExecutor buildStageByMpMc3(int threadMin, int threadMax) {
        //多生产多消费者队列(线程竞争队列)
        DefaultQueueManager queueExecutors = new DefaultQueueManager(QueueFactory.MPSC_QUEUE_FACTORY);
        ThreadPoolQueueGroupExecutor threadPoolQueueGroupExecutor=new ThreadPoolQueueGroupExecutor(threadMin,threadMax,new LinkedBlockingQueue<>(),queueExecutors);
        return threadPoolQueueGroupExecutor;
    }

    public static void main(String[] args) throws InterruptedException {
        TestQueuePerformance tq = new TestQueuePerformance();
        System.out.println("输入任意内容开始");
        new Scanner(System.in).nextLine();
        int maxCore=Runtime.getRuntime().availableProcessors();
        //多队列多线程测试
        {
            QueueGroupExecutor ft = buildStageByMpMc1(1, maxCore);
            TestQueues testQueues=new TestQueues(){
                @Override
                public Executor getQueue(String key) {
                    return ft.getQueueExecutor(key);
                }
            };
            System.out.println("#########1");
            tq.test(1000 * 60, testQueues, maxCore, maxCore);//1000W随机分配到10个队列
            QueueExecutor queueExecutor = ft.getQueueExecutor("0");
            log.info("队列事件次数:{},最大单次事件处理任务数量:{}",queueExecutor.handleCount(),queueExecutor.maxProcessCount());
        }

        {
//            QueueGroupExecutor ft = buildStageByMpMc3(1, maxCore);
//            TestQueues testQueues=new TestQueues(){
//                @Override
//                public Executor getQueue(String key) {
//                    return ft.getQueueExecutor(key);
//                }
//            };
//            System.out.println("#########1");
//            tq.test(1000 * 60, testQueues, maxCore, maxCore);//1000W随机分配到10个队列
//            QueueExecutor queueExecutor = ft.getQueueExecutor("0");
//            log.info("队列事件次数:{},最大单次事件处理任务数量:{}",queueExecutor.handleCount(),queueExecutor.maxProcessCount());
        }

        // buildStageByMpMc1
//        所有队列每秒共处理任务:8136162,队列平均每秒吞吐:1017020
//        总任务in:481443385 out:481442932
//        单个队列每秒    :[1016124, 1017687, 1016200, 1017774, 1016949, 1018026, 1015636, 1017761]
//        单个队列累计in  :[60174418, 60177475, 60182350, 60162423, 60183844, 60187188, 60190837, 60185207]
//        单个队列累计out :[60174388, 60177480, 60182280, 60162331, 60183860, 60187189, 60190841, 60185071]
//        单个队列积压    :[50, 21, 89, 100, 2, 14, 14, 148]
//        21-02-01 14:36:17.764 [main:68849] INFO  net.jueb.util4j.test.TestQueuePerformance - 844650-20331

        //test2

        Thread.sleep(10000000);
    }
}