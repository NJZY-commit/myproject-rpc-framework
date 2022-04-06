package github.njzy.remoting.transport.netty.server;

import github.njzy.config.CustomShutdownHook;
import github.njzy.config.RpcServiceConfig;
import github.njzy.factory.SingletonFactory;
import github.njzy.provider.impl.zkServiceProviderImpl;
import github.njzy.provider.zookeeper.ServiceProvider;
import github.njzy.utils.RunTimeUtil;
import github.njzy.utils.threadpool.ThreadPoolFactoryUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * 基于Netty实现的服务器，应用于RPC框架
 *
 * @author njzy
 * @package github.njzy.remoting.transport.netty.server
 * @create 2022年04月02日 14:12
 */
@Slf4j
@Component
public class NettyRpcServer {

    // 配置端口号
    public static final int port = 9898;

    // 使用工厂模式来创建服务器提供器
    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(zkServiceProviderImpl.class); // 工厂模式

    /**
     * 注册服务
     */
    public void registerService(RpcServiceConfig rpcServiceConfig){
        serviceProvider.publishService(rpcServiceConfig); // 发布服务
    }


    /**
     * 启动服务
     */
    @SneakyThrows // 关闭异常
    public void start(){
        CustomShutdownHook.getCustomShutdownHook().unregisterAll(); // 清除所有注册表内的信息
        String address = InetAddress.getLocalHost().getHostAddress(); // 获取本地的地址

        // todo: 创建两个NioEvent分组，其中一个是bossGroup，另一个是workGroup
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1); // bossGroup用于处理客户端连接请求
        NioEventLoopGroup workGroup = new NioEventLoopGroup(); // workGroup用于处理具体的操作

        // 创建一个默认的服务处理组
        DefaultEventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(RunTimeUtil.getCpuThreadCount() * 2, ThreadPoolFactoryUtil.createThreadFactory("server-event-handler-group", false));

        // todo: 创建服务器引导类 serverBoorStrap
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 给服务端的引导类配置两个线程，来确定线程模型
        serverBootstrap.group(bossGroup,workGroup) // 因为是服务端，需要接收来自客户端的请求并且处理请求，所以把这个公用拆分为两个处理会更好
                .childOption(ChannelOption.TCP_NODELAY, true)  // 用来开启TCP默认的Nagle算法，以此发送大数据快减少网络传输
                .childOption(ChannelOption.SO_KEEPALIVE, true) // 开启底层的心跳机制，用来确保传输还存在
                .option(ChannelOption.SO_BACKLOG, 200) // 该参数对应的是tcp/ip协议listen函数中的backlog参数，由于服务器处理客户端的请求是按照顺序处理的，所以同一时间只能处理一个请求，但是当连接过于频繁时，可适当调整此参数的参数值，让存放连接的队列容量扩大一些，以减小压力
                .handler(new LoggingHandler(LogLevel.INFO)) // 设置日志记录的等级为info级别
                .childHandler(new ChannelInitializer<SocketChannel>() { // 当客户端第一次进行请求才会初始化
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 定义一条处理链
                        ChannelPipeline pipeline = ch.pipeline(); // SocketChannel间接继承了顶级父类Channel，在Channel类中有pipeline()返回当前线程绑定的pipeline
                        // pipeline通过addLast()添加处理的内容
                        pipeline.addLast(new IdleStateHandler(30L, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast();


                    }
                });

    }


}
