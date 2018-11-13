package am.widget.floatingactionmode.impl;

import android.content.Context;

import java.util.ArrayList;

import am.widget.floatingactionmode.FloatingMenu;
import am.widget.floatingactionmode.FloatingMenuItem;

/**
 * Created by Xiang Zhicheng on 2018/11/13.
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