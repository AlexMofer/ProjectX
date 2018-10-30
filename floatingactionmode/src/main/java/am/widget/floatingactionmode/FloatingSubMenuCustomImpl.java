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

import android.view.View;

/**
 * 次级自定义菜单
 * Created by Alex on 2018/10/29.
 */
final class FloatingSubMenuCustomImpl implements FloatingSubMenu {

    private View mView;

    @Override
    public FloatingMenuItem add(CharSequence title) {
        throw new UnsupportedOperationException("Custom sub menu not support add.");
    }

    @Override
    public FloatingMenuItem add(int titleRes) {
        throw new UnsupportedOperationException("Custom sub menu not support add.");
    }

    @Override
    public FloatingMenuItem add(int itemId, int order, CharSequence title) {
        throw new UnsupportedOperationException("Custom sub menu not support add.");
    }

    @Override
    public FloatingMenuItem add(int itemId, int order, int titleRes) {
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
    public boolean isCustomMenu() {
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
