package github.njzy.remoting.dto;

import github.njzy.rpcenum.ResponseEnum;
import lombok.*;

import java.io.Serializable;

/**
 * 服务端响应实体类
 *
 * @author njzy
 * @package github.njzy.remoting.dto
 * @create 2022年03月16日 16:19
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder // 创建一个名叫"builder"的切面
@ToString
@Setter
public class RpcResponse<T> implements Serializable {


    private static final long serialVersionUID = -1069707456665808851L;
    /**
     * 请求Id
     */
    private String requestId;

    /**
     * 返回的状态码
     */
    private Integer code;

    /**
     * 返回的信息
     */
    private String message;

    /**
     * 返回响应的数据
     */
    private T data;

    /**
     * 远程调用方法成功
     *
     * @param data
     * @param requestId
     * @param <T>
     * @return
     */
    public static <T> RpcResponse<T> success(T data, String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(ResponseEnum.SUCESS.getCode());
        response.setMessage(ResponseEnum.SUCESS.getMessage());
        response.setRequestId(requestId);
        if (null != data){
            response.setData(data);
        }

        return response;
    }

    /**
     * 远程调用方法失败
     *
     *
     * @param responseEnum
     * @param <T>
     * @return
     */
    public static <T> RpcResponse<T> fail(ResponseEnum responseEnum){
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(responseEnum.getCode());
        response.setMessage(responseEnum.getMessage());
        return response;
    }

}
