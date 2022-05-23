package github.njzy.test.service.impl;

import github.njzy.test.service.shape;

/**
 * @author njzy
 * @package github.njzy.test.service.impl
 * @create 2022年05月11日 17:35
 */
public class Circle implements shape {
    @Override
    public void create() {
        System.out.println("create a circle");
    }
}
