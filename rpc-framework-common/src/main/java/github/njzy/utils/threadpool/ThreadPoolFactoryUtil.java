package github.njzy.utils.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author njzy
 * @package github.njzy.utils.threadpool
 * @create 2022年04月04日 15:59
 */
@Slf4j
public class ThreadPoolFactoryUtil {

    private static final Map<String, ExecutorService> map = new ConcurrentHashMap<>(); // 初始化容器，存储线程池

    public ThreadPoolFactoryUtil() {
    }

    /**
     * 关闭所有的线程池
     */
    public void shutDownAllThreadPoll(){
        log.info("Shutting down all threads");
        map.entrySet().parallelStream().forEach(entry -> {
            ExecutorService service = entry.getValue(); // 服务器
            service.shutdown(); // 关闭服务器
            log.info("Shutting down");
            // 超时的操作：如果服务器在10秒后还没关闭，再一次关闭
            try {
                service.awaitTermination(10L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Thread pool never terminated");
                service.shutdown(); // 关闭服务器
            }
        });
    }

    /**
     * 创建线程池的方法
     *
     * @return
     */
    public static ExecutorService createThreadPool(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean isDaemon){
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, isDaemon);
        return new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(), customThreadPoolConfig.getMaxPoolSize(),
                customThreadPoolConfig.getKeepAliveTime(), customThreadPoolConfig.getTimeUnit(), customThreadPoolConfig.getWorkQueue(),
                threadFactory);
    }

    /**
     * 创建线程工厂
     *    说明：在这里之所以要创建一个ThreadFactory，是因为在创建线程池的方法中，ThreadPoolExecutor方法的够遭参数有这一参数
     *
     * @return
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean isDaemon){
        if (threadNamePrefix != null){
            if(isDaemon != null){
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(isDaemon).build();
            }else{
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }

        // 如果线程没有线程名前缀，就直接返回默认的线程工厂类
        return Executors.defaultThreadFactory();
    }
}
