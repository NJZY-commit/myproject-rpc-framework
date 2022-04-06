package github.njzy.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author njzy
 * @package github.njzy.utils
 * @create 2022年04月06日 9:02
 */
@Slf4j
public class PropertyFileUtil {

    // todo: 创建一个无参的构造函数，方便实例化对象
    public PropertyFileUtil() {
    }


    // todo: 创建一个读取配置文件的方法
    public static Properties readPropertiesFile(String fileName){
        // 1. 创建URL对象
        URL url = Thread.currentThread().getContextClassLoader().getResource("");

        // 2. 判断url对象是否为空，不为空则拼接文件路径
        String rpcConfig = "";
        if (url != null){
            rpcConfig = url.getPath() + fileName;
        }

        // 3. 初始化Properties对象
        Properties properties = null;

        // 4. 读取配置文件
        // 5. 异常处理
        try ( InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(rpcConfig), StandardCharsets.UTF_8)) {
            properties = new Properties();
            properties.load(inputStreamReader);
        }catch (IOException e) {
            //e.printStackTrace();
            log.info("occur exception when read properties file [{}]", fileName);
        }

        // 6. 返回结果
        return properties;
    }
}
