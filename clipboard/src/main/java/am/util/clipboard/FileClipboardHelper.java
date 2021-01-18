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
import android.content.Context;
import android.net.Uri;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 文件剪切板辅助器
 * 使用{@link SuperClipboard}
 * Created by Alex on 2019/3/8.
 */
@Deprecated
@SuppressWarnings("ALL")
public abstract class FileClipboardHelper extends ClipboardHelper {

    private static final String NAME = "am_util_clipboard_tmp_clipboard";

    @Override
    protected boolean onCopy(Context context, Object data, Uri uri, ClipboardHelper.Adapter adapter) {
        if (!(adapter instanceof Adapter))
            return super.onCopy(context, data, uri, adapter);
        if (data == null)
            return false;
        //noinspection unchecked
        final Serializable serializable = ((Adapter) adapter).getCopyData(context, uri, data);
        if (serializable == null)
            return false;
        final FileOutputStream fos;
        try {
            fos = context.openFileOutput(NAME, Context.MODE_PRIVATE);
        } catch (Exception e) {
            return false;
        }
        final ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(fos);
        } catch (Exception e) {
            try {
                fos.close();
            } catch (Exception e1) {
                // ignore
            }
            return false;
        }
        try {
            oos.writeObject(serializable);
        } catch (Exception e) {
            try {
                oos.close();
            } catch (Exception e1) {
                // ignore
            }
            try {
                fos.close();
            } catch (Exception e1) {
                // ignore
            }
            return false;
        }
        boolean finish = true;
        try {
            oos.close();
        } catch (Exception e1) {
            finish = false;
        }
        try {
            fos.close();
        } catch (Exception e1) {
            finish = false;
        }
        return finish;
    }

    @Override
    protected <T> T onPaste(Context context, ClipData clip, ClipboardHelper.Adapter adapter) {
        if (!(adapter instanceof Adapter))
            return super.onPaste(context, clip, adapter);
        final FileInputStream fis;
        try {
            fis = context.openFileInput(NAME);
        } catch (Exception e) {
            return null;
        }
        final ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(fis);
        } catch (Exception e) {
            try {
                fis.close();
            } catch (Exception e1) {
                // ignore
            }
            return null;
        }
        final Serializable serializable;
        try {
            serializable = (Serializable) ois.readObject();
        } catch (Exception e) {
            try {
                ois.close();
            } catch (Exception e1) {
                // ignore
            }
            try {
                fis.close();
            } catch (Exception e1) {
                // ignore
            }
            return null;
        }
        boolean finish = true;
        try {
            ois.close();
        } catch (Exception e1) {
            finish = false;
        }
        try {
            fis.close();
        } catch (Exception e1) {
            finish = false;
        }
        //noinspection unchecked
        return finish ? (T) ((Adapter) adapter).getPasteData(context, serializable) : null;
    }

    public static abstract class Adapter<T> extends ClipboardHelper.Adapter<T> {

        /**
         * 获取复制数据
         *
         * @param context Context
         * @param uri     数据连接
         * @param data    数据
         * @return 复制数据
         */
        protected abstract Serializable getCopyData(Context context, Uri uri, T data);

        /**
         * 获取粘贴数据
         *
         * @param context Context
         * @param data    序列化数据
         * @return 数据
         */
        public abstract T getPasteData(Context context, Serializable data);

    }
}
