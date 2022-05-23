package github.njzy.bean;

import org.springframework.beans.BeansException;

/**
 * @author njzy
 * @package github.njzy.bean
 * @create 2022年04月29日 15:18
 */
@FunctionalInterface
public interface ObjectFactory<T> {

    T getObject() throws BeansException;

}
