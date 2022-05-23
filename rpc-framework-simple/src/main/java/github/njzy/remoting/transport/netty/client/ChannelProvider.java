package github.njzy.remoting.transport.netty.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author njzy
 * @package github.njzy.remoting.transport.netty.client
 * @create 2022年05月23日 23:09
 */
@Slf4j
public class ChannelProvider {

    private final Map<String, Channel> channelMap; // 创建存储结构为map

    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
    } // 利用ConcurrentHashMap这个线程的Map

    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString(); // 把套接字地址地址作为key,（套接字主机名 + 套接字ip地址 + 套接字端口号）
        // determine if there is a connection for the corresponding address 判断对应地址是否有连接
        if (channelMap.containsKey(key)) { // 判断集合当中是否已经存在了相同的key
            Channel channel = channelMap.get(key); // 如果有，就获取这个key对应的通道
            // if so, determine if the connection is available, and if so, get it directly
            if (channel != null && channel.isActive()) { // 如果通道存在且已经连接
                return channel; // 返回这个通道
            } else {
                channelMap.remove(key); // 否则把这个没用的key删除
            }
        }
        return null; // 如果不存在，就返回null
    }

    /**
     * 存储 Channel 对象
     *
     * @param inetSocketAddress 套接字地址
     * @param channel 通道
     */
    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        channelMap.put(key, channel);
    }

    /**
     * 删除channel对象
     *
     * @param inetSocketAddress  套接字地址
     */
    public void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key); // 删除操作
        log.info("Channel map size :[{}]", channelMap.size()); // 显示容量，以表删除
    }
}
