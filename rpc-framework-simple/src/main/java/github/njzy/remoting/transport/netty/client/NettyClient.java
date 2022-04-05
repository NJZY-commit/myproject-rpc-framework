package github.njzy.remoting.transport.netty.client;

import github.njzy.remoting.dto.RpcRequest;
import github.njzy.remoting.dto.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端类
 *    作用：客户端的主要核心就是sendMessage()，这个方法可以将RpcRequest对象发送到服务端，并且你也可以同步获取到服务端返回的结果(即RpcResponse)
 *
 * @author njzy
 * @package github.njzy.remoting.transport.netty
 * @create 2022年03月16日 16:29
 */
public class NettyClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    private final String host;
    private final int port;
    private static final Bootstrap b;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /*初始化相关资源，比如：EventLoopGroup, Bootstrap*/
    static {
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            b = new Bootstrap();
            //new KryoSerializer();
    }

    /**
     * 发送方法
     *
     * @return RpcResponse对象
     */
    public RpcResponse sendMessage(RpcRequest rpcRequest) {

        try {
            ChannelFuture future = b.connect(host, port).sync();
            LOGGER.info("client connect {}", host + ":" + port);
            Channel channel = future.channel(); // 返回与此future关联的 IO 操作发生的通道
            LOGGER.info("send Message");
            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()) {
                        LOGGER.info("client send message: [{}]", rpcRequest.toString());
                    } else {
                        LOGGER.error("send failed: ", future.cause());
                    }
                });
                // 阻塞等待，直到channel关闭
                channel.closeFuture().sync();
                // 将服务端返回的数据(即rpcResponse对象)取出
                AttributeKey<Object> key = AttributeKey.valueOf("rpcResponse");
                return (RpcResponse) channel.attr(key).get();
            }
        } catch (InterruptedException e) {
            LOGGER.error("occur exception when connect server:", e);
        }
        return null;
    }

}
