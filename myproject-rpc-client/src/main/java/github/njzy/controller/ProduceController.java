package github.njzy.controller;

import github.njzy.annotation.RpcReference;
import github.njzy.annotation.RpcService;
import github.njzy.obj.entity.Produce;
import github.njzy.obj.service.IProduceService;
import io.protostuff.Rpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author njzy
 * @package github.njzy.controller
 * @create 2022年04月02日 10:40
 */
@Component
public class ProduceController {

    @RpcReference(version = "version1", group = "group_produce")
    private IProduceService produceService;

    public void getProduces() throws InterruptedException {
        String produce = this.produceService.getProduceList(new Produce("iphone", 9900));
        assert null != produce;
        // 模拟发送线程的间隔停顿时间
        Thread.sleep(2000);
        for (int i = 0; i < 5; i++) {
            System.out.println(produce);
        }
    }

}
