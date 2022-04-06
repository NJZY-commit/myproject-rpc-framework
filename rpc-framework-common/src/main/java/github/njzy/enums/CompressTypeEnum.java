package github.njzy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 压缩文件类型的枚举类
 *
 * @author njzy
 * @package github.njzy.enums
 * @create 2022年04月06日 17:48
 */
@Getter
@AllArgsConstructor
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
