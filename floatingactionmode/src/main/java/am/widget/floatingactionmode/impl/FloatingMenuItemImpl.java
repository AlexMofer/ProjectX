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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import am.widget.floatingactionmode.FloatingMenu;
import am.widget.floatingactionmode.FloatingMenuItem;
import am.widget.floatingactionmode.FloatingSubMenu;

/**
 * 菜单子项实现
 * Created by Alex on 2018/11/21.
 */
class FloatingMenuItemImpl implements FloatingMenuItem {
    private final Context mContext;
    private int mId;
    private int mOrder;
    private CharSequence mTitle;
    private Drawable mIcon;
    private int mShowType = SHOW_TYPE_AUTO;
    private CharSequence mContentDescription;
    private Object mTag;
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
    public int getId() {
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
    public FloatingMenuItem setIcon(int icon) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mIcon = mContext.getDrawable(icon);
        else
            mIcon = mContext.getResources().getDrawable(icon);
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
    public Object getTag() {
        return mTag;
    }

    @Override
    public void setTag(Object tag) {
        mTag = tag;
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
        mSubMenu = new FloatingSubMenuCommonImpl(mContext, mTitle);
        return mSubMenu;
    }

    @Override
    public FloatingSubMenu setSubMenu(View custom) {
        mSubMenu = new FloatingSubMenuCustomImpl(mContext, mTitle);
        return mSubMenu.setCustomView(custom);
    }
}