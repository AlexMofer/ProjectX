package am.widget.floatingactionmode.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import am.widget.floatingactionmode.FloatingMenu;
import am.widget.floatingactionmode.FloatingMenuItem;
import am.widget.floatingactionmode.FloatingSubMenu;

/**
 * Created by Xiang Zhicheng on 2018/11/13.
 */
class FloatingMenuItemImpl implements FloatingMenuItem {
    private final Context mContext;
    private int mId;
    private int mOrder;
    private CharSequence mTitle;
    private Drawable mIcon;
    private int mShowType = SHOW_TYPE_AUTO;
    private CharSequence mContentDescription;
    private FloatingSubMenu mSubMenu;

    FloatingMenuItemImpl(Context context) {
        this(context, FloatingMenu.NONE, FloatingMenu.NONE);
    }

    FloatingMenuItemImpl(Context context, int id, int order) {
        mContext = context;
        mId = id;
        mOrder = order;
    }

    @Override
    public int getItemId() {
        return mId;
    }

    @Override
    public int getOrder() {
        return mOrder;
    }

    @Override
    public FloatingMenuItem setTitle(CharSequence title) {
        mTitle = title;
        return this;
    }

    @Override
    public FloatingMenuItem setTitle(int title) {
        mTitle = mContext.getString(title);
        return this;
    }

    @Override
    public CharSequence getTitle() {
        return mTitle;
    }

    @Override
    public FloatingMenuItem setIcon(Drawable icon) {
        mIcon = icon;
        return this;
    }

    @Override
    public FloatingMenuItem setIcon(int iconRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mIcon = mContext.getDrawable(iconRes);
        else
            mIcon = mContext.getResources().getDrawable(iconRes);
        return this;
    }

    @Override
    public Drawable getIcon() {
        return mIcon;
    }

    @Override
    public FloatingMenuItem setShowType(int type) {
        mShowType = type;
        return this;
    }

    @Override
    public int getShowType() {
        return mShowType;
    }

    @Override
    public FloatingMenuItem setContentDescription(CharSequence contentDescription) {
        mContentDescription = contentDescription;
        return this;
    }

    @Override
    public FloatingMenuItem setContentDescription(int contentDescription) {
        mContentDescription = mContext.getString(contentDescription);
        return this;
    }

    @Override
    public CharSequence getContentDescription() {
        return mContentDescription;
    }

    @Override
    public boolean hasSubMenu() {
        if (mSubMenu == null)
            return false;
        if (mSubMenu instanceof FloatingSubMenuCommonImpl)
            return mSubMenu.size() > 0;
        else if (mSubMenu instanceof FloatingSubMenuCustomImpl)
            return mSubMenu.getCustomView() != null;
        return false;
    }

    @Override
    public FloatingSubMenu getSubMenu() {
        return mSubMenu;
    }

    @Override
    public FloatingSubMenu setSubMenu() {
        mSubMenu = new FloatingSubMenuCommonImpl(mContext);
        return mSubMenu;
    }

    @Override
    public FloatingSubMenu setSubMenu(View custom) {
        mSubMenu = new FloatingSubMenuCustomImpl();
        return mSubMenu.setCustomView(custom);
    }
}