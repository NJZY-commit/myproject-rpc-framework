package github.njzy.provider.impl;

import github.njzy.config.RpcServiceConfig;
import github.njzy.extension.ExtensionLoader;
import github.njzy.provider.zookeeper.ServiceProvider;
import github.njzy.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author njzy
 * @package github.njzy.provider.impl
 * @create 2022年04月02日 14:58
 */
@Slf4j
public class zkServiceProviderImpl implements ServiceProvider {

    private Map<String,Object> services;
    private Set<String> registeredServices;
    private ServiceRegistry serviceRegistry;

    public zkServiceProviderImpl(){
        this.services = new ConcurrentHashMap<>();
        this.registeredServices = ConcurrentHashMap.newKeySet();
        this.serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");
    }


    @Override
    public void addService(RpcServiceConfig serviceConfig) {
        String rpcServiceName = serviceConfig.getRpcServiceName();
        if (services.containsKey(rpcServiceName)){
            return;
        }
        registeredServices.add(rpcServiceName);
        services.put(rpcServiceName, serviceConfig.getService());
        log.info("Added service:{} and Interface:{}", rpcServiceName, serviceConfig.getService().getClass().getInterfaces());
    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = services.get(rpcServiceName);
        if (service == null){

        }
        return null;
    }

    @Override
    public void publishService(RpcServiceConfig serviceConfig) {

    }
}
