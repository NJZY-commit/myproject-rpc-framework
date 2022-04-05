package github.njzy.registry.zk.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.checkerframework.checker.units.qual.K;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  Zookeeper提供的一套开源的客户端框架，主要用来进行增删改查的操作
 *
 *
 * @author njzy
 * @package github.njzy.registry.zk.util
 * @create 2022年04月05日 10:44
 */
@Slf4j
public class CuratorUtils {

    /* 定义一系列的zookeeper属性 */
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    public static final String  ZK_REGISTER_ROOT_PATH = "/project-rpc";
    // 格式： key--代表着服务器名，value--地址值（因为地址值不止一个）
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>(); //存放服务器注册地址
    private static final Set<String> REGISTERED_SERVICE_PATH = ConcurrentHashMap.newKeySet(); // 获取已经注册过的地址
    private static CuratorFramework zkClient;
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "192.168.64.150:2181";


    /**
     * 删除服务器 --> 在底层逻辑里，就是清除注册表中的注册信息
     */
    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress socketAddress){
        REGISTERED_SERVICE_PATH.stream().parallel().forEach(str -> {
            try {
                if (str.endsWith(socketAddress.toString())){
                    zkClient.delete().forPath(str); // 清除注册表中的对应信息
                }
            } catch (Exception e) {
                //e.printStackTrace();
                log.info("clear registry [{}] faild", str);
            }
        });

        log.info("clear all registry information [{}] succeed", REGISTERED_SERVICE_PATH.toString());
    }

    /**
     * 获取zk客户端的方法
     */
    public static CuratorFramework getZkClient() {
        // todo:通过读取配置文件来创建客户端对象
        
    }




}
