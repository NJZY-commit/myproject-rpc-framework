package github.njzy.config;

import lombok.*;

/**
 * rpc服务配置类
 *     用在服务注册上
 *
 * @author njzy
 * @package github.njzy.config
 * @create 2022年04月02日 15:02
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RpcServiceConfig {

    private String version = "";
    private String group = "";
    private Object service;

    public String getRpcServiceName() {
        return this.getVersion() + this.getGroup() + this.getService();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

}
