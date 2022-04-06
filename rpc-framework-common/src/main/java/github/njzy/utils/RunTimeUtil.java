package github.njzy.utils;

/**
 * 用来获取cpu的核心数，以便于计算默认的处理线程
 *
 * @author njzy
 * @package github.njzy.utils
 * @create 2022年04月06日 15:37
 */
public class RunTimeUtil {

    /**
     * 获取cpu的线程数量
     */
    public static int getCpuThreadCount(){
        return Runtime.getRuntime().availableProcessors();
    }

}
