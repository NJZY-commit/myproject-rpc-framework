package github.njzy.remoting.transport;

import github.njzy.extension.SPI;
import github.njzy.remoting.dto.RpcRequest;

/**
 * @author njzy
 * @package github.njzy.remoting.transport
 * @create 2022年03月17日 13:09
 */
@SPI // 仅仅是一个标识，没有别的作用
public interface RpcRequestTransport {

    /**
     * 将rpc请求传送到远程服务器再返回结果
     *
     * @param rpcRequest
     * @return
     */
    Object sendRpcRequest(RpcRequest rpcRequest);

}
