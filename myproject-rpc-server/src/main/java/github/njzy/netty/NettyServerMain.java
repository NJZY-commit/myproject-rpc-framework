package github.njzy.netty;

import github.njzy.serviceImpl.ProductServiceImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 该类是服务层的主类，主要用于执行实现类中定义好的逻辑
 *
 * @author njzy
 * @package github.njzy.netty
 * @create 2022年04月02日 14:05
 */
public class NettyServerMain {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext acac = new AnnotationConfigApplicationContext(NettyServerMain.class);
        // 注册服务器


    }

}
