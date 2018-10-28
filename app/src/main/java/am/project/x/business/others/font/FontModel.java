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
package am.project.x.business.others.font;

import java.util.List;

import am.util.font.TypefaceCollection;
import am.util.font.TypefaceConfig;
import am.util.mvp.AMModel;

/**
 * Model
 */
class FontModel extends AMModel<FontPresenter> implements FontViewModel, FontJob.Callback {

    private TypefaceConfig mConfig;
    private String mDefaultName;
    private List<String> mNames;
    private List<String> mAlias;
    private TypefaceCollection mTypeface;

    FontModel(FontPresenter presenter) {
        super(presenter);
    }

    // PickerViewModel
    @Override
    public String getDefaultFamilyName() {
        return mDefaultName;
    }

    @Override
    public int getFamilyNameOrAliasCount() {
        final int nameCount = mNames == null ? 0 : mNames.size();
        final int aliaCount = mAlias == null ? 0 : mAlias.size();
        return nameCount + aliaCount;
    }

    @Override
    public String getFamilyNameOrAlias(int position) {
        final int nameCount = mNames == null ? 0 : mNames.size();
        //noinspection ConstantConditions
        return position < nameCount ? mNames.get(position) : mAlias.get(position - nameCount);
    }

    @Override
    public boolean isFamilyAlias(int position) {
        return position >= (mNames == null ? 0 : mNames.size());
    }

    // AdapterViewModel

    // ViewModel
    @Override
    public void loadConfig() {
        FontJob.loadConfig(this);
    }

    @Override
    public void loadTypefaceCollection(String nameOrAlias) {
        FontJob.loadTypefaceCollection(this, mConfig, nameOrAlias);
    }

    // Callback
    @Override
    public void onLoadConfigFailure() {
        if (isDetachedFromPresenter())
            return;
        getPresenter().onLoadConfigFailure();
    }

    @Override
    public void onLoadConfigSuccess(TypefaceConfig config) {
        mConfig = config;
        mDefaultName = config.getDefaultName();
        mNames = config.getNames();
        mAlias = config.getAliases();
        if (isDetachedFromPresenter())
            return;
        getPresenter().onLoadConfigSuccess();
    }

    @Override
    public void onLoadTypefaceCollectionFailure() {
        if (isDetachedFromPresenter())
            return;
        getPresenter().onLoadTypefaceCollectionFailure();
    }

    @Override
    public void onLoadTypefaceCollectionSuccess(TypefaceCollection collection) {
        mTypeface = collection;
        if (isDetachedFromPresenter())
            return;
        getPresenter().onLoadTypefaceCollectionSuccess();
    }
}
