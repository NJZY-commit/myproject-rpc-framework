package github.njzy.remoting.transport.netty.codec;

import github.njzy.compress.Compress;
import github.njzy.enums.CompressTypeEnum;
import github.njzy.enums.SerializationTypeEnum;
import github.njzy.extension.ExtensionLoader;
import github.njzy.remoting.constants.RpcConstants;
import github.njzy.remoting.dto.RpcMessage;
import github.njzy.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义rpc编码器
 *
 * @author njzy
 * @package github.njzy.remoting.transport.netty.codec
 * @create 2022年04月06日 16:34
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) {
        try {
            out.writeBytes(RpcConstants.MAGIC_NUMBER); // 魔法数，用来认证rpc信息
            out.writeByte(RpcConstants.VERSION); // 版本号
            // leave a place to write the value of full length 留个地方写消息长度的值
            out.writerIndex(out.writerIndex() + 4); // 设置此缓冲区的 writerIndex
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType); // 设置消息类型
            out.writeByte(rpcMessage.getCodec());  // 设置序列化类型
            out.writeByte(CompressTypeEnum.GZIP.getCode()); // 设置压缩类型
            out.writeInt(ATOMIC_INTEGER.getAndIncrement()); // 用原子的方式+1
            // build full length 构建消息长度
            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            // if messageType is not heartbeat message,fullLength = head length + body length
            // 如果message Type不是心跳消息, 消息长度 = 头部长度 + 内容长度
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                    && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                // serialize the object 序列化对象
                String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());  // 获取序列化编码
                log.info("codec name: [{}] ", codecName);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                        .getExtension(codecName); // 创建序列化器
                bodyBytes = serializer.serialize(rpcMessage.getData());  // 序列化对象信息
                // compress the bytes 压缩字节码
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress()); // 获取压缩名
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class) // 创建文件压缩器
                        .getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes); // 压缩文件
                fullLength += bodyBytes.length; // 计算消息长度
            }

            if (bodyBytes != null) {
                out.writeBytes(bodyBytes); // 设置压缩后的对象信息
            }

            // 如果message Type是心跳消息
            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1); //
            out.writeInt(fullLength); // 设置消息长度
            out.writerIndex(writeIndex); // 设置消息类型
        } catch (Exception e) {
            log.error("Encode request error!", e);
        }

    }


}
