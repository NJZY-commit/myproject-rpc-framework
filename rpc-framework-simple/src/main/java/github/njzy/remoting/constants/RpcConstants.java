package github.njzy.remoting.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * rpc框架的常用参数类
 *      主要定义一些关于网络传输协议的消息
 *
 * @author njzy
 * @package github.njzy.remoting.constants
 * @create 2022年04月06日 17:44
 */
public class RpcConstants {

    /**
     * Magic number. Verify RpcMessage 幻数，用来认证rpc信息
     */
    public static final byte[] MAGIC_NUMBER = {(byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'};
    /*默认传输字符集*/
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    //version information
    /* 1.版本号；2.消息体长度；3.请求类型；4.回应类型*/
    public static final byte VERSION = 1;
    public static final byte TOTAL_LENGTH = 16;
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
    //ping （控制帧）
    // 心跳请求类型
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    //pong
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4; // 心跳响应类型
    public static final int HEAD_LENGTH = 16; // 头部长度
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024; // 最大帧长


}
