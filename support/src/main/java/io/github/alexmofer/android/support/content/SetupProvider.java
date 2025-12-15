package io.github.alexmofer.android.support.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 配置 Provider
 * Created by Alex on 2025/11/20.
 */
public abstract class SetupProvider extends ContentProvider {

    /**
     * 初始化
     */
    protected abstract void onSetup();

    @Override
    public final boolean onCreate() {
        onSetup();
        return false;
    }

    @Nullable
    @Override
    public final String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public final Uri insert(@NonNull Uri uri,
                            @Nullable ContentValues values) {
        return null;
    }

    @Override
    public final int delete(@NonNull Uri uri,
                            @Nullable String selection,
                            @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public final Cursor query(@NonNull Uri uri,
                              @Nullable String[] projection,
                              @Nullable String selection,
                              @Nullable String[] selectionArgs,
                              @Nullable String sortOrder) {
        return null;
    }

    @Override
    public final int update(@NonNull Uri uri,
                            @Nullable ContentValues values,
                            @Nullable String selection,
                            @Nullable String[] selectionArgs) {
        return 0;
    }
}
