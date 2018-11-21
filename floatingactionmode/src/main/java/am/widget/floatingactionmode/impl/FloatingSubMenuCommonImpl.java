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
package am.widget.floatingactionmode.impl;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import am.widget.floatingactionmode.FloatingMenuItem;
import am.widget.floatingactionmode.FloatingSubMenu;

/**
 * 常规次级菜单实现
 * Created by Alex on 2018/11/21.
 */
final class FloatingSubMenuCommonImpl implements FloatingSubMenu {

    private final Context mContext;
    private CharSequence mTitle;
    private final ArrayList<FloatingMenuItem> mItems = new ArrayList<>();

    FloatingSubMenuCommonImpl(Context context, CharSequence title) {
        mContext = context;
        mTitle = title;
    }

    @Override
    public FloatingSubMenu setTitle(CharSequence title) {
        mTitle = title;
        return this;
    }

    @Override
    public FloatingSubMenu setTitle(int title) {
        mTitle = mContext.getString(title);
        return this;
    }

    @Override
    public CharSequence getTitle() {
        return mTitle;
    }

    @Override
    public FloatingMenuItem add(CharSequence title) {
        final FloatingMenuItem item = new FloatingSubMenuItemImpl(mContext).setTitle(title);
        mItems.add(item);
        return item;
    }

    @Override
    public FloatingMenuItem add(int title) {
        final FloatingMenuItem item = new FloatingSubMenuItemImpl(mContext).setTitle(title);
        mItems.add(item);
        return item;
    }

    @Override
    public FloatingMenuItem add(int id, int order, CharSequence title) {
        final FloatingMenuItem item = new FloatingSubMenuItemImpl(mContext, id, order);
        mItems.add(item.setTitle(title));
        return item;
    }

    @Override
    public FloatingMenuItem add(int id, int order, int title) {
        final FloatingMenuItem item = new FloatingSubMenuItemImpl(mContext, id, order);
        mItems.add(item.setTitle(title));
        return item;
    }

    @Override
    public void removeItem(int id) {
        int count = mItems.size();
        for (int i = 0; i < count; i++) {
            final FloatingMenuItem item = mItems.get(i);
            if (item.getId() == id)
                mItems.remove(i);
            i--;
            count = mItems.size();
        }
    }

    @Override
    public void clear() {
        mItems.clear();
    }

    @Override
    public FloatingMenuItem findItem(int id) {
        for (FloatingMenuItem item : mItems) {
            if (item.getId() == id)
                return item;
        }
        return null;
    }

    @Override
    public int size() {
        return mItems.size();
    }

    @Override
    public FloatingMenuItem getItem(int index) {
        return mItems.get(index);
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @Override
    public FloatingSubMenu setCustomView(View view) {
        throw new UnsupportedOperationException("Common sub menu not support set custom view.");
    }

    @Override
    public View getCustomView() {
        throw new UnsupportedOperationException("Common sub menu not support get custom view.");
    }
}
