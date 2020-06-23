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

import am.util.mvp.core.MVPPresenter;

/**
 * Presenter
 */
class OpenTypePresenter extends MVPPresenter<OpenTypeView, OpenTypeModel> implements OpenTypeView,
        OpenTypeViewModel {

    OpenTypePresenter() {
        setModel(new OpenTypeModel());
    }

    // View
    @Override
    public void onParseFailure() {
        final OpenTypeView view = getView();
        if (view != null)
            view.onParseFailure();
    }

    @Override
    public void onParseSuccess(boolean isCollection) {
        final OpenTypeView view = getView();
        if (view != null)
            view.onParseSuccess(isCollection);
    }

    // AdapterViewModel
    @Override
    public int getItemCount() {
        return getModel().getItemCount();
    }

    @Override
    public Object getItem(int position) {
        return getModel().getItem(position);
    }

    @Override
    public String getItemLabel(Object item) {
        return getModel().getItemLabel(item);
    }

    @Override
    public String getItemInfo(Object item) {
        return getModel().getItemInfo(item);
    }

    // PickerViewModel
    @Override
    public int getSubCount() {
        return getModel().getSubCount();
    }

    @Override
    public Object getSubItem(int position) {
        return getModel().getSubItem(position);
    }

    @Override
    public String getSubName(Object item) {
        return getModel().getSubName(item);
    }

    // ViewModel
    @Override
    public void parse(String path) {
        getModel().parse(path);
    }

    @Override
    public boolean isCollection() {
        return getModel().isCollection();
    }

    @Override
    public void setCollectionItem(int position) {
        getModel().setCollectionItem(position);
    }
}
