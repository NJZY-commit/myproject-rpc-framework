package github.njzy.cglib.proxy;

import github.njzy.cglib.entity.RealHello;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

/**
 * 代理对象
 *
 * @author njzy
 * @package github.njzy.cglib.proxy
 * @create 2022年04月12日 16:02
 */
public class JdkProxy implements InvocationHandler {

    private Object target;

    public JdkProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] objects) throws Throwable {
        return ((RealHello)target).invoke();
    }


}
