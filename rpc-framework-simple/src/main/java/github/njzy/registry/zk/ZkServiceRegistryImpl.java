package github.njzy.registry.zk;

import github.njzy.registry.ServiceRegistry;
import github.njzy.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * service registration  based on zookeeper 基于zookeeper的服务注册
 *
 * 操作总结：
 *        1. 创建服务路径；
 *        2. 获取Curator客户端;
 *        3. 创建持久节点。
 *
 * @author shuang.kou
 * @createTime 2020年05月31日 10:56:00
 */
@Slf4j
public class ZkServiceRegistryImpl implements ServiceRegistry {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString(); // 初始化服务路径
        CuratorFramework zkClient = CuratorUtils.getZkClient(); // 获取zookeeper客户端
        CuratorUtils.createPersistentNode(zkClient, servicePath); // 创建持久节点
    }
}
