package github.njzy.spring;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 自定义包扫描仪器
 *
 * @author njzy
 * @package github.njzy.spring
 * @create 2022年03月30日 16:31
 */
public class CustomScan extends ClassPathBeanDefinitionScanner {

    // 第1步：生成构造器
    public CustomScan(BeanDefinitionRegistry registry, Class<? extends Annotation> annoType) {
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annoType)); // 把包含类型过滤器加入到包含列表尾端
    }


    //public CustomScan(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
    //    super(registry, useDefaultFilters);
    //}

    // 第2步：重写doScan方法
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }
}
