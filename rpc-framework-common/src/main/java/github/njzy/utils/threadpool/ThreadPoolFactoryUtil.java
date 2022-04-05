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
     * 如果服务器被弃用，就注册一个服务器
     */
    public ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix){
        CustomThreadPoolConfig config = new CustomThreadPoolConfig(); // 按照默认的线程池参数来创建服务器
        return createThreadPool(config,threadNamePrefix,false);
    }

    // 自己定义了一个线程池，就直接传入参数即可
    public ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix, CustomThreadPoolConfig config){
        return createThreadPool(config,threadNamePrefix,false);
    }

    // 自定义，如果
    public ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix, CustomThreadPoolConfig config, Boolean isDaemon){
        ExecutorService threadPool = map.computeIfAbsent(threadNamePrefix, k -> createThreadPool(config, threadNamePrefix, isDaemon));
                // 如果当前线程名的服务器已经被关闭了，就要新建一个
                if(threadPool.isShutdown() || threadPool.isTerminated()){
                    map.remove(threadNamePrefix); // 先从容器中把服务器删除
                    threadPool = createThreadPool(config, threadNamePrefix, isDaemon); // 创建对象
                    map.put(threadNamePrefix,threadPool); // 存入容器
                }
        return threadPool;
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
