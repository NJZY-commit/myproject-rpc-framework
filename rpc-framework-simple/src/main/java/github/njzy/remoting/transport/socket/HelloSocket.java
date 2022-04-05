package github.njzy.remoting.transport.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 本类是Socket网络编程测试案例的服务端
 *
 * @author njzy
 * @package github.njzy.remoting.transport.socket
 * @create 2022年03月16日 14:10
 */
public class HelloSocket {

    private static final Logger logger = LoggerFactory.getLogger(HelloSocket.class);

    /**
     * 知识讲解：
     *    Socket -- 是一个ip地址与端口号的集合，相当于(ip:port) = socket
     *
     * @param port 此处的port代表着socket里的port
     */
    public void start(int port){
        // 1.创建一个Socket并绑定一个接口
        try (ServerSocket serverSocket = new ServerSocket(port);){
            Socket socket;
            // 2. 通过 accept()监听客户端请求
            while ((socket = serverSocket.accept()) != null){
                logger.info("client connected");
                try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                     ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());){
                    // 3. 通过输入流读取客户端发送的请求信息
                    //Message message = (Message) inputStream.readObject();
                    //logger.info("server receive message:"  + );

                    // 4. 通过输出流向客户端发送响应信息


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
