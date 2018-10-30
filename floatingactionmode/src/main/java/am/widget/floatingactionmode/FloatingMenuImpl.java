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
package am.widget.floatingactionmode;

import android.content.Context;

import java.util.ArrayList;

/**
 * 菜单实现
 * Created by Alex on 2018/10/24.
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
    public FloatingMenuItem add(int titleRes) {
        final FloatingMenuItem item = new FloatingMenuItemImpl(mContext).setTitle(titleRes);
        mItems.add(item);
        return item;
    }

    @Override
    public FloatingMenuItem add(int itemId, int order, CharSequence title) {
        final FloatingMenuItem item = new FloatingMenuItemImpl(mContext, itemId, order);
        mItems.add(item.setTitle(title));
        return item;
    }

    @Override
    public FloatingMenuItem add(int itemId, int order, int titleRes) {
        final FloatingMenuItem item = new FloatingMenuItemImpl(mContext, itemId, order);
        mItems.add(item.setTitle(titleRes));
        return item;
    }

    @Override
    public void removeItem(int id) {
        int count = mItems.size();
        for (int i = 0; i < count; i++) {
            final FloatingMenuItem item = mItems.get(i);
            if (item.getItemId() == id)
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
            if (item.getItemId() == id)
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
}
