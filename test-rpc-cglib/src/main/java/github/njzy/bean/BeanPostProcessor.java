package github.njzy.bean;

import org.springframework.beans.BeansException;

import javax.annotation.Nullable;

/**
 * @author njzy
 * @package github.njzy.bean
 * @create 2022年04月29日 10:29
 */
public interface BeanPostProcessor {

    @Nullable
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Nullable
    default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
