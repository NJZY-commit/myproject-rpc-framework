package github.njzy.spring;

import github.njzy.annotation.RpcReference;
import github.njzy.annotation.RpcService;
import github.njzy.config.RpcServiceConfig;
import github.njzy.extension.ExtensionLoader;
import github.njzy.factory.SingletonFactory;
import github.njzy.provider.impl.zkServiceProviderImpl;
import github.njzy.provider.zookeeper.ServiceProvider;
import github.njzy.proxy.RpcClientProxy;
import github.njzy.remoting.transport.RpcRequestTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author njzy
 * @package github.njzy.spring
 * @create 2022年04月08日 22:09
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider; // 服务提供端
    private final RpcRequestTransport rpcClient; // 传递消息的类

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(zkServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //System.out.println("postProcessBeforeInitialization执行了");
        // 让带有rpcService注解的服务类，发布
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            // get RpcService annotation
            // 获取服务类
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // build RpcServiceProperties
            // 创建服务
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            // 发布服务
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //System.out.println("postProcessAfterInitialization执行了");
        Class<?> targetClass = bean.getClass(); // 通过反射获取bean对象的类型
        Field[] declaredFields = targetClass.getDeclaredFields(); // 获取bean类里面声明的所有属性
        // 循环属性
        for (Field declaredField : declaredFields) {
            // 获取属性上面的注解
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) { // 如果这个变量上面有RpcReference注解
                // 创建一个服务器
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();

                // 实例化动态代理对象
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);

                // 用rpcClientProxy类中的getProxy()去获取代理类
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());

                // 将访问的标识设置为true
                declaredField.setAccessible(true);

                try {
                    declaredField.set(bean, clientProxy); // 设置bean对象和对应的代理类
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return bean;
    }

}
