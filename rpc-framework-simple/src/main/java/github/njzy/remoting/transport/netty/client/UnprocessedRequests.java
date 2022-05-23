package github.njzy.remoting.transport.netty.client;

import github.njzy.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author njzy
 * @package github.njzy.remoting.transport.netty.client
 * @create 2022年05月23日 23:07
 */
public class UnprocessedRequests {

    // 初始化用来存放 "服务器未处理的请求" 的容器
    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    /**
     * 存储
     * @param requestId 请求Id
     * @param future rpcResponse对象
     */
    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    public void complete(RpcResponse<Object> rpcResponse) {
        // 返回该requestId所对应的key
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        // 如果值不为空
        if (null != future) {
            future.complete(rpcResponse); // 另一个线程调用complete方法完成该Future，则所有阻塞在get()的线程都将获得返回结果
        } else {
            throw new IllegalStateException(); // 否则抛出非法状态异常
        }
    }

}
