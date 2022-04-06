package github.njzy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 序列化类型枚举类
 *
 * @author njzy
 * @package github.njzy.enums
 * @create 2022年04月06日 19:03
 */
@Getter
@AllArgsConstructor
public enum SerializationTypeEnum {

    KYRO((byte) 0x01, "kyro"), // kyro序列化工具
    PROTOSTUFF((byte) 0x02, "protostuff"),
    HESSIAN((byte) 0X03, "hessian");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (SerializationTypeEnum c : SerializationTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
