package github.njzy.utils;

/**
 * 字符串工具类，专门用来处理字符串类型数据
 *
 * @author njzy
 * @package github.njzy.utils
 * @create 2022年03月17日 13:33
 */
public class StringUtil {

    // 判断是否为空的方法
    public static boolean isBlank(String s) {
        // 如果参数字符串类型s为空或者参数字符串类型s的长度为0
        if (s == null || s.length() == 0) {
            return true; // 返回true
        }
        // 如果字符串参数s不为空
        // 遍历字符串类型参数s。之所以能够遍历是因为String类型底层是char[]，1.9后变为了byte[]
        for (int i = 0; i < s.length(); ++i) {
            // 判断返回的对应下标的字符是否为空格
            // 如果不是
            if (!Character.isWhitespace(s.charAt(i))) {
                return false; // 返回false
            }
        }
        // 当遍历完后，证明字符串是空格，返回true
        return true;
    }

}
