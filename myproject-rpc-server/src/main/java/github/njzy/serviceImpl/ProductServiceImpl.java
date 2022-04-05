package github.njzy.serviceImpl;

import github.njzy.annotation.RpcService;
import github.njzy.obj.entity.Produce;
import github.njzy.obj.service.IProduceService;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务层实现类，主要是当服务器接收到参数后，获取参数对象的值，并返回一句话 “产品已经获取成功!”
 *
 * @author njzy
 * @package github.njzy.serviceImpl
 * @create 2022年04月02日 11:02
 */
@Slf4j
@RpcService(version = "version1", group = "example1")
public class ProductServiceImpl implements IProduceService {

    static {
        System.out.println("ProductServiceImpl被注册");
    }

    @Override
    public String getProduceList(Produce produce) {
        log.info("产品的名称与价格是：{}", produce.getProdName() + "," + produce.getProdPrice());
        String result = "产品已经获取成功";
        return result;
    }
}
