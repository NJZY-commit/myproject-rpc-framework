package github.njzy.remoting.serialize.kyro;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 本类是Kyro的快速开始类
 *
 *
 * @author njzy
 * @package github.njzy.remoting.serialize.kyro
 * @create 2022年03月17日 8:52
 */
public class HelloKryo {

    public static void main(String[] args) {
        // 1. 创建一个Kyro对象
        Kryo kryo = new Kryo();
        kryo.register(Apple.class); // 注册

        Apple apple = new Apple();
        apple.setBrand("红富士");

        Output output = null;
        try {
            output = new Output(new FileOutputStream("file.bin"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 2. 写出对象
        kryo.writeObject(output,apple);
        output.close();

        Input input = null;
        try {
            input = new Input(new FileInputStream("file.bin"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        kryo.readObject(input,Apple.class);
        input.close();




    }

}
