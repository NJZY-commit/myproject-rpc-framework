package github.njzy.bean;

import cn.hutool.core.lang.ClassScanner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import github.njzy.bean.annotation.AutoWired;
import github.njzy.bean.annotation.Component;
import github.njzy.bean.annotation.ComponentScan;
import github.njzy.bean.annotation.Scope;
import github.njzy.bean.entity.BeanDefination;

import java.lang.reflect.Field;
import java.util.*;

/**
 *
 *
 * @author njzy
 * @package github.njzy.bean
 * @create 2022年04月20日 10:30
 */
public class MyApplicationContext {

    private Class configClass;
    private Set<Class<?>> classes;
    private Map<String, Object> singletonMap = Maps.newHashMap();
    private Map<String, BeanDefination> beanDefinationMap = new HashMap<>();
    private static  List<BeanPostProcessor> processorList = Lists.newArrayList();
    // 定义一个半成品池
    private final Map<String, Object> earlySingletonObjects = Maps.newHashMap();

    public MyApplicationContext(Class configClass) {
        this.configClass = configClass;

        /* 解析配置类 */
        ComponentScan scan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
        String path = scan.value();
        //System.out.println(path);

        this.classes = ClassScanner.scanPackage(path); // 使用hutool扫描包路径

        /* 从classes集合中过滤留下带有Component注解的类
         *  并把实例化的对象存入到集合中去*/
        classes.stream()
                .filter(clazz -> clazz.getAnnotation(Component.class) != null)
                .forEach(c -> {
                    loadBeanDefinition(c,configClass); // 加载bean定义

                    /*解析后置处理器*/
                    if (BeanPostProcessor.class.isAssignableFrom(c)) {
                        try {

                            BeanPostProcessor postProcessor = (BeanPostProcessor) c.newInstance();
                            processorList.add(postProcessor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void loadBeanDefinition(Class<?> c, Class configClass) {

        BeanDefination beanDefination = new BeanDefination();
        beanDefination.setClazz(c);
        Scope scope = (Scope) configClass.getAnnotation(c);
        beanDefination.setScope(scope == null ? "singleton" : scope.value());
        Component component = c.getAnnotation(Component.class);
        beanDefinationMap.put(component.value(), beanDefination);
    }

    public Object getBean(String beanName) throws Exception {
        BeanDefination beanDefination = beanDefinationMap.get(beanName);
        if (beanDefination.getScope().equals("singleton")){
            Object obj = singletonMap.get(beanName);
            if (obj == null){
                obj = earlySingletonObjects.get(beanName);
                if (obj == null) {
                    this.earlySingletonObjects.put(beanName, beanDefination.getClazz().newInstance());
                    obj = createBean(beanDefination.getClazz(), beanDefination.getClazz().getTypeName());
                    singletonMap.put(beanName, obj);
                }
            }
            return obj;
        }else {
            return createBean(beanDefination.getClazz(), beanDefination.getClazz().getTypeName());
        }

    }

    /**
     * 依赖注入
     * @param clazz 类
     * @return 对象
     * @throws Exception
     */
    public Object createBean(Class clazz, String beanName) throws Exception {
        Object obj = clazz.newInstance(); // 实例化类
        Field[] fields = clazz.getDeclaredFields(); // 利用反射技术获取声明的属性
        Arrays.stream(fields)
                .filter(filed -> filed.isAnnotationPresent(AutoWired.class))
                .forEach(filed -> {
                    try {
                        filed.setAccessible(true);
                        filed.set(obj, getBean(filed.getName()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        processorList.stream().forEach(p -> p.postProcessBeforeInitialization(obj,beanName));

        /*实现具体的业务逻辑*/
        if (obj instanceof InitializingBean){
            ((InitializingBean) obj).afterPropertiesSet();
        }

        processorList.stream().forEach(p -> p.postProcessAfterInitialization(obj,beanName));

        return obj;
    }

}
