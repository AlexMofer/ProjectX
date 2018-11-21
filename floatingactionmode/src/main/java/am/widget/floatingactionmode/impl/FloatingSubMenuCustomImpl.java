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

import am.widget.floatingactionmode.FloatingMenuItem;
import am.widget.floatingactionmode.FloatingSubMenu;

/**
 * 自定义菜单实现
 * Created by Alex on 2018/11/21.
 */
final class FloatingSubMenuCustomImpl implements FloatingSubMenu {

    private final Context mContext;
    private CharSequence mTitle;
    private View mView;

    FloatingSubMenuCustomImpl(Context context, CharSequence title) {
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
        throw new UnsupportedOperationException("Custom sub menu not support add.");
    }

    @Override
    public FloatingMenuItem add(int title) {
        throw new UnsupportedOperationException("Custom sub menu not support add.");
    }

    @Override
    public FloatingMenuItem add(int id, int order, CharSequence title) {
        throw new UnsupportedOperationException("Custom sub menu not support add.");
    }

    @Override
    public FloatingMenuItem add(int id, int order, int title) {
        throw new UnsupportedOperationException("Custom sub menu not support add.");
    }

    @Override
    public void removeItem(int id) {
        throw new UnsupportedOperationException("Custom sub menu not support remove item.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Custom sub menu not support clear.");
    }

    @Override
    public FloatingMenuItem findItem(int id) {
        throw new UnsupportedOperationException("Custom sub menu not support find item.");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Custom sub menu not support size.");
    }

    @Override
    public FloatingMenuItem getItem(int index) {
        throw new UnsupportedOperationException("Custom sub menu not support get item.");
    }

    @Override
    public boolean isCustom() {
        return true;
    }

    @Override
    public FloatingSubMenu setCustomView(View view) {
        mView = view;
        return this;
    }

    @Override
    public View getCustomView() {
        return mView;
    }
}