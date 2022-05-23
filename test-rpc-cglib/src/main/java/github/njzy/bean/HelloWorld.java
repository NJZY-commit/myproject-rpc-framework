package github.njzy.bean;

import github.njzy.bean.annotation.AutoWired;
import github.njzy.bean.annotation.Component;
import lombok.Data;

/**
 * @author njzy
 * @package github.njzy.bean
 * @create 2022年04月20日 10:36
 */
@Data
@Component("helloWorld")
public class HelloWorld implements InitializingBean{

    @AutoWired
    private TestBean testBean;

    private String context = "hello, spring";

    private String password;


    public void test(){
        System.out.println(testBean.test());
    }

    /*实现InitializingBean，重写afterPropertiesSet方法*/
    @Override
    public void afterPropertiesSet() throws Exception {
        password = "从远程加密机获取密码";
        System.out.println(password);
    }
}
