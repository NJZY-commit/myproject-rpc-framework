package github.njzy.test.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 *
 * @author njzy
 * @package github.njzy.test.util
 * @create 2022年05月11日 15:19
 */
public class PropertiesLoader {

    private static final transient Logger log = LoggerFactory.getLogger(PropertiesLoader.class);

    private static final String CON_NAME = "testProperty.properties";
    private static String username;
    private static String password;
    private static String path;


    static Properties properties = new Properties();

    static {
        properties = getProperties(CON_NAME);
        username = properties.getProperty("properties.username");
        password = properties.getProperty("properties.password");
        path = properties.getProperty("properties.path");
    }

    private static Properties getProperties(String fileName){
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
            properties.load(inputStreamReader);
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        return properties;
    }

    public static void main(String[] args) {
        PropertiesLoader loader = new PropertiesLoader();
        System.out.println("username=" + loader.username);
        System.out.println("password=" + loader.password);
        System.out.println("path=" + loader.path);
    }

}
