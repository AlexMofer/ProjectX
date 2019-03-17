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
package am.project.x.business.others.clipboard;

import android.content.Context;
import android.net.Uri;

import java.io.Serializable;

import am.util.clipboard.FileClipboardHelper;

/**
 * Created by Alex on 2019/3/8.
 */
final class MyTestClipboardAdapter extends FileClipboardHelper.Adapter<ClipboardBean> {

    private static final String SUBTYPE = "test";
    private static final String TYPE = MIME_ITEM + "/vnd.am." + SUBTYPE;
    private static MyTestClipboardAdapter mInstance;

    private MyTestClipboardAdapter() {
        //no instance
    }

    static MyTestClipboardAdapter getInstance() {
        if (mInstance == null)
            mInstance = new MyTestClipboardAdapter();
        return mInstance;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getSubType() {
        return SUBTYPE;
    }

    @Override
    public boolean canCopy(Context context, Object data) {
        return data instanceof ClipboardBean;
    }

    @Override
    protected Serializable getCopyData(Context context, Uri uri, ClipboardBean data) {
        return data;
    }

    @Override
    public ClipboardBean getPasteData(Context context, Serializable data) {
        return (ClipboardBean) data;
    }
}
