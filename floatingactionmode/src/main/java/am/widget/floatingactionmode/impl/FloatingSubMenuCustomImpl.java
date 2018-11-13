package am.widget.floatingactionmode.impl;

import android.view.View;

import am.widget.floatingactionmode.FloatingMenuItem;
import am.widget.floatingactionmode.FloatingSubMenu;

/**
 * Created by Xiang Zhicheng on 2018/11/13.
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