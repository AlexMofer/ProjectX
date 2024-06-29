package io.github.alexmofer.android.support.app;

import androidx.annotation.NonNull;

/**
 * 应用级数据创建器
 * Created by Alex on 2024/2/28.
 */
public interface ApplicationDataCreator<T extends ApplicationData> {

    /**
     * 创建
     *
     * @return 应用级数据
     */
    @NonNull
    T create();
}
