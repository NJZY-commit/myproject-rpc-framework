package github.njzy.bean.session;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author njzy
 * @package github.njzy.bean.session
 * @create 2022年04月29日 15:24
 */
public class MySession {

    public static Object dealSql(Class clazz){
        Class[] classes = new Class[]{clazz};
        return Proxy.newProxyInstance(MySession.class.getClassLoader(),classes, (InvocationHandler) new MyInvocationHandler());
    }

}
