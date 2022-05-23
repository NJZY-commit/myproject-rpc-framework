package github.njzy.bean;

import github.njzy.bean.annotation.Component;
import org.springframework.beans.BeansException;

/**
 * @author njzy
 * @package github.njzy.bean
 * @create 2022年04月29日 15:19
 */
@Component
public class MapperFactory implements ObjectFactory{

    private Class clazz;

    public MapperFactory(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object getObject() throws BeansException {
        if (this.clazz.getName().endsWith("Mapper")){
            return
        }
        return null;
    }

}
