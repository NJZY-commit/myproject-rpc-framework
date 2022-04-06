package github.njzy.compress;

import github.njzy.extension.SPI;

/**
 * 压缩工具的顶级接口
 *
 * @author njzy
 * @package github.njzy.compress
 * @create 2022年04月06日 19:10
 */
@SPI
public interface Compress {

    /**
     * 压缩
     *
     * @param bytes byte文件
     * @return
     */
    byte[] compress(byte[] bytes);

    /**
     * 解压
     *
     * @param bytes byte文件
     * @return
     */
    byte[] decompress(byte[] bytes);

}
