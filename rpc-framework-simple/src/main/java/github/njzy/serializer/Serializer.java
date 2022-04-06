package github.njzy.serializer;

import github.njzy.extension.SPI;

/**
 * 序列胡工具的顶级父类，想要创建序列化器，就都要实现这个父接口
 *
 * @author njzy
 * @package github.njzy.serializer
 * @create 2022年04月06日 19:06
 */
@SPI
public interface Serializer {

    /**
     * 序列化
     *
     * @param obj 要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes 序列化后的字节数组
     * @param clazz 目标类
     * @param <T>   类的类型。举个例子,  {@code String.class} 的类型是 {@code Class<String>}.
     *              如果不知道类的类型的话，使用 {@code Class<?>}
     * @return 反序列化的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
