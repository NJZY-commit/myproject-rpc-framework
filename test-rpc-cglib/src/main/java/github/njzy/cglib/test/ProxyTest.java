package github.njzy.cglib.test;

import github.njzy.cglib.entity.RealHello;
import github.njzy.cglib.proxy.JdkProxy;
import org.junit.platform.commons.util.ClassLoaderUtils;


/**
 * @author njzy
 * @package github.njzy.cglib.test
 * @create 2022年04月12日 16:06
 */
public class ProxyTest {

    public static void main(String[] args) {

        // 构建代理器
        JdkProxy jdkProxy = new JdkProxy(new RealHello());
        //ClassLoaderUtils.getCurrentClassLoader();

    }

}
