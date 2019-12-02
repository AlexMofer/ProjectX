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

package am.util.ftpserver;

import android.content.ContentResolver;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import java.lang.ref.WeakReference;

/**
 * Uri形式的文件系统视图提供者
 * Created by Alex on 2019/10/8.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class UriFtpFileSystemViewAdapter implements FtpFileSystemViewAdapter {

    private final WeakReference<ContentResolver> mContentResolver;
    private final DocumentFile mHomeDirectory;
    private UriFtpFileSystemView mView;

    @SuppressWarnings("WeakerAccess")
    public UriFtpFileSystemViewAdapter(ContentResolver contentResolver,
                                       DocumentFile homeDirectory) {
        mContentResolver = new WeakReference<>(contentResolver);
        mHomeDirectory = homeDirectory;
    }

    @Override
    public void onAttached(FtpUser user) {
        mView = null;
        final ContentResolver contentResolver = mContentResolver.get();
        if (contentResolver != null)
            mView = new UriFtpFileSystemView(user, contentResolver, mHomeDirectory);
    }

    @Override
    public UriFtpFileSystemView createFileSystemView() {
        return mView;
    }
}
