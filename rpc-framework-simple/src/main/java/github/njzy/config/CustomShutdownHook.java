package github.njzy.config;

import lombok.extern.slf4j.Slf4j;

/**
 * 创建这个类的目的就是在服务器关闭时，可以有人去做一些事儿，比如注销服务
 *
 * @author njzy
 * @package github.njzy.config
 * @create 2022年04月04日 15:39
 */
@Slf4j
public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    /**
     * 注销所有服务注册
     */
    public void unregisterAll(){
        log.info("创建CustomShutdownHook来注销所有已注册服务");

    }

}
