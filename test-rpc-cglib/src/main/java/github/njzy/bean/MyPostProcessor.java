package github.njzy.bean;

import github.njzy.bean.annotation.Component;
import org.springframework.beans.BeansException;

import javax.annotation.Nullable;

/**
 * @author njzy
 * @package github.njzy.bean
 * @create 2022年04月29日 10:37
 */
@Component
public class MyPostProcessor implements BeanPostProcessor{

    @Nullable
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("这里是postProcessBeforeInitialization");
        return null;
    }

    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("这里是postProcessAfterInitialization");
        return null;
    }
}
