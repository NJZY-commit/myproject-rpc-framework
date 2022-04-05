package github.njzy.remoting.transport.socket;

import github.njzy.extension.ExtensionLoader;
import github.njzy.registry.ServiceDiscovery;
import github.njzy.remoting.dto.RpcRequest;
import github.njzy.remoting.transport.RpcRequestTransport;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author njzy
 * @package github.njzy.remoting.transport.socket
 * @create 2022年03月17日 13:17
 */
@AllArgsConstructor
@Slf4j
public class SocketRpcClient implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // 1. 根据rpcRequest里的rpcServiceName获取服务器地址
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        try (Socket socket = new Socket();){
            socket.connect(inetSocketAddress);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
