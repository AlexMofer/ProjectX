package am.project.support.font;

/**
 * 字体读取器
 * Created by Alex on 2018/8/30.
 */
interface FontsReader {

    String DIR_CONFIG = "/system/etc";
    String DIR_FONTS = "/system/fonts";

    /**
     * 获取字体配置文件目录
     *
     * @return 目录
     */
    String getConfigDir();

    /**
     * 获取字体目录
     *
     * @return 目录
     */
    String getFontsDir();

    /**
     * 读取配置文件（最好异步执行）
     *
     * @return 字体族集
     */
    FamilySet readConfig();
}
