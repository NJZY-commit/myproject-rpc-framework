package github.njzy.annotation;

import github.njzy.spring.ScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 该注释类适用于包扫描，在Main类上的
 *
 *
 * @author njzy
 * @package github.njzy.annotation
 * @create 2022年03月30日 16:13
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Import(ScannerRegistrar.class )
public @interface RpcScan {

    String[] basPpackage();

}
