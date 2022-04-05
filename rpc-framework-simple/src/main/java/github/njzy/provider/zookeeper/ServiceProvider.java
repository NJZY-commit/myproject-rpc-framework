package github.njzy.provider.zookeeper;

import github.njzy.annotation.RpcService;
import github.njzy.config.RpcServiceConfig;

/**
 * @author njzy
 * @package github.njzy.provider.impl
 * @create 2022年04月02日 14:57
 */
public interface ServiceProvider {

    /**
     * 注册服务
     *     为了注册服务，我们需要服务配置类: RpcServiceConfig
     */
    void addService(RpcServiceConfig serviceConfig);

    /**
     * 寻找服务
     */
    Object getService(String rpcServiceName);

    /**
     * 服务发布
     */
    void publishService(RpcServiceConfig serviceConfig);

}
