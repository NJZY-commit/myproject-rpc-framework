package github.njzy.registry;

import github.njzy.extension.SPI;
import github.njzy.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author njzy
 * @package github.njzy.registry
 * @create 2022年03月17日 13:21
 */
@SPI
public interface ServiceDiscovery{

    /**
     * 通过 rpcServiceName 来找到指定的服务器
     *
     * @param rpcServiceName rpc服务pojo类
     * @return 服务器地址
     */
    InetSocketAddress lookupService(RpcRequest rpcServiceName);

}
