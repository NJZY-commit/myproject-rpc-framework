package github.njzy.exception;

import github.njzy.enums.RpcErrorMessageEnum;

/**
 * 自定义异常类
 *
 * @author njzy
 * @package github.njzy.exception
 * @create 2022年04月02日 17:43
 */
public class RpcException extends RuntimeException {

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }

}
