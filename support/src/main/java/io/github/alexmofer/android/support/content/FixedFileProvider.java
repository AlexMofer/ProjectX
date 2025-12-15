package io.github.alexmofer.android.support.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.provider.DocumentsContract;
import android.util.ArraySet;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.FileNotFoundException;

/**
 * 修复 getType 直接从原始文件读取而忽略 displayName 的问题
 * 修复 只读 Uri 可强制读写打开问题
 * Created by Alex on 2025/11/20.
 */
public final class FixedFileProvider extends FileProvider {

    private static final String KEY_RO = "read_only";
    private final ArraySet<Uri> mReadOnlyUris = new ArraySet<>();

    public static void insertReadOnlyUri(@NonNull Context context, @NonNull Uri uri) {
        final ContentValues values = new ContentValues();
        values.put(KEY_RO, true);
        context.getContentResolver().insert(uri, values);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final String name = queryName(uri);
        if (name != null) {
            final int lastDot = name.lastIndexOf('.');
            if (lastDot >= 0) {
                final String extension = name.substring(lastDot + 1);
                final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                if (mime != null) {
                    return mime;
                }
            }
        }
        return super.getType(uri);
    }

    @Override
    public Uri insert(@NonNull Uri uri, @NonNull ContentValues values) {
        // 为避免多进程问题，此处使用uid来匹配，不同于Linux，Android的uid用于区分应用，各应用uid不一样
        if (Binder.getCallingUid() == Process.myUid()) {
            // 确保只有应用自身可以配置Uri只读
            if (values.containsKey(KEY_RO) && Boolean.TRUE.equals(values.getAsBoolean(KEY_RO))) {
                // 指定Uri只读
                mReadOnlyUris.add(uri);
                return uri;
            }
        }
        return super.insert(uri, values);
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        if (mReadOnlyUris.contains(uri)) {
            throw new SecurityException("Permission denied: Write permission not granted.");
        }
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri,
                                         @NonNull String mode)
            throws FileNotFoundException {
        if (mReadOnlyUris.contains(uri)) {
            // 无写入权限
            if (mode.contains("w")) {
                throw new SecurityException("Permission denied: Write permission not granted.");
            }
        }
        return super.openFile(uri, mode);
    }

    @Nullable
    private String queryName(Uri uri) {
        try (final Cursor cursor = query(uri,
                new String[]{DocumentsContract.Document.COLUMN_DISPLAY_NAME},
                null, null, null)) {
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                return cursor.getString(0);
            }
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }
}
