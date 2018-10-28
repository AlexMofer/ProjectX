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

import am.util.mvp.AMPresenter;

/**
 * Presenter
 */
class FontPresenter extends AMPresenter<FontView, FontModel> implements FontView, FontViewModel {

    private final FontModel mModel = new FontModel(this);

    FontPresenter(FontView view) {
        super(view);
    }

    @Override
    protected FontModel getModel() {
        return mModel;
    }

    // View
    @Override
    public void onLoadConfigFailure() {
        if (isDetachedFromView())
            return;
        getView().onLoadConfigFailure();
    }

    @Override
    public void onLoadConfigSuccess() {
        if (isDetachedFromView())
            return;
        getView().onLoadConfigSuccess();
    }

    @Override
    public void onLoadTypefaceCollectionFailure() {
        if (isDetachedFromView())
            return;
        getView().onLoadTypefaceCollectionFailure();
    }

    @Override
    public void onLoadTypefaceCollectionSuccess() {
        if (isDetachedFromView())
            return;
        getView().onLoadTypefaceCollectionSuccess();
    }

    // PickerViewModel
    @Override
    public String getDefaultFamilyName() {
        return getModel().getDefaultFamilyName();
    }

    @Override
    public int getFamilyNameOrAliasCount() {
        return getModel().getFamilyNameOrAliasCount();
    }

    @Override
    public String getFamilyNameOrAlias(int position) {
        return getModel().getFamilyNameOrAlias(position);
    }

    @Override
    public boolean isFamilyAlias(int position) {
        return getModel().isFamilyAlias(position);
    }

    // AdapterViewModel
    @Override
    public int getTypefaceFallbackCount() {
        return getModel().getTypefaceFallbackCount();
    }

    @Override
    public String getTypefaceName() {
        return getModel().getTypefaceName();
    }

    @Override
    public String getCommonTitle() {
        return getModel().getCommonTitle();
    }

    @Override
    public int getCommonItemCount() {
        return getModel().getCommonItemCount();
    }

    @Override
    public Object getCommonItem(int position) {
        return getModel().getCommonItem(position);
    }

    @Override
    public Object getFallback(int position) {
        return getModel().getFallback(position);
    }

    @Override
    public String getFallbackTitle(Object fallback) {
        return getModel().getFallbackTitle(fallback);
    }

    @Override
    public int getFallbackItemCount(Object fallback) {
        return getModel().getFallbackItemCount(fallback);
    }

    @Override
    public Object getFallbackItem(Object fallback, int position) {
        return getModel().getFallbackItem(fallback, position);
    }

    @Override
    public String getTypefaceItemName(Object item) {
        return getModel().getTypefaceItemName(item);
    }

    @Override
    public String getTypefaceItemInfo(Object item) {
        return getModel().getTypefaceItemInfo(item);
    }

    // ViewModel
    @Override
    public void loadConfig() {
        getModel().loadConfig();
    }

    @Override
    public void loadTypefaceCollection(String nameOrAlias) {
        getModel().loadTypefaceCollection(nameOrAlias);
    }

    @Override
    public String getTypefaceItemPath(Object item) {
        return getModel().getTypefaceItemPath(item);
    }
}
