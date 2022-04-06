package github.njzy.registry.zk.util;

import github.njzy.enums.RpcConfigEnum;
import github.njzy.utils.PropertyFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.checkerframework.checker.units.qual.K;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
        // todo:如果用户在配置文件中已经配置了zk地址，那么就直接读取配置文件中的内容，创建客户端对象
        // 第一步：先要创建一个读取配置文件的工具类
        Properties properties = PropertyFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        String ZookeeperAddress = properties != null && properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) != null ?
                properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) : DEFAULT_ZOOKEEPER_ADDRESS;

        // todo: 判断ZkClient是否已经启动，如果启动就直接返回，否则就创建一个客户端并启动
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) return zkClient;

        // todo: 创建一个zookeeper客户端
        // 先制定重试策略
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        // 创建客户端对象，设置两个参数（连接地址和重试策略）
        zkClient = CuratorFrameworkFactory.builder().connectString(ZookeeperAddress).retryPolicy(retry).build();
        zkClient.start(); // 启动服务

        // todo: 判断如果zookeeper连接超过30秒还没连接上，就报出异常
        try {
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)){
                throw new RuntimeException("连接超时，正在等待连接zookeeper");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return zkClient;
    }




}
