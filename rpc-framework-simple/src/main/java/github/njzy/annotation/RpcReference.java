package github.njzy.annotation;

import java.lang.annotation.*;

/**
 * 该注释类是用在引入的接口属性上的
 *
 * @author njzy
 * @package github.njzy.annotation
 * @create 2022年04月02日 10:44
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface RpcReference {

    String version() default "";

    String group() default "";
}
