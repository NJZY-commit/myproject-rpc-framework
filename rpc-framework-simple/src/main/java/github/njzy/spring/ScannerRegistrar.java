package github.njzy.spring;

import github.njzy.annotation.RpcScan;
import github.njzy.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author njzy
 * @package github.njzy.spring
 * @create 2022年03月30日 15:55
 */
@Slf4j
public class ScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    // 定义统一的属性
    private static final String SPRING_BEAN_BASE_PACKAGE = "github.njzy"; // bean对象的包路径
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage"; // 基础包属性名称
    private ResourceLoader resourceLoader; // 加载资源用的资源加载器

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        //ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);

        // 第一步：获取RpcScan注解的属性和值
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RpcScan.class.getName()));

        // 初始化存储容器
        String[] basePackages = new String[0];
        if (annotationAttributes != null){
            basePackages = annotationAttributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        if (basePackages.length == 0){
            basePackages = new String[]{((StandardAnnotationMetadata)annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }

        // 扫描RpcService注解
        CustomScan rpcServiceScan = new CustomScan(registry, RpcService.class);

        // 扫描组件注释
        CustomScan componentScan = new CustomScan(registry, Component.class);

        // 给两个RpcService设置资源加载器
        if (resourceLoader != null){
            rpcServiceScan.setResourceLoader(resourceLoader);
            componentScan.setResourceLoader(resourceLoader);
        }

        // 接下来开始扫描操作
        int springBeanAmount = componentScan.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("componentScan扫描到的springBean对象数量是:{}",springBeanAmount);
        int rpcServerCount = rpcServiceScan.scan(basePackages);
        log.info("rpcServiceScan扫描到的服务器数量是:{}",rpcServerCount);

    }
}
