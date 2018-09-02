package am.project.support.font;

import android.os.Build;


/**
 * 兼容器
 * Created by Alex on 2018/8/30.
 */
class FontsReaderCompat {

    private static final FontsReader IMPL;

    static {
        final int api = Build.VERSION.SDK_INT;
        if (api >= 28) {
            IMPL = new FontsReaderApi28();
        } else if (api >= 27) {
            IMPL = new FontsReaderApi13();// TODO
        } else if (api >= 26) {
            IMPL = new FontsReaderApi26();
        } else if (api >= 25) {
            IMPL = new FontsReaderApi25();
        } else if (api >= 24) {
            IMPL = new FontsReaderApi24();
        } else if (api >= 23) {
            IMPL = new FontsReaderApi23();
        } else if (api >= 22) {
            IMPL = new FontsReaderApi22();
        } else if (api >= 21) {
            IMPL = new FontsReaderApi21();
        } else if (api >= 20) {
            IMPL = new FontsReaderApi20();
        } else if (api >= 19) {
            IMPL = new FontsReaderApi19();
        } else if (api >= 18) {
            IMPL = new FontsReaderApi18();
        } else if (api >= 17) {
            IMPL = new FontsReaderApi17();
        } else if (api >= 16) {
            IMPL = new FontsReaderApi16();
        } else if (api >= 15) {
            IMPL = new FontsReaderApi15();
        } else if (api >= 14) {
            IMPL = new FontsReaderApi14();
        } else if (api >= 13) {
            IMPL = new FontsReaderApi13();
        } else if (api >= 12) {
            IMPL = new FontsReaderApi12();
        } else if (api >= 11) {
            IMPL = new FontsReaderApi11();
        } else if (api >= 10) {
            IMPL = new FontsReaderApi10();
        } else {
            IMPL = new FontsReaderBase();
        }
    }

    /**
     * 获取字体读取器
     *
     * @return 字体读取器
     */
    static FontsReader getFontsReader() {
        return IMPL;
    }

    private FontsReaderCompat() {
        //no instance
    }
}
