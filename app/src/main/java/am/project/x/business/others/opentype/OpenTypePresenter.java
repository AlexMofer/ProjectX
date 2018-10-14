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

import am.util.mvp.AMPresenter;

/**
 * Presenter
 */
class OpenTypePresenter extends AMPresenter<OpenTypeView, OpenTypeModel> implements
        OpenTypeView, OpenTypeViewModel {

    private final OpenTypeModel mModel = new OpenTypeModel(this);

    OpenTypePresenter(OpenTypeView view) {
        super(view);
    }

    @Override
    protected OpenTypeModel getModel() {
        return mModel;
    }

    // View
    @Override
    public void onOpenTypeLoaded() {
        if (isDetachedFromView())
            return;
        getView().onOpenTypeLoaded();
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
    public String getItemName(int position) {
        return getModel().getItemName(position);
    }

    // ViewModel
    @Override
    public void loadOpenType() {
        getModel().loadOpenType();
    }
}
