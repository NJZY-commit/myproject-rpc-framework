package github.njzy.annotation;

import java.lang.annotation.*;

/**
 * 该注释类主要用在Service层上，用于识别该接口
 *
 * @author njzy
 * @package github.njzy.annotation
 * @create 2022年03月31日 11:27
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcService {

    /**
     * 标识服务版本
     * @return
     */
    String version() default "";

    /**
     * 标识服务组名
     * @return
     */
    String group() default "";

}
