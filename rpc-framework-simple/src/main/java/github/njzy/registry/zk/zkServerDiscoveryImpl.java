package github.njzy.registry.zk;

import github.njzy.enums.RpcErrorMessageEnum;
import github.njzy.exception.RpcException;
import github.njzy.extension.ExtensionLoader;
import github.njzy.loadbalance.LoadBalance;
import github.njzy.registry.ServiceDiscovery;
import github.njzy.registry.zk.util.CuratorUtils;
import github.njzy.remoting.dto.RpcRequest;
import github.njzy.utils.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author njzy
 * @package github.njzy.registry.zk
 * @create 2022年04月05日 10:43
 */
@Slf4j
public class zkServerDiscoveryImpl implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public zkServerDiscoveryImpl() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName(); //
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        // load balancing
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
