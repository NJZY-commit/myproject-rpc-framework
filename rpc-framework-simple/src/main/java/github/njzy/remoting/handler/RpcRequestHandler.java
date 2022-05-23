package github.njzy.remoting.handler;

import github.njzy.exception.RpcException;
import github.njzy.factory.SingletonFactory;
import github.njzy.provider.impl.zkServiceProviderImpl;
import github.njzy.provider.zookeeper.ServiceProvider;
import github.njzy.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * RPC处理器
 *
 * @author njzy
 * @package github.njzy.remoting.handler
 * @create 2022年04月08日 15:44
 */
@Slf4j
public class RpcRequestHandler {

    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        this.serviceProvider = SingletonFactory.getInstance(zkServiceProviderImpl.class);
    }


    /**
     * 调用目标的方法
     *
     * @param rpcRequest
     * @param service
     * @return
     */
    public Object invokeTargetMethod(RpcRequest rpcRequest, Object service){
        Object result;

        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            //e.printStackTrace();
            throw new RpcException(e.getMessage(), e);
        }

        return result;
    }

    /**
     * Processing rpcRequest: call the corresponding method, and then return the method
     * 处理rpcRequest：调用对应的方法，然后返回方法
     */
    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }



}
