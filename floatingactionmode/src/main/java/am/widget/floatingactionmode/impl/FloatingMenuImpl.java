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
import android.text.TextUtils;

import java.util.ArrayList;

import am.widget.floatingactionmode.FloatingMenu;
import am.widget.floatingactionmode.FloatingMenuItem;

/**
 * 菜单实现
 * Created by Alex on 2018/11/21.
 */
final class FloatingMenuImpl implements FloatingMenu {

    private final Context mContext;
    private final ArrayList<FloatingMenuItem> mItems = new ArrayList<>();
    private final ArrayList<FloatingMenuItem> mNeedShow = new ArrayList<>();

    FloatingMenuImpl(Context context) {
        mContext = context;
    }

    @Override
    public FloatingMenuItem add(CharSequence title) {
        final FloatingMenuItem item = new FloatingMenuItemImpl(mContext).setTitle(title);
        mItems.add(item);
        return item;
    }

    @Override
    public FloatingMenuItem add(int title) {
        final FloatingMenuItem item = new FloatingMenuItemImpl(mContext).setTitle(title);
        mItems.add(item);
        return item;
    }

    @Override
    public FloatingMenuItem add(int id, int order, CharSequence title) {
        final FloatingMenuItem item = new FloatingMenuItemImpl(mContext, id, order);
        mItems.add(item.setTitle(title));
        return item;
    }

    @Override
    public FloatingMenuItem add(int id, int order, int title) {
        final FloatingMenuItem item = new FloatingMenuItemImpl(mContext, id, order);
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

    void prepareToShow() {
        mNeedShow.clear();
        mNeedShow.addAll(mItems);
    }

    boolean hasMoreMenu() {
        return !mNeedShow.isEmpty();
    }

    FloatingMenuItem pullItemOut() {
        return mNeedShow.isEmpty() ? null : mNeedShow.remove(0);
    }

    void pushItemBack(FloatingMenuItem item) {
        mNeedShow.add(0, item);
    }

    FloatingMenuItem getSubItem(FloatingMenuItem saved) {
        if (saved == null)
            return null;
        final int id = saved.getId();
        final int order = saved.getOrder();
        final CharSequence title = saved.getTitle();
        for (FloatingMenuItem item : mItems) {
            if (item == null)
                continue;
            if (id == item.getId() && order == item.getOrder() &&
                    TextUtils.equals(title, item.getTitle()))
                return item;
        }
        return null;
    }
}