package github.njzy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * rpc框架配置参数的枚举类
 *      主要保存 配置文件的路径
 *
 * @author njzy
 * @package github.njzy.enums
 * @create 2022年04月06日 12:17
 */
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    RPC_CONFIG_PATH("rpcConfig.properties"),
    ZK_ADDRESS("rpc.zookeeper.address")
    ;

    private String propertyValue;
}
