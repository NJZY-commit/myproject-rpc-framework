package github.njzy.bean;

/**
 *
 *
 * @author njzy
 * @package github.njzy.bean
 * @create 2022年04月28日 14:48
 */
public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
