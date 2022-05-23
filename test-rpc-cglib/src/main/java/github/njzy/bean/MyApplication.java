package github.njzy.bean;

/**
 * @author njzy
 * @package github.njzy.bean
 * @create 2022年04月20日 10:34
 */
public class MyApplication {

    /* 一段简易的Spring启动代码 */
    public static void main(String[] args) throws Exception {
        MyApplicationContext context = new MyApplicationContext(MyConfig.class);
        HelloWorld helloWorld = (HelloWorld) context.getBean("helloWorld");
        //System.out.println(helloWorld);
        helloWorld.test();
    }

}
