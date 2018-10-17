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

import am.util.mvp.AMModel;

/**
 * Model
 */
class OpenTypeModel extends AMModel<OpenTypePresenter> implements OpenTypeViewModel {

    private boolean mCollection = false;
    OpenTypeModel(OpenTypePresenter presenter) {
        super(presenter);
    }

    // AdapterViewModel
    @Override
    public int getItemCount() {
        return 20;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public String getItemLabel(Object item) {
        return "基础信息";
    }

    @Override
    public String getItemInfo(Object item) {
        return "kjvnoiushueojoiajfknkjneonnfjndjnfuihsugfjio  jdeindnioasj jijdijieji";
    }

    // PickerViewModel
    @Override
    public int getSubCount() {
        return 3;
    }

    @Override
    public Object getSubItem(int position) {
        return null;
    }

    @Override
    public String getSubName(Object item) {
        return "字体名称-------------";
    }

    // ViewModel
    @Override
    public void parse(String path) {
        mCollection = true;
        getPresenter().onParseSuccess(true);
    }

    @Override
    public boolean isCollection() {
        return mCollection;
    }
}
