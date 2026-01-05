package io.github.alexmofer.android.support.glide;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.ObjectKey;

/**
 * 本地图片提供者
 * Created by Alex on 2026/1/5.
 */
public interface LocalImageAdapter {

    /**
     * 用于做缓存的 Key
     *
     * @return Key
     */
    default Key getKey() {
        return new ObjectKey(this);
    }
}