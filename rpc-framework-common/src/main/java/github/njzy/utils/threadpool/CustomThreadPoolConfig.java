package github.njzy.utils.threadpool;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的线程池配置类，可根据具体的业务需求修改参数
 *      说明：本项目中定义线程池的方式是使用了 ThreadPoolExecutor 的方式，
 *           在该类的构造器内有最多 7 个参数，它们分别是：
 *               1.corePoolSize --- 核心线程数，线程池中始终存活的线程数。
 *               2.maximumPoolSize --- 最大线程数，线程池中允许的最大线程数
 *               3.keepAliveTime --- 最大线程数可以存活的时间
 *               4。unit --- 单位是和keepAliveTime 存活时间配合使用的，合在一起用于设定线程的存活时间
 *               5.workQueue --- 一个阻塞队列，用来存储线程池等待执行的任务，均为线程安全
 *               6.threadFactory --- 线程工厂，主要用来创建线程，默认为正常优先级、非守护线程。
 *               7.handler --- 拒绝策略，拒绝处理任务时的策略
 *
 * @author njzy
 * @package github.njzy.utils.threadpool
 * @create 2022年04月04日 16:07
 */
@Getter
@Setter
public class CustomThreadPoolConfig {

    /**
     * 默认的线程池配置:
     *     线程池核心大小、最大线程池容量、单个线程的活动时间单元、阻塞队列的长度
     */
    private static final int DEFAULT_CORE_POOL_SIZE = 10;
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = 100;
    private static final int DEFAULT_KEEP_ALIVE_TIME = 1;
    private static final TimeUnit DEFAULT_TIME_UNIT= TimeUnit.MINUTES;
    private static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 100;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;


    /**
     * 可改动的线程池配置
     */
    private int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    private int maxPoolSize = DEFAULT_MAXIMUM_POOL_SIZE;
    private int keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
    private TimeUnit timeUnit = DEFAULT_TIME_UNIT;

    /**
     * 使用有界队列
     *        一个阻塞队列，用来存储线程池等待执行的任务
     *
     */
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);

}
