/*
 * Copyright (C) 2018 AlexMofer
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
package am.project.x.business.others.opentype;

import java.io.File;

import am.util.mvp.AMModel;

/**
 * Model
 */
class OpenTypeModel extends AMModel<OpenTypePresenter> implements OpenTypeViewModel {

    private static final String DIR_FONTS = "/system/fonts";
    private final File dir = new File(DIR_FONTS);
    private File[] mItems;

    OpenTypeModel(OpenTypePresenter presenter) {
        super(presenter);
    }

    // AdapterViewModel
    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.length;
    }

    @Override
    public File getItem(int position) {
        return mItems == null ? null : mItems[position];
    }

    @Override
    public String getItemName(int position) {
        final File file = getItem(position);
        if (file == null)
            return null;
        return file.getName();
    }

    // ViewModel
    @Override
    public void loadOpenType() {
        mItems = dir.listFiles();
        if (isDetachedFromPresenter())
            return;
        getPresenter().onOpenTypeLoaded();
    }
}
