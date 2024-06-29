package io.github.alexmofer.android.support.other;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

/**
 * DocumentFile 筛选器
 * Created by Alex on 2024/5/22.
 */
public interface DocumentFileFilter {

    /**
     * 测试是否接受该 DocumentFile
     *
     * @param df DocumentFile
     * @return 接受该 DocumentFile 时返回true
     */
    boolean accept(@NonNull DocumentFile df);
}
