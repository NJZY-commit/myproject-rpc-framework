package github.njzy.test;

import github.njzy.test.factory.ShapeFactory;
import github.njzy.test.service.shape;

/**
 * @author njzy
 * @package github.njzy.test
 * @create 2022年05月11日 17:40
 */
public class FactoryTest {

    public static void main(String[] args) {
        shape circle = ShapeFactory.getShape("Circle");
        circle.create();
    }

}
