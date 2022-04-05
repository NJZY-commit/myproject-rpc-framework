package github.njzy.extension;

/**
 * 持有者类，用来存储从缓存中获得的对象
 *
 * @author njzy
 * @package github.njzy.extension
 * @create 2022年03月17日 13:35
 */
public class Holder<T> {

    private volatile T value;  // 任意类的对象，value，添加了volatile关键字，使得该值可被所有线程看见，还能防止指令重排序

    // 返回value这个成员变量
    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
