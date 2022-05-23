package github.njzy.registry.zk.util;

import github.njzy.enums.RpcConfigEnum;
import github.njzy.utils.PropertyFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
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
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet(); // 注册路径集
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

    /**
     * 创建持久节点
     *
     * Create persistent nodes. Unlike temporary nodes, persistent nodes are not removed when the client disconnects
     *                  创建持久节点。与临时节点不同，持久节点在客户端断开连接时不会被移除
     *
     * @param zkClient   zookeeper客户端
     * @param path node path  节点路径
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            /*如果，注册路径集中包含作为参数传入的路径，或者测试检查参数路径的节点存在*/
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                // 在日志中显示“节点已经存在，这个节点是...”
                log.info("The node already exists. The node is:[{}]", path);
            } else {
                //eg: /my-rpc/github.javaguide.HelloService/127.0.0.1:9999
                // 否则，如果该节点不存在
                // 创建一个持久节点
                // 创建节点，并递归创建父结点。creatingParentsIfNeeded这个方法的好处在于当没有父结点时自动创建父结点
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                // 在日志中记录“节点创建成功，该节点是...”
                log.info("The node was created successfully. The node is:[{}]", path);
            }
            // 在注册路径集中加入这个节点路径
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail", path);
        }
    }

    /**
     * 获取子节点
     *
     * Gets the children under a node  获取节点下的子节点
     *
     * @param zkClient   zookeeper客户端
     * @param rpcServiceName rpc service name  rpc 服务名称
     * @return All child nodes under the specified node 指定节点下的所有子节点
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) { // 如果服务地址集合的key中已经包含了传入的rpc服务名称
            return SERVICE_ADDRESS_MAP.get(rpcServiceName); // 返回这个rpc服务名称对应的子节点集合
        }

        // 初始化子节点集合
        List<String> result = null;
        // 拼接Zookeeper注册跟节点路径 + / + rpc 服务名称
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        try {
            // 获取servicePath下的所有子节点 (核心操作)
            result = zkClient.getChildren().forPath(servicePath);
            // 把得到的内容以及key一起存入Map中
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            registerWatcher(rpcServiceName, zkClient); // 注册监听指定节点的变化
        } catch (Exception e) {
            // 如果异常，就提示“获取子节点路径失败”
            log.error("get children nodes for path [{}] fail", servicePath);
        }
        return result; // 返回子节点集合
    }

    /**
     * 注册监视
     *
     * Registers to listen for changes to the specified node 注册监听指定节点的变化
     *
     * @param rpcServiceName
     */
    private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName; // 初始化服务路径
        /* 监听子节点的变化情况 */
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);  // 创建一个路径子缓存对象
        // 创建监听器：当发生改变时就会调用
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            /* 此处用来写你自己的代码逻辑 */
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath); // 获取服务路径下的所有子节点中的内容
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses); // 更新获取到的内容
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener); // 在子路径缓存对象中安装监听器
        pathChildrenCache.start(); // 启动监听器
    }

}
