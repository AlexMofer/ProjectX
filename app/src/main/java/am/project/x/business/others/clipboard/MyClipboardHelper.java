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

import am.project.x.BuildConfig;
import am.util.clipboard.FileClipboardHelper;

final class MyClipboardHelper extends FileClipboardHelper {

    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".clipboardprovider";
    private static MyClipboardHelper mInstance;

    private MyClipboardHelper() {
        //no instance
    }

    static MyClipboardHelper getInstance() {
        if (mInstance == null)
            mInstance = new MyClipboardHelper();
        return mInstance;
    }

    @Override
    protected String getAuthority() {
        return AUTHORITY;
    }

    @Override
    protected int getAdapterCount() {
        return 1;
    }

    @Override
    protected Adapter getAdapter(int index) {
        switch (index) {
            case 0:
                return MyTestClipboardAdapter.getInstance();
        }
        return null;
    }
}
