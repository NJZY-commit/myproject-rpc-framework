package github.njzy.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册的顶级接口，里面定义了服务注册的方法，方便后续的子类去实现
 *
 * @author njzy
 * @package github.njzy.registry
 * @create 2022年04月02日 17:00
 */
public interface ServiceRegistry {

    /* 1. rpc服务器名；2，服务器地址 */
    void registerService(String rpcServerName, InetSocketAddress inetSocketAddress);
}
