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

import android.support.annotation.Nullable;

import am.util.mvp.AMModel;
import am.util.opentype.OpenType;
import am.util.opentype.OpenTypeCollection;
import am.util.opentype.tables.NamingTable;

/**
 * Model
 */
class OpenTypeModel extends AMModel<OpenTypePresenter> implements OpenTypeViewModel,
        OpenTypeJob.Callback {

    private boolean mCollection = false;
    private OpenType mFont;
    private OpenTypeCollection mFonts;

    OpenTypeModel(OpenTypePresenter presenter) {
        super(presenter);
    }

    // AdapterViewModel
    @Override
    public int getItemCount() {
        if (mFont != null) {
            return mFont.getTablesSize() + 1;
        }
        return mFonts == null ? 0 : 1;
    }

    @Override
    public Object getItem(int position) {
        if (mFont != null) {
            if (position == 0)
                return mFont;
            return null;// TODO
        }
        return mFonts;
    }

    @Override
    public String getItemLabel(Object item) {
        // TODO
        if (item instanceof OpenType || item instanceof OpenTypeCollection)
            return "基础信息";
        return "表";
    }

    @Override
    public String getItemInfo(Object item) {
        // TODO
        return "kjvnoiushueojoiajfknkjneonnfjndjnfuihsugfjio  jdeindnioasj jijdijieji";
    }

    // PickerViewModel
    @Override
    public int getSubCount() {
        return mFonts == null ? 0 : mFonts.getOpenTypesCount();
    }

    @Override
    public Object getSubItem(int position) {
        return mFonts == null ? null : mFonts.getOpenType(position);
    }

    @Override
    public String getSubName(Object item) {
        if (item instanceof OpenType) {
            final OpenType font = (OpenType) item;
            final NamingTable naming = font.getNamingTable();
            if (naming != null)
                return naming.getFullName();
        }
        return null;
    }

    // ViewModel
    @Override
    public void parse(String path) {
        OpenTypeJob.parse(this, path);
    }

    @Override
    public boolean isCollection() {
        return mCollection;
    }

    @Override
    public void setCollectionItem(int position) {
        if (mFonts == null)
            return;
        mFont = mFonts.getOpenType(position);
    }

    // Callback
    @Override
    public void onParseFailure() {
        if (isDetachedFromPresenter())
            return;
        getPresenter().onParseFailure();
    }

    @Override
    public void onParseSuccess(boolean isCollection,
                               @Nullable OpenType font, @Nullable OpenTypeCollection fonts) {
        mCollection = isCollection;
        mFont = font;
        mFonts = fonts;
        if (isDetachedFromPresenter())
            return;
        getPresenter().onParseSuccess(isCollection);
    }
}
