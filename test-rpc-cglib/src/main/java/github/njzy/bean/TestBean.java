package github.njzy.bean;

import github.njzy.bean.annotation.AutoWired;
import github.njzy.bean.annotation.Component;

/**
 * @author njzy
 * @package github.njzy.bean
 * @create 2022年04月20日 14:33
 */
@Component("testBean")
public class TestBean {

    @AutoWired
    private HelloWorld helloWorld;

    public String test(){
        return "这里是testBean，注入成功";
    }

}
