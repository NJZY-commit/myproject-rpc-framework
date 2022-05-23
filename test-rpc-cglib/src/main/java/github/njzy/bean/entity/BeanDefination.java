package github.njzy.bean.entity;

import lombok.Data;

/**
 * 用来存放实例化的对象
 *
 * @author njzy
 * @package github.njzy.bean.entity
 * @create 2022年04月20日 11:11
 */
@Data
public class BeanDefination {
    private Class clazz;

    private String scope;

}
