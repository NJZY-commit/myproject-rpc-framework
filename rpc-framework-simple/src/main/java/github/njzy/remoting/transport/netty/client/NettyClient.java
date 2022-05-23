package github.njzy.remoting.transport.netty.client;

import github.njzy.enums.CompressTypeEnum;
import github.njzy.enums.SerializationTypeEnum;
import github.njzy.extension.ExtensionLoader;
import github.njzy.factory.SingletonFactory;
import github.njzy.registry.ServiceDiscovery;
import github.njzy.remoting.constants.RpcConstants;
import github.njzy.remoting.dto.RpcMessage;
import github.njzy.remoting.dto.RpcRequest;
import github.njzy.remoting.dto.RpcResponse;
import github.njzy.remoting.transport.RpcRequestTransport;
import github.njzy.remoting.transport.netty.codec.RpcMessageDecoder;
import github.njzy.remoting.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 客户端类
 *    作用：客户端的主要核心就是sendMessage()，这个方法可以将RpcRequest对象发送到服务端，并且你也可以同步获取到服务端返回的结果(即RpcResponse)
 *
 * @author njzy
 * @package github.njzy.remoting.transport.netty
 * @create 2022年03月16日 16:29
 */
@Slf4j
public class NettyClient implements RpcRequestTransport {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    private final Bootstrap b;
    private final ServiceDiscovery serviceDiscovery; // 服务发现
    private final UnprocessedRequests unprocessedRequests; // 未处理信息请求
    private final ChannelProvider channelProvider; // channel提供者
    private final EventLoopGroup eventLoopGroup;


    /*初始化相关资源，比如：EventLoopGroup, Bootstrap*/
    public NettyClient() {
            eventLoopGroup = new NioEventLoopGroup();
            b = new Bootstrap();
            b.group(eventLoopGroup) //设置用于处理Channel所有事件的eventLoopGroup
                .channel(NioSocketChannel.class) // 指定Channel的实现类
                .handler(new LoggingHandler(LogLevel.INFO)) // 设置将被添加到ChannelPipeline以及接收事件通知的ChannelHandler
                //  The timeout period of the connection.  连接的超时时间。
                //  If this time is exceeded or the connection cannot be established, the connection fails. 如果超过此时间或无法建立连接，则连接失败。
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 设置ChannelOption
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        // If no data is sent to the server within 15 seconds, a heartbeat request is sent
                        // 如果 15 秒内没有数据发送到服务器，则发送心跳请求
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder()); // 加入自定义协议编码器
                        p.addLast(new RpcMessageDecoder()); // 加入自定义协议解码器
                        p.addLast(new NettyRpcClientHandler()); // 加入NettyRpc客户端处理器
                    }
                });
        serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    ///**
    // * 发送方法
    // *
    // * @return RpcResponse对象
    // */
    //public RpcResponse sendMessage(RpcRequest rpcRequest) {
    //
    //    try {
    //        ChannelFuture future = b.connect(host, port).sync();
    //        LOGGER.info("client connect {}", host + ":" + port);
    //        Channel channel = future.channel(); // 返回与此future关联的 IO 操作发生的通道
    //        LOGGER.info("send Message");
    //        if (channel != null) {
    //            channel.writeAndFlush(rpcRequest).addListener(future1 -> {
    //                if (future1.isSuccess()) {
    //                    LOGGER.info("client send message: [{}]", rpcRequest.toString());
    //                } else {
    //                    LOGGER.error("send failed: ", future.cause());
    //                }
    //            });
    //            // 阻塞等待，直到channel关闭
    //            channel.closeFuture().sync();
    //            // 将服务端返回的数据(即rpcResponse对象)取出
    //            AttributeKey<Object> key = AttributeKey.valueOf("rpcResponse");
    //            return (RpcResponse) channel.attr(key).get();
    //        }
    //    } catch (InterruptedException e) {
    //        LOGGER.error("occur exception when connect server:", e);
    //    }
    //    return null;
    //}

    /**
     * 发送请求
     * @param rpcRequest
     * @return
     */
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // build return value 用来接收从服务端得到的结果
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // get server address （到Zookeeper中的注册表中去查询服务地址）
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        // get  server address related channel 连接与该服务地址对应的服务器之间的通道
        Channel channel = getChannel(inetSocketAddress);

        if (channel.isActive()) { // 如果channel是活动的
            // put unprocessed request 未处理的请求存放到unprocessedRequests里(底层是concurrentHashMap)
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);

            /*封装信息*/
            RpcMessage rpcMessage = RpcMessage.builder()
                    .data(rpcRequest)
                    .codec(SerializationTypeEnum.HESSIAN.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .messageType(RpcConstants.REQUEST_TYPE)
                    .build();

            /*发送消息*/
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcMessage);  // 发送成功后，返回发送的内容
                } else {
                    future.channel().close(); // 关闭通道
                    resultFuture.completeExceptionally(future.cause()); // 获取异常信息
                    log.error("Send failed:", future.cause());
                }
            });

        } else {
            throw new IllegalStateException();
        }

        return resultFuture;
    }

    /**
     * 获取channel封装成方法
     * @param inetSocketAddress
     * @return
     */
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    /**
     * 连接封装成方法
     * @param inetSocketAddress
     * @return
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>(); // 当多个线程存在依赖组合时，有一个简单的方式就是利用CompletableFuture
        b.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) { //当连接操作成功时，返回true
                log.info("The client has connected [{}] successful!", inetSocketAddress.toString()); // 记录日志
                completableFuture.complete(future.channel()); // 返回channel对象到CompletableFuture中
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get(); // get()是阻塞的，按正常的流程，如果使用这个方法需要设置超时时间
    }
}
