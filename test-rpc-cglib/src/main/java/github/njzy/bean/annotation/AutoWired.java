package github.njzy.bean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义的Autowired标签，主要用于依赖注入
 *
 * @author njzy
 * @package github.njzy.bean.annotation
 * @create 2022年04月20日 14:31
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoWired {
    String value() default "";
}
