package github.njzy.config;

import github.njzy.registry.zk.util.CuratorUtils;
import github.njzy.remoting.transport.netty.server.NettyRpcServer;
import github.njzy.utils.threadpool.ThreadPoolFactoryUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * 创建这个类的目的就是在服务器关闭时，可以有人去做一些事儿，比如注销服务
 *
 * @author njzy
 * @package github.njzy.config
 * @create 2022年04月04日 15:39
 */
@Slf4j
public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    /**
     * 注销所有服务注册
     *      所谓的清除操作：在底层实现中，其实是把注册的地址值与对应key从集合中移除
     */
    public void unregisterAll(){
        log.info("创建CustomShutdownHook来注销所有已注册服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.port);
                // todo: 清除服务器，而能够做到这一个操作的只有Curator客户端框架，所以我要写一个客户端框架工具类，封装一些操作
                // todo: 由于输入的参数需要client，因此在CuratorUtils中还需自定义一个获取客户端的方法
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), socketAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            // todo: 关闭线程池
            ThreadPoolFactoryUtil.shutDownAllThreadPoll();
        }));
    }

}
