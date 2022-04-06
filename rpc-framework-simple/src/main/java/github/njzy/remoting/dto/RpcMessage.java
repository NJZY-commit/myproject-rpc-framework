package github.njzy.remoting.dto;

import lombok.*;

/**
 * 用来保存rpc发送的消息
 *
 * @author njzy
 * @package github.njzy.remoting.dto
 * @create 2022年04月06日 17:40
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class RpcMessage {

    /**
     * rpc message type  rpc 消息类型
     */
    private byte messageType;
    /**
     * serialization type 序列化类型
     */
    private byte codec;
    /**
     * compress type 压缩类型
     */
    private byte compress;
    /**
     * request id  请求编号
     */
    private int requestId;
    /**
     * request data 请求数据
     */
    private Object data;


}
