package github.njzy.loadbalance;

import github.njzy.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @author njzy
 * @package github.njzy.loadbalance
 * @create 2022年05月23日 23:26
 */
public class LoadBalance {

    /**
     * Choose one from the list of existing service addresses list
     *
     * @param serviceUrlList Service address list
     * @param rpcRequest
     * @return target service address
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);

}
