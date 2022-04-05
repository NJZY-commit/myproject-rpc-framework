package github.njzy.remoting.transport.netty.server;

import github.njzy.config.RpcServiceConfig;
import github.njzy.factory.SingletonFactory;
import github.njzy.provider.impl.zkServiceProviderImpl;
import github.njzy.provider.zookeeper.ServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    public void start(){

    }


}
