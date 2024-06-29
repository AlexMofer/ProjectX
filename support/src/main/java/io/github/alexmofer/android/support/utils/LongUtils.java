package io.github.alexmofer.android.support.utils;

import java.util.UUID;

/**
 * Long工具
 * Created by Alex on 2022/5/21.
 */
public class LongUtils {

    private LongUtils() {
        //no instance
    }

    /**
     * 随机获取一个唯一Long
     *
     * @param least 最少的
     * @return 唯一Long
     */
    public static long random(boolean least) {
        return least ? UUID.randomUUID().getLeastSignificantBits() :
                UUID.randomUUID().getMostSignificantBits();
    }
}
