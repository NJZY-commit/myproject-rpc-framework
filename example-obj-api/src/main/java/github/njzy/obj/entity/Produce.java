package github.njzy.obj.entity;

import java.io.Serializable;

/**
 * @author njzy
 * @package github.njzy.obj
 * @create 2022年04月02日 10:26
 */
public class Produce implements Serializable {

    private static final long serialVersionUID = 6956540332493657672L;

    /**
     * 商品名
     */
    private String prodName;

    /**
     * 商品价格
     */
    private Integer prodPrice;

    public Produce() {
    }

    public Produce(String prodName, Integer prodPrice) {
        this.prodName = prodName;
        this.prodPrice = prodPrice;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public Integer getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(Integer prodPrice) {
        this.prodPrice = prodPrice;
    }

    @Override
    public String toString() {
        return "produce{" +
                "prodName='" + prodName + '\'' +
                ", prodPrice=" + prodPrice +
                '}';
    }
}
