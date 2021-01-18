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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileNotFoundException;

/**
 * 剪切板ContentProvider
 * 使用{@link SuperClipboard}
 * Created by Alex on 2019/3/7.
 */
@Deprecated
@SuppressWarnings("ALL")
public abstract class ClipboardProvider extends ContentProvider {

    private UriMatcher mMatcher;

    /**
     * 获取剪切板辅助器
     *
     * @return 剪切板辅助器
     */
    protected abstract ClipboardHelper getClipboardHelper();

    @Override
    public boolean onCreate() {
        final ClipboardHelper helper = getClipboardHelper();
        if (helper == null)
            return false;
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(helper.getAuthority(), helper.getPath() + "/*", helper.getCode());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final ClipboardHelper helper = getClipboardHelper();
        if (helper == null)
            return null;
        if (mMatcher.match(uri) == helper.getCode()) {
            return helper.getType(this, uri);
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final ClipboardHelper helper = getClipboardHelper();
        if (helper == null)
            return null;
        if (mMatcher.match(uri) == helper.getCode()) {
            return helper.insert(this, uri, values);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final ClipboardHelper helper = getClipboardHelper();
        if (helper == null)
            return 0;
        if (mMatcher.match(uri) == helper.getCode()) {
            return helper.delete(this, uri, selection, selectionArgs);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final ClipboardHelper helper = getClipboardHelper();
        if (helper == null)
            return 0;
        if (mMatcher.match(uri) == helper.getCode()) {
            return helper.update(this, uri, values, selection, selectionArgs);
        }
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        final ClipboardHelper helper = getClipboardHelper();
        if (helper == null)
            return null;
        if (mMatcher.match(uri) == helper.getCode()) {
            return helper.query(this, uri, projection, selection, selectionArgs, sortOrder);
        }
        return null;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {
        final ClipboardHelper helper = getClipboardHelper();
        if (helper == null)
            return super.openFile(uri, mode);
        if (mMatcher.match(uri) == helper.getCode()) {
            return helper.openFile(this, uri, mode);
        }
        return super.openFile(uri, mode);
    }
}
