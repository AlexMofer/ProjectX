/*
 * Copyright (C) 2019 AlexMofer
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
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.SparseArray;

import java.io.FileNotFoundException;

/**
 * 剪切板辅助器
 * Created by Alex on 2019/3/8.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ClipboardHelper {

    private static final String PATH_CLIPBOARD = "clipboard";
    private static final int CODE_CLIPBOARD = 0;
    private UriMatcher mMatcher;
    private final SparseArray<String> mTypes = new SparseArray<>();

    public ClipboardHelper() {
        onCreate();
    }

    /**
     * 获取Authority
     *
     * @return Authority
     */
    protected abstract String getAuthority();

    protected void onCreate() {
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mTypes.clear();
        final int count = getAdapterCount();
        if (count <= 0)
            return;
        for (int i = 0; i < count; i++) {
            final Adapter adapter = getAdapter(i);
            if (adapter == null)
                continue;
            final String type = adapter.getType();
            final String subType = adapter.getSubType();
            mTypes.put(i, type);
            mMatcher.addURI(getAuthority(), getPath() + "/" + subType, i);
        }
    }

    /**
     * 插入
     *
     * @param provider ClipboardProvider
     * @param uri      数据URI
     * @param values   插入数据
     * @return 数据URI
     */
    protected Uri insert(ClipboardProvider provider, Uri uri, ContentValues values) {
        return null;
    }


    /**
     * 删除
     *
     * @param provider      ClipboardProvider
     * @param uri           数据URI
     * @param selection     选择
     * @param selectionArgs 选择参数
     * @return 被删除条目数
     */
    protected int delete(ClipboardProvider provider, Uri uri, String selection,
                         String[] selectionArgs) {
        return 0;
    }

    /**
     * 更新
     *
     * @param provider      ClipboardProvider
     * @param uri           数据URI
     * @param values        更新数据
     * @param selection     选择
     * @param selectionArgs 选择参数
     * @return 被更新条目数
     */
    protected int update(ClipboardProvider provider, Uri uri, ContentValues values,
                         String selection, String[] selectionArgs) {
        return 0;
    }


    /**
     * 查询
     *
     * @param provider      ClipboardProvider
     * @param uri           数据URI
     * @param projection    游标对应行集合
     * @param selection     选择
     * @param selectionArgs 选择参数
     * @param sortOrder     排序方式
     * @return 游标
     */
    protected Cursor query(ClipboardProvider provider, Uri uri, String[] projection,
                           String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    /**
     * 打开文件
     *
     * @param provider ClipboardProvider
     * @param uri      数据URI
     * @param mode     模式
     * @return 文件描述
     * @throws FileNotFoundException 错误
     */
    protected ParcelFileDescriptor openFile(ClipboardProvider provider, Uri uri, String mode)
            throws FileNotFoundException {
        throw new FileNotFoundException("No files supported by provider at " + uri);
    }

    /**
     * 获取路径
     *
     * @return 路径
     */
    protected String getPath() {
        return PATH_CLIPBOARD;
    }

    /**
     * 获取返回码
     *
     * @return 返回码
     */
    protected int getCode() {
        return CODE_CLIPBOARD;
    }

    /**
     * 获取数据MIME类型
     *
     * @param uri 数据URI
     * @return MIME类型或null表示无类型
     */
    protected String getType(ClipboardProvider provider, Uri uri) {
        if (mMatcher == null)
            return null;
        return mTypes.get(mMatcher.match(uri));
    }

    /**
     * 获取Adapter总数
     *
     * @return Adapter总数
     */
    protected abstract int getAdapterCount();

    /**
     * 获取Adapter
     *
     * @param index 下标
     * @return Adapter
     */
    protected abstract Adapter getAdapter(int index);

    /**
     * 复制
     *
     * @param context Context
     * @param data    数据
     * @return 是否成功
     */
    public boolean copy(Context context, Object data) {
        if (mMatcher == null)
            return false;
        final int count = getAdapterCount();
        if (count <= 0)
            return false;
        for (int i = 0; i < count; i++) {
            final Adapter adapter = getAdapter(i);
            if (adapter == null)
                continue;
            if (adapter.canCopy(context, data)) {
                final ClipboardManager manager = (ClipboardManager)
                        context.getSystemService(Context.CLIPBOARD_SERVICE);
                if (manager == null)
                    return false;
                final Uri uri = Uri.parse("content://" + getAuthority() + "/" + getPath() +
                        "/" + adapter.getSubType());
                if (onCopy(context, data, uri, adapter)) {
                    ClipData clip = ClipData.newUri(context.getContentResolver(), "URI", uri);
                    manager.setPrimaryClip(clip);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 粘贴
     *
     * @param context Context
     * @param data    数据
     * @param uri     连接
     * @param adapter Adapter
     * @return 是否成功
     */
    protected boolean onCopy(Context context, Object data, Uri uri, Adapter adapter) {
        return false;
    }

    /**
     * 判断是否能粘贴
     *
     * @param context Context
     * @return 是否能粘贴
     */
    public boolean canPaste(Context context) {
        if (mMatcher == null)
            return false;
        final ClipboardManager manager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null)
            return false;
        final ClipData clip = manager.getPrimaryClip();
        if (clip == null)
            return false;
        if (clip.getItemCount() <= 0)
            return false;
        final ClipData.Item item = clip.getItemAt(0);
        final Uri uri = item.getUri();
        if (uri == null)
            return false;
        final String type = mTypes.get(mMatcher.match(uri));
        return type != null && TextUtils.equals(type, context.getContentResolver().getType(uri));
    }

    /**
     * 粘贴
     *
     * @param context Context
     * @return 数据，可能为空
     */
    public <T> T paste(Context context) {
        if (mMatcher == null)
            return null;
        final ClipboardManager manager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null)
            return null;
        final ClipData clip = manager.getPrimaryClip();
        if (clip == null)
            return null;
        if (clip.getItemCount() <= 0)
            return null;
        final ClipData.Item item = clip.getItemAt(0);
        final Uri uri = item.getUri();
        if (uri == null)
            return null;
        final int index = mMatcher.match(uri);
        final Adapter<?> adapter = getAdapter(index);
        if (adapter == null)
            return null;
        return onPaste(context, clip, adapter);
    }

    /**
     * 粘贴
     *
     * @param context Context
     * @param clip    剪切板
     * @param adapter Adapter
     * @param <T>     Adapter类型
     * @return 数据，可能为空
     */
    protected <T> T onPaste(Context context, ClipData clip, Adapter adapter) {
        return null;
    }

    public static abstract class Adapter<T> {

        protected static final String MIME_ITEM = "vnd.android.cursor.item";
        protected static final String MIME_DIR = "vnd.android.cursor.dir";

        /**
         * 获取MIME
         * 基本格式vnd.android.cursor.item/vnd.example.subtype(单条)
         * 或vnd.android.cursor.dir/vnd.example.subtype(多条)
         *
         * @return MIME
         */
        public abstract String getType();

        /**
         * 获取子类型（判断复制类型）
         *
         * @return 子类型
         */
        public abstract String getSubType();

        /**
         * 判断是否能粘贴
         *
         * @param context Context
         * @param data    数据
         * @return 是否能粘贴
         */
        public abstract boolean canCopy(Context context, Object data);
    }
}
