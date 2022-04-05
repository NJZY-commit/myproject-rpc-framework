package github.njzy.rpcenum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author njzy
 * @package github.njzy
 * @create 2022年03月17日 12:58
 */
@AllArgsConstructor
@Getter
@ToString
public enum ResponseEnum {

    SUCESS(200,"The remote call is successfu"),
    FAIL(500, "The remote call is fail");

    private final int code;

    private final String message;
}
