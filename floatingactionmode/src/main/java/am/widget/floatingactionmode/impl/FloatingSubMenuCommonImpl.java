package am.widget.floatingactionmode.impl;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import am.widget.floatingactionmode.FloatingMenuItem;
import am.widget.floatingactionmode.FloatingSubMenu;

/**
 * Created by Xiang Zhicheng on 2018/11/13.
 */
final class FloatingSubMenuCommonImpl implements FloatingSubMenu {

    private final Context mContext;
    private final ArrayList<FloatingMenuItem> mItems = new ArrayList<>();

    FloatingSubMenuCommonImpl(Context context) {
        mContext = context;
    }

    @Override
    public FloatingMenuItem add(CharSequence title) {
        final FloatingMenuItem item = new FloatingSubMenuItemImpl(mContext).setTitle(title);
        mItems.add(item);
        return item;
    }

    @Override
    public FloatingMenuItem add(int titleRes) {
        final FloatingMenuItem item = new FloatingSubMenuItemImpl(mContext).setTitle(titleRes);
        mItems.add(item);
        return item;
    }

    @Override
    public FloatingMenuItem add(int itemId, int order, CharSequence title) {
        final FloatingMenuItem item = new FloatingSubMenuItemImpl(mContext, itemId, order);
        mItems.add(item.setTitle(title));
        return item;
    }

    @Override
    public FloatingMenuItem add(int itemId, int order, int titleRes) {
        final FloatingMenuItem item = new FloatingSubMenuItemImpl(mContext, itemId, order);
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

    @Override
    public boolean isCustomMenu() {
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
