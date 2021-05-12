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

import android.content.Context;

import com.am.font.TypefaceCollection;
import com.am.font.TypefaceConfig;
import com.am.font.TypefaceFallback;
import com.am.font.TypefaceItem;
import com.am.mvp.core.MVPModel;

import java.io.File;
import java.util.List;

import am.project.x.ProjectXApplication;
import am.project.x.R;

/**
 * Model
 */
class FontModel extends MVPModel<FontPresenter> implements FontViewModel, FontJob.Callback {

    private TypefaceConfig mConfig;
    private String mDefaultName;
    private List<String> mNames;
    private List<String> mAlias;
    private TypefaceCollection mTypeface;
    private List<TypefaceItem> mItems;// 常规字体
    private List<TypefaceFallback> mFallbacks;// 备用字体

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
    @Override
    public int getTypefaceFallbackCount() {
        return mFallbacks == null ? 0 : mFallbacks.size();
    }

    @Override
    public String getTypefaceName() {
        return mTypeface == null ? null : mTypeface.getName();
    }

    @Override
    public String getCommonTitle() {
        return ProjectXApplication.getInstance().getString(R.string.font_common);
    }

    @Override
    public int getCommonItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public Object getCommonItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    @Override
    public Object getFallback(int position) {
        return mFallbacks == null ? null : mFallbacks.get(position);
    }

    @Override
    public String getFallbackTitle(Object fallback) {
        if (fallback instanceof TypefaceFallback) {
            final TypefaceFallback f = (TypefaceFallback) fallback;
            final Context context = ProjectXApplication.getInstance();
            if (f.getLang() != null && f.getVariant() != null) {
                final String more = "lang=" + f.getLang() + ", variant=" + f.getVariant();
                return context.getString(R.string.font_fallback_more, more);
            } else if (f.getVariant() != null) {
                final String more = "variant=" + f.getVariant();
                return context.getString(R.string.font_fallback_more, more);
            } else if (f.getLang() != null) {
                final String more = "lang=" + f.getLang();
                return context.getString(R.string.font_fallback_more, more);
            } else {
                return context.getString(R.string.font_fallback);
            }
        }
        return null;
    }

    @Override
    public int getFallbackItemCount(Object fallback) {
        if (fallback instanceof TypefaceFallback) {
            final List<TypefaceItem> items = ((TypefaceFallback) fallback).getItems();
            return items == null ? 0 : items.size();
        }
        return 0;
    }

    @Override
    public Object getFallbackItem(Object fallback, int position) {
        if (fallback instanceof TypefaceFallback) {
            final List<TypefaceItem> items = ((TypefaceFallback) fallback).getItems();
            return items == null ? null : items.get(position);
        }
        return null;
    }

    @Override
    public String getTypefaceItemName(Object item) {
        return item instanceof TypefaceItem ? ((TypefaceItem) item).getName() : null;
    }

    @Override
    public String getTypefaceItemInfo(Object item) {
        if (item instanceof TypefaceItem) {
            final TypefaceItem ti = (TypefaceItem) item;
            final StringBuilder builder = new StringBuilder();
            builder.append("Weight:");
            builder.append(ti.getWeight());
            builder.append(" ");
            builder.append("Style:");
            switch (ti.getStyle()) {
                default:
                    builder.append("unknown");
                    break;
                case TypefaceItem.STYLE_NORMAL:
                    builder.append("normal");
                    break;
                case TypefaceItem.STYLE_ITALIC:
                    builder.append("italic");
                    break;
            }
            builder.append(" ");
            final int index = ti.getIndex();
            if (index >= 0) {
                builder.append("Index:");
                builder.append(index);
                builder.append(" ");
            }
            final int axisCount = ti.getAxisCount();
            if (axisCount > 0) {
                builder.append("AxisesCount:");
                builder.append(axisCount);
            }
            return builder.toString();
        }
        return null;
    }

    // ViewModel
    @Override
    public void loadConfig() {
        FontJob.loadConfig(this);
    }

    @Override
    public void loadTypefaceCollection(String nameOrAlias) {
        FontJob.loadTypefaceCollection(this, mConfig, nameOrAlias);
    }

    @Override
    public String getTypefaceItemPath(Object item) {
        if (item instanceof TypefaceItem)
            return TypefaceConfig.getFontsDir() + File.separator + ((TypefaceItem) item).getName();
        return null;
    }

    // Callback
    @Override
    public void onLoadConfigFailure() {
        final FontPresenter presenter = getPresenter();
        if (presenter == null)
            return;
        presenter.onLoadConfigFailure();
    }

    @Override
    public void onLoadConfigSuccess(TypefaceConfig config) {
        mConfig = config;
        mDefaultName = config.getDefaultName();
        mNames = config.getNames();
        mAlias = config.getAliases();
        final FontPresenter presenter = getPresenter();
        if (presenter == null)
            return;
        presenter.onLoadConfigSuccess();
    }

    @Override
    public void onLoadTypefaceCollectionFailure() {
        final FontPresenter presenter = getPresenter();
        if (presenter == null)
            return;
        presenter.onLoadTypefaceCollectionFailure();
    }

    @Override
    public void onLoadTypefaceCollectionSuccess(TypefaceCollection collection) {
        mTypeface = collection;
        mItems = mTypeface.getItems();
        mFallbacks = mTypeface.getFallbacks();
        final FontPresenter presenter = getPresenter();
        if (presenter == null)
            return;
        presenter.onLoadTypefaceCollectionSuccess();
    }
}
