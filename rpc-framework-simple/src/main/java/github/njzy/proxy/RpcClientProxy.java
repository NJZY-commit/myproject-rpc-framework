package github.njzy.proxy;

import github.njzy.config.RpcServiceConfig;
import github.njzy.enums.RpcErrorMessageEnum;
import github.njzy.enums.RpcResponseCodeEnum;
import github.njzy.exception.RpcException;
import github.njzy.remoting.dto.RpcRequest;
import github.njzy.remoting.dto.RpcResponse;
import github.njzy.remoting.transport.RpcRequestTransport;
import github.njzy.remoting.transport.netty.client.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author njzy
 * @package github.njzy.proxy
 * @create 2022年05月23日 23:35
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private static final String INTERFACE_NAME = "interfaceName";

    /**
     * 用来发送请求到服务端
     * Used to send requests to the server.And there are two implementations: socket and netty
     */
    private final RpcRequestTransport rpcRequestTransport;
    private final RpcServiceConfig rpcServiceConfig;

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, RpcServiceConfig rpcServiceConfig) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = rpcServiceConfig;
    }

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = new RpcServiceConfig();
    }

    /**
     * get the proxy object 获取代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

        log.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder()
                .methodName(method.getName()) // 设置方法名称
                .parameters(objects) // 设置参数
                .interfaceName(method.getDeclaringClass().getName()) // 设置接口名称
                .paramTypes(method.getParameterTypes()) // 设置参数类型
                .requestId(UUID.randomUUID().toString()) // 设置请求id
                .group(rpcServiceConfig.getGroup()) // 设置组名
                .version(rpcServiceConfig.getVersion()) // 设置版本号
                .build();
        RpcResponse<Object> rpcResponse = null; // 用来存储服务器返回的消息

        /*使用Netty来实现网络传输
         *  CompletableFuture类专门用来处理异步任务的时候，尤其是多个线程之间存在依赖组合
         * */
        if (rpcRequestTransport instanceof NettyClient) {
            CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) rpcRequestTransport.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get(); // 获取方法
        }

        this.check(rpcResponse, rpcRequest);
        return rpcResponse.getData(); // 返回数据
    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
