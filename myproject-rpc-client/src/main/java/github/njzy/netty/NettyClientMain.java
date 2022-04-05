package github.njzy.netty;

import github.njzy.annotation.RpcScan;
import github.njzy.controller.ProduceController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 本类是主类，主要用于发送对象到服务器
 *
 * @author njzy
 * @package github.njzy.netty
 * @create 2022年04月02日 10:52
 */
@RpcScan(basPpackage = {"github.njzy"})
public class NettyClientMain {

    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext acac = new AnnotationConfigApplicationContext(NettyClientMain.class);
        ProduceController bean = (ProduceController) acac.getBean("ProduceController");
        bean.getProduces();
    }

}
