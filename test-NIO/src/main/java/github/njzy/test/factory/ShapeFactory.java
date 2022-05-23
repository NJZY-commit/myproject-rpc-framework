package github.njzy.test.factory;

import github.njzy.test.service.impl.Circle;
import github.njzy.test.service.impl.rectAngle;
import github.njzy.test.service.shape;
import org.checkerframework.checker.units.qual.C;

/**
 * @author njzy
 * @package github.njzy.test.factory
 * @create 2022年05月11日 17:33
 */
public class ShapeFactory {

    public static shape getShape(String shape){

        if (shape == null) return null;

        if("Circle".equals(shape)) return new Circle();

        if ("rectAngle".equals(shape)) return new rectAngle();

        return null;
    }

}
