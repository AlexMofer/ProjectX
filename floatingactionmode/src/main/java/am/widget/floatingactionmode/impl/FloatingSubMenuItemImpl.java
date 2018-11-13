package am.widget.floatingactionmode.impl;

import android.content.Context;
import android.view.View;

import am.widget.floatingactionmode.FloatingSubMenu;

/**
 * Created by Xiang Zhicheng on 2018/11/13.
 */
final class FloatingSubMenuItemImpl extends FloatingMenuItemImpl {

    FloatingSubMenuItemImpl(Context context) {
        super(context);
    }

    FloatingSubMenuItemImpl(Context context, int id, int order) {
        super(context, id, order);
    }

    @Override
    public boolean hasSubMenu() {
        return false;
    }

    @Override
    public FloatingSubMenu getSubMenu() {
        return null;
    }

    @Override
    public FloatingSubMenu setSubMenu() {
        throw new UnsupportedOperationException("Sub menu item not support set sub menu.");
    }

    @Override
    public FloatingSubMenu setSubMenu(View custom) {
        throw new UnsupportedOperationException("Sub menu item not support set sub menu.");
    }
}