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
package io.github.alexmofer.projectx.business.others.opentypelist;

import com.am.mvp.core.MVPModel;

import java.io.File;

/**
 * Model
 */
class OpenTypeListModel extends MVPModel<OpenTypeListPresenter> implements OpenTypeListViewModel {

    private static final String DIR_FONTS = "/system/fonts";
    private final File dir = new File(DIR_FONTS);
    private File[] mItems;

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
    public String getItemName(Object item) {
        return item instanceof File ? ((File) item).getName() : null;
    }

    // ViewModel
    @Override
    public void loadOpenType() {
        mItems = dir.listFiles();
        final OpenTypeListPresenter presenter = getPresenter();
        if (presenter == null)
            return;
        presenter.onOpenTypeLoaded();
    }

    @Override
    public String getItemPath(Object item) {
        return item instanceof File ? ((File) item).getPath() : null;
    }
}
