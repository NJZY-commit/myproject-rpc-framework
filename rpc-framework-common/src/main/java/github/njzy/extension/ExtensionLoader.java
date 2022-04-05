package github.njzy.extension;


import github.njzy.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 扩展类加载器的实现
 *
 * @author njzy
 * @package github.njzy.extension
 * @create 2022年03月17日 13:28
 */
@Slf4j
public final class ExtensionLoader<T> {

    // 扩展目录的地址
    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";
    // Map集合，用来专门存储扩展类加载器
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    // Map集合，用来专门存储扩展类实例对象
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    // 类，通过这个类来获取类加载器
    private final Class<?> type;
    // 创建一个ConcurrentHashMap对象，因为线程是安全的
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    // 创建一个Holder集合，用来储存缓存类
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    // 获取扩展类的类加载器
    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        // 如果type为空
        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface.");
        }
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        // firstly get from cache, if not hit, create one
        // 从类加载器的集合中获取指定类型的类加载器
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        // 如果类加载器是空的
        if (extensionLoader == null) {
            // 创建一个扩展类加载器放入EXTENSION_LOADERS中去
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            // 再从EXTENSION_LOADERS中获取对应类的加载器
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        // 返回类加载器
        return extensionLoader;
    }

    public T getExtension(String name) {
        // 1. 利用字符串工具类判断参数是否为空
        if (StringUtil.isBlank(name)) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }

        // firstly get from cache, if not hit, create one
        // 首先从缓存中获取，如果没有获取到，就创建一个
        Holder<Object> holder = cachedInstances.get(name); // 从缓存中获取key值与name同名的值
        // 判断这个对象是否为空
        // 如果对象是空的
        if (holder == null) {
            // 就创建一个Holder对象与name关联起来
            cachedInstances.putIfAbsent(name, new Holder<>());
            // 然后利用name去取对应的value值
            holder = cachedInstances.get(name);
        }
        // create a singleton if no instance exists
        // 走到这里，就说明holder不为空
        Object instance = holder.get(); // 获取holder对象中存储的对象
        // 如果实例对象不存在，就创建一个单例对象
        if (instance == null) {
            // 单例模式
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    // 创建一个Extension对象
                    instance = createExtension(name);
                    // 把这个对象存入到holder中去
                    holder.set(instance);
                }
            }
        }
        // 返回这个对象
        return (T) instance;
    }

    /**
     * 创建一个Extension对象
     *
     * @param name 名称
     * @return 对象
     */
    private T createExtension(String name) {
        // load all extension classes of type T from file and get specific one by name
        // 从文件中加载所有类型为 T 的扩展类并按名称获取特定的扩展类
        Class<?> clazz = getExtensionClasses().get(name); // 从扩展类中获取同name的value
        // 如果获取到的value对象是空的
        if (clazz == null) {
            // 就抛出一个运行时异常"没有这个名字的拓展类"
            throw new RuntimeException("No such extension of name " + name);
        }
        // 代码运行到这儿，就证明扩展类里的对象不是空的
        // 将这个实例对象从EXTENSION_INSTANCES中取出来
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        // 如果这个实例是空的
        if (instance == null) {
            try {
                // 就创建一个对象，存放进EXTENSION_INSTANCES对应的clazz键的值中
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                // 再根据clazz这个键获取关联的值
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        // 返回这个实例对象
        return instance;
    }

    /**
     * 获取ExtensionClass对象
     *
     * @return
     */
    private Map<String, Class<?>> getExtensionClasses() {
        // get the loaded extension class from the cache
        // 从缓存类中获取value值
        Map<String, Class<?>> classes = cachedClasses.get();
        // double check
        // 双重校验
        // 如果这个集合对象是空的
        if (classes == null) {
            // 就创建单例对象
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = new HashMap<>();
                    // load all extensions from our extensions directory
                    // 从我们的扩展目录加载所有扩展
                    loadDirectory(classes);
                    // 把扩展放入缓存类集合中去
                    cachedClasses.set(classes);
                }
            }
        }
        // 返回集合
        return classes;
    }

    /**
     * 从扩展目录中加载扩展对象
     *
     * @param extensionClasses
     */
    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        // 初始化文件名称
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        try {
            // Enumeration接口中定义了一些方法，通过这些方法可以枚举（一次获得一个）对象集合中的元素。
            Enumeration<URL> urls;
            // 获取扩展类的类加载器
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            // 类加载器对象获取扩展目录下的资源
            urls = classLoader.getResources(fileName);
            // 如果资源不为空
            if (urls != null) {
                // 就遍历这个资源
                while (urls.hasMoreElements()) {
                    // 获取该资源中的内容
                    URL resourceUrl = urls.nextElement();
                    // 加载资源
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 加载资源
     *
     * @param extensionClasses 扩展结合类
     * @param classLoader  类加载器
     * @param resourceUrl  资源地址
     */
    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        // 创建一个输入流
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {
            // 初始化行
            String line;
            // read every line
            // 读取每一行
            while ((line = reader.readLine()) != null) { // 如果每一行不为空
                // get index of comment
                // 获取#字符的初次下标值
                final int ci = line.indexOf('#');
                // 如果获取到了
                if (ci >= 0) {
                    // string after # is comment so we ignore it
                    // 就截取从下标0到该下标的内容
                    line = line.substring(0, ci);
                }
                // 将这段内容的首尾空白去掉
                line = line.trim();
                // 判断该行的内容长度，如果内容长度大于0
                if (line.length() > 0) {
                    try {
                        // 返回字符值为"="的下标值
                        final int ei = line.indexOf('=');
                        // 截取该内容并去掉空格作为name
                        String name = line.substring(0, ei).trim();
                        // 截取=号后面的内容作为类名
                        String clazzName = line.substring(ei + 1).trim();
                        // our SPI use key-value pair so both of them must not be empty
                        // 只用键值对来保存SPI，所以键值不可为空
                        if (name.length() > 0 && clazzName.length() > 0) {
                            // 用类加载器加载这个类名的类
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            // 并把这个类和类名放入扩展类的集合中
                            extensionClasses.put(name, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }

            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
