/*
 * Copyright (C) 2021 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.util.clipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 超级剪切板
 */
public class SuperClipboard {

    public static final String MIME_ITEM = "vnd.android.cursor.item";
    public static final String MIME_DIR = "vnd.android.cursor.dir";
    private static String sAuthority;
    private static Uri sBase;

    private SuperClipboard() {
        //no instance
    }

    /**
     * 设置Authority
     * ClipboardContentProvider能够自动获取，自定义的该子类需要重写getAuthority方法。
     *
     * @param authority Authority
     */
    public static void setAuthority(String authority) {
        ClipboardProvider.setAuthority(authority);
    }

    private static Uri getUri(Context context, String pathSegment) {
        if (sBase == null) {
            if (sAuthority == null) {
                sAuthority = ClipboardProvider.getAuthority(context);
            }
            if (sAuthority != null) {
                sBase = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + sAuthority);
            }
        }
        if (sBase == null) {
            return null;
        }
        return Uri.withAppendedPath(sBase, pathSegment);
    }

    private static Uri getCopyUri(Context context) {
        return getUri(context,
                ClipboardProvider.PATH_COPY + "/" + UUID.randomUUID().toString());
    }

    /**
     * 复制到剪切板
     *
     * @param context Context
     * @param type    MIME数据类型
     * @param data    数据集
     * @return 复制成功时返回true
     */
    public static boolean copy(Context context, String type, Serializable... data) {
        if (context == null || data == null || data.length <= 0) {
            return false;
        }
        final ClipboardManager manager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null) {
            return false;
        }
        try {
            final Uri clear = getUri(context, ClipboardProvider.PATH_CLEAR);
            if (clear != null) {
                context.getContentResolver().delete(clear, null, null);
            }
        } catch (Exception e) {
            // ignore
        }
        final Uri uri = copy(context, data[0]);
        if (uri == null) {
            return false;
        }
        final ClipDescription description = new ClipDescription("URI", new String[]{type});
        final ClipData clip = new ClipData(description, new ClipData.Item(uri));
        final int count = data.length;
        for (int i = 1; i < count; i++) {
            final Uri u = copy(context, data[i]);
            if (u == null) {
                return false;
            }
            clip.addItem(new ClipData.Item(u));
        }
        manager.setPrimaryClip(clip);
        return true;
    }

    /**
     * 复制到剪切板
     *
     * @param context Context
     * @param adapter 文件剪切板内容提供者
     * @return 复制成功时返回true
     */
    public static boolean copy(Context context, FileClipboardAdapter<?> adapter) {
        if (context == null || adapter == null || adapter.getCount() <= 0) {
            return false;
        }
        final ClipboardManager manager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null) {
            return false;
        }
        try {
            final Uri clear = getUri(context, ClipboardProvider.PATH_CLEAR);
            if (clear != null) {
                context.getContentResolver().delete(clear, null, null);
            }
        } catch (Exception e) {
            // ignore
        }
        final Uri uri = copy(context, adapter, 0);
        if (uri == null) {
            return false;
        }
        final ClipDescription description = new ClipDescription("URI", adapter.getTypes());
        final ClipData clip = new ClipData(description, new ClipData.Item(uri));
        final int count = adapter.getCount();
        for (int i = 1; i < count; i++) {
            final Uri u = copy(context, adapter, i);
            if (u == null) {
                return false;
            }
            clip.addItem(new ClipData.Item(u));
        }
        manager.setPrimaryClip(clip);
        return true;
    }

    private static ParcelFileDescriptor openFileDescriptor(Context context, Uri uri, String mode) {
        final ParcelFileDescriptor descriptor;
        try {
            descriptor = context.getContentResolver().openFileDescriptor(uri, mode);
        } catch (Exception e) {
            return null;
        }
        return descriptor;
    }

    private static Uri copy(Context context, Serializable data) {
        final Uri uri = getCopyUri(context);
        if (uri == null) {
            return null;
        }
        final ParcelFileDescriptor descriptor = openFileDescriptor(context, uri, "rwt");
        if (descriptor == null) {
            return null;
        }
        final boolean wrote = SerializableHelper.write(descriptor, data);
        try {
            descriptor.close();
        } catch (IOException e) {
            // ignore
        }
        return wrote ? uri : null;
    }

    private static Uri copy(Context context, FileClipboardAdapter<?> adapter, int position) {
        final Uri uri = getCopyUri(context);
        if (uri == null) {
            return null;
        }
        final ParcelFileDescriptor descriptor = openFileDescriptor(context, uri, "rwt");
        if (descriptor == null) {
            return null;
        }
        final boolean wrote = adapter.write(position, descriptor);
        try {
            descriptor.close();
        } catch (IOException e) {
            // ignore
        }
        return wrote ? uri : null;
    }

    /**
     * 判断剪切板是否包含该类型数据
     *
     * @param context Context
     * @param type    类型
     * @return 包含该类型数据时返回true
     */
    public static boolean contains(Context context, String type) {
        final ClipboardManager manager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null || !manager.hasPrimaryClip()) {
            return false;
        }
        final ClipData clip = manager.getPrimaryClip();
        if (clip == null) {
            return false;
        }
        return clip.getDescription().hasMimeType(type);
    }

    /**
     * 获取全部剪切板序列化数据
     *
     * @param context Context
     * @return 序列化数据
     */
    public static Serializable getClipData(Context context) {
        final ClipboardManager manager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null || !manager.hasPrimaryClip()) {
            return null;
        }
        final ClipData clip = manager.getPrimaryClip();
        if (clip == null || clip.getItemCount() <= 0) {
            return null;
        }
        Uri uri = null;
        final int count = clip.getItemCount();
        for (int i = 0; i < count; i++) {
            final ClipData.Item item = clip.getItemAt(i);
            final Uri u = item.getUri();
            if (u != null) {
                uri = u;
                break;
            }
        }
        if (uri == null) {
            return null;
        }
        final ParcelFileDescriptor descriptor = openFileDescriptor(context, uri, "r");
        if (descriptor == null) {
            return null;
        }
        final Serializable data = SerializableHelper.read(descriptor);
        try {
            descriptor.close();
        } catch (IOException e) {
            // ignore
        }
        return data;
    }

    /**
     * 获取全部剪切板序列化数据集
     *
     * @param context Context
     * @return 序列化数据集
     */
    public static List<Serializable> getAllClipData(Context context) {
        final ClipboardManager manager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null || !manager.hasPrimaryClip()) {
            return null;
        }
        final ClipData clip = manager.getPrimaryClip();
        if (clip == null || clip.getItemCount() <= 0) {
            return null;
        }
        final ArrayList<Serializable> items = new ArrayList<>();
        final int count = clip.getItemCount();
        for (int i = 0; i < count; i++) {
            final ClipData.Item item = clip.getItemAt(i);
            final Uri uri = item.getUri();
            if (uri != null) {
                final ParcelFileDescriptor descriptor = openFileDescriptor(context, uri, "r");
                if (descriptor != null) {
                    final Serializable data = SerializableHelper.read(descriptor);
                    try {
                        descriptor.close();
                    } catch (IOException e) {
                        // ignore
                    }
                    if (data != null) {
                        items.add(data);
                    }
                }
            }
        }
        return items.isEmpty() ? null : items;
    }

    /**
     * 获取所有剪切板数据
     *
     * @param context Context
     * @param adapter FileClipboardAdapter
     * @param <T>     类型
     * @return 数据集
     */
    public static <T> List<T> getAllClipData(Context context, FileClipboardAdapter<T> adapter) {
        final ClipboardManager manager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null || !manager.hasPrimaryClip()) {
            return null;
        }
        final ClipData clip = manager.getPrimaryClip();
        if (clip == null || clip.getItemCount() <= 0) {
            return null;
        }
        final ArrayList<T> items = new ArrayList<>();
        final int count = clip.getItemCount();
        for (int i = 0; i < count; i++) {
            final ClipData.Item item = clip.getItemAt(i);
            final Uri uri = item.getUri();
            if (uri != null) {
                final ParcelFileDescriptor descriptor = openFileDescriptor(context, uri, "r");
                if (descriptor != null) {
                    final T data = adapter.read(descriptor);
                    try {
                        descriptor.close();
                    } catch (IOException e) {
                        // ignore
                    }
                    if (data != null) {
                        items.add(data);
                    }
                }
            }
        }
        return items.isEmpty() ? null : items;
    }

    /**
     * 清除剪切板
     *
     * @param context Context
     * @return 是否清楚成功
     */
    public static boolean clear(Context context) {
        try {
            final Uri clear = getUri(context, ClipboardProvider.PATH_CLEAR);
            if (clear != null) {
                context.getContentResolver().delete(clear, null, null);
            }
        } catch (Exception e) {
            return false;
        }
        final ClipboardManager manager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                manager.clearPrimaryClip();
            } else {
                manager.setPrimaryClip(ClipData.newPlainText("TEXT", ""));
            }
        }
        return true;
    }

    /**
     * 判断是否已复制
     * 检查的是ClipboardContentProvider，而非ClipboardManager
     *
     * @param context Context
     * @return 已复制内容时返回true
     */
    public static boolean isCopied(Context context) {
        try {
            final Uri check = getUri(context, ClipboardProvider.PATH_CHECK);
            if (check != null) {
                final Cursor cursor =
                        context.getContentResolver().query(check, null, null,
                                null, null);
                if (cursor != null) {
                    boolean result = false;
                    if (cursor.moveToFirst()) {
                        final byte[] blob = cursor.getBlob(0);
                        result = blob != null && blob.length == 1 && blob[0] != 0;
                    }
                    cursor.close();
                    return result;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return false;
    }
}
