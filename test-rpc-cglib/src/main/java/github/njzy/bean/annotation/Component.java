package github.njzy.bean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义一个Component注解来深入理解Spring的包扫描流程
 *
 * @author njzy
 * @package github.njzy.bean.annotation
 * @create 2022年04月20日 10:39
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {

    String value() default "";

}
