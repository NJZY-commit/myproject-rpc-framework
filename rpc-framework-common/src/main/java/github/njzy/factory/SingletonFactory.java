package github.njzy.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工厂模式：
 *     专门用来创建单例对象
 *
 * @author njzy
 * @package github.njzy.factory
 * @create 2022年04月02日 14:19
 */

public class SingletonFactory {

   private static final Map<String,Object> map = new ConcurrentHashMap<String,Object>(); // 初始化存储容器

    public SingletonFactory() {
    }

    // 创建单例对象
    // public static <T> T getInstance(Class<T> c)
    public static <T> T getInstance(Class<T> c){
            // 判断参数对象是否为null
        if (c == null) throw new IllegalArgumentException();

        // 若参数对象不为null，就当作value存入map中去
        String key = c.toString();
        if (map.containsKey(key)){
            return c.cast(map.get(key));
        }else{
            return c.cast(map.computeIfAbsent(key, k ->{
                try {
                    return c.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    //e.printStackTrace();
                    throw new RuntimeException(e.getMessage(), e);
                }
            }));
        }

    }

}
