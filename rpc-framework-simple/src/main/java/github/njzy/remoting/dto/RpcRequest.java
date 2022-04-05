package github.njzy.remoting.dto;


import lombok.*;

import java.io.Serializable;

/**
 * 客户端请求实体类
 *
 * @author njzy
 * @package github.njzy.remoting.dto
 * @create 2022年03月16日 16:14
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder // 创建一个名叫"builder"的切面
@ToString
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 7386916051230694617L;

    private String requestId; //
    private String interfaceName; // 要调用的接口的名称
    private String methodName; // 要调用的目标方法
    private Object[] parameters; // 要调用的目标方法所需要的参数
    private Class<?>[] paramTypes; // 要调用的类
    private String version; // 代表服务版本，主要是为后续不兼容升级提供可能
    private String group; // 主要用于处理一个接口有多个类实现的情况

    public String getRpcServiceName(){
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }



}
