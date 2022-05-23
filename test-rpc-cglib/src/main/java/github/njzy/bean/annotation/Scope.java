package github.njzy.bean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义Scope注解，用来深度理解Spring对象实例化
 *
 * @author njzy
 * @package github.njzy.bean.annotation
 * @create 2022年04月20日 11:07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Scope {
    String value() default "singleton";
}
