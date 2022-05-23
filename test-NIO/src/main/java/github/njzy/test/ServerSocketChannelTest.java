package github.njzy.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 本类是用来学习Selector选择器的测试类
 *      运行逻辑介绍：
 *          打开一个Selector，注册一个通道注册到这个Selector上(通道的初始化过程略去), 然后持续监控这个Selector的四种事件（接受，连接，读，写）是否就绪
 *
 *  技术总结：Selector的使用步骤
 *      1. 创建Selector  ---> Selector.open()
 *      2. 将Channel注册到Selector中  --->  channel.configureBlocking(false); | channel.register(selector, ops);
 *      3. 不断重复下面的操作
 *           · 通过Selector选择通道 ---> selector.select();
 *           · 获取selected key ---> selector.selectedKeys();
 *           · 迭代每个selected key
 *                  · 从selected key中获取对应的Channel【如果有的话】
 *                  · 判断那些IO事件已经就绪，处理该事件
 *                  · 根据需要修改selected key的监听事件
 *                  · 将已经处理完的key从selected key中删除
 *
 * @author njzy
 * @package github.njzy.test
 * @create 2022年04月12日 11:07
 */
public class ServerSocketChannelTest {

    private int size = 1024;
    private ServerSocketChannel socketChannel;
    private ByteBuffer byteBuffer;
    private Selector selector;
    private final int port = 8998;
    private int remoteClientNum=0;

    public ServerSocketChannelTest() {
        try {
            initChannel(); // 初始化数据的方法
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * 为什么特意把一些数据放在一个方法中去初始化，是为了增加代码的可读性，方便做统一的异常处理
     *
     * @throws Exception
     */
    public void initChannel() throws Exception {
        socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.bind(new InetSocketAddress(port));
        System.out.println("listener on port:" + port);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        byteBuffer = ByteBuffer.allocateDirect(size);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
    }

    private void listener() throws Exception {
        while (true) { // 就是不断的返回通道
            // select(): 选择已经准备就绪的通道【这个通道是你感兴趣的。比如你要进行读操作，就只会返回给你专门用来读的通道】
            //           该方法还会把获取到的对象存放到selectedKeys中
            int n = selector.select();
            if (n == 0) { // 如果返回值不为0，说明有n个通道已经准备就绪
                continue;
            }
            Iterator<SelectionKey> ite = selector.selectedKeys().iterator(); // 获取这些已经准备就绪的对象
            while (ite.hasNext()) {
                SelectionKey key = ite.next();
                /*检测各个通道的事件*/
                //a connection was accepted by a ServerSocketChannel.
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel(); // 取出SelectionKey关联的channel
                    SocketChannel channel = server.accept(); // 接收和该通道的连接
                    registerChannel(selector, channel, SelectionKey.OP_READ);
                    remoteClientNum++; // 注册成功后，远程客户端数量+1
                    System.out.println("online client num="+remoteClientNum);
                    replyClient(channel); // 客户端返回答复
                }
                //a channel is ready for reading 读操作就绪的通道
                /*isReadable()属于判断ready集合中读操作是否已准备就绪了?*/
                if (key.isReadable()) {
                    readDataFromSocket(key); // 读取数据
                }

                ite.remove();//must 之所以必须要这一步，是因为select()只是单纯的获取，并没有删除selectedKeys中的，而这就需要我们自己手动实现
            }

        }
    }

    protected void readDataFromSocket(SelectionKey key) throws Exception {
        SocketChannel socketChannel = (SocketChannel) key.channel(); // 获取与客户端相连的通道
        int count; // 读取的数据长度
        byteBuffer.clear();
        while ((count = socketChannel.read(byteBuffer)) > 0) {
            byteBuffer.flip(); // Make buffer readable 使byteBuffer内的内容可读
            // Send the data; don't assume it goes all at once
            while (byteBuffer.hasRemaining()) { // 发送数据，但不是一次性全部发送过去
                socketChannel.write(byteBuffer);
            }
            byteBuffer.clear(); // Empty buffer 清空buffer
        }
        // 如果数据已经读完，就关闭通道
        if (count < 0) {
            socketChannel.close();
        }
    }

    /**
     * 客户端回复方法
     *
     * @param channel 通道
     * @throws IOException
     */
    private void replyClient(SocketChannel channel) throws IOException {
        byteBuffer.clear();
        byteBuffer.put("hello client!\r\n".getBytes());
        byteBuffer.flip();
        channel.write(byteBuffer);
    }

    private void registerChannel(Selector selector, SocketChannel channel, int ops) throws Exception {
        if (channel == null) {
            return;
        }

        /*为了将Selector与Channel配合使用，必须将channel注册到selector上，且channel必须是非阻塞模式*/
        channel.configureBlocking(false);
        /**
         * @param ops 此处的参数是一个“interest集合”，意思是你使用selector监听channel中的什么事件？
         *            它可以监听四种不同种类的事件（Connect、Accept、Read、Write），如果你对不止一件事件感兴趣，可以使用 | 来连接
         */
        channel.register(selector, ops);
    }


    public static void main(String[] args) {
        try {
            new ServerSocketChannelTest().listener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
