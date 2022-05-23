package github.njzy.utils;

import java.util.Collection;

/**
 * @author njzy
 * @package github.njzy.utils
 * @create 2022年05月23日 23:28
 */
public class CollectionUtil {

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }
}
