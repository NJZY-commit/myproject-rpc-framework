package github.njzy.test;

import github.njzy.config.TestBeanPostProcessorConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author njzy
 * @package github.njzy.test
 * @create 2022年04月09日 16:37
 */
public class TestBeanPostProcessor {

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(TestBeanPostProcessorConfig.class);
    }

}
