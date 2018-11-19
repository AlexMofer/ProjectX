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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import am.widget.floatingactionmode.FloatingActionMode;
import am.widget.floatingactionmode.FloatingMenuItem;
import am.widget.floatingactionmode.FloatingSubMenu;
import am.widget.floatingactionmode.R;


/**
 * 次级菜单面板
 * Created by Alex on 2018/10/23.
 */
final class SubLayout extends LinearLayout {

    private final TextView mTitle;
    private final SubListView mList;
    private final int mListMaxHeight;
    private final LayoutParams mParams;
    private View mCustom;
    private boolean mBind = false;

    SubLayout(Context context) {
        super(context);
        final Resources resources = context.getResources();
        int size = resources.getDimensionPixelOffset(R.dimen.floatingActionModeItemSize);
        int textColor = resources.getColor(R.color.floatingActionModeSubTitleTextColor);
        float textSize = resources.getDimension(R.dimen.floatingActionModeSubTitleTextSize);
        @SuppressLint("CustomViewStyleable") final TypedArray custom =
                context.obtainStyledAttributes(R.styleable.FloatingActionMode);
        size = custom.getDimensionPixelOffset(
                R.styleable.FloatingActionMode_floatingActionModeItemSize, size);
        final int textAppearance = custom.getResourceId(
                R.styleable.FloatingActionMode_floatingActionModeSubTitleTextAppearance, 0);
        textColor = custom.getColor(
                R.styleable.FloatingActionMode_floatingActionModeSubTitleTextColor, textColor);
        textSize = custom.getDimension(
                R.styleable.FloatingActionMode_floatingActionModeSubTitleTextSize, textSize);
        custom.recycle();
        setOrientation(VERTICAL);
        mTitle = new TextView(context);
        if (textAppearance != 0)
            mTitle.setTextAppearance(context, textAppearance);
        else {
            final TypedArray a = context.obtainStyledAttributes(
                    new int[]{android.R.attr.textAppearanceListItemSmall});
            mTitle.setTextAppearance(context, a.getResourceId(0, 0));
            a.recycle();
        }
        mTitle.setBackgroundDrawable(null);
        mTitle.setEllipsize(TextUtils.TruncateAt.END);
        mTitle.setFocusable(false);
        mTitle.setFocusableInTouchMode(false);
        mTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setSingleLine();
        mTitle.setTextColor(textColor);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mTitle.setMinimumHeight(size);
        mTitle.setPadding(size, 0, size, 0);
        if (Build.VERSION.SDK_INT >= 16)
            mTitle.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        mTitle.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mList = new SubListView(context);
        mListMaxHeight = mList.getMaxHeight();
        mParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        mParams.weight = 1;
    }

    boolean hasSubMenu(FloatingMenuImpl menu) {
        final int count = menu.size();
        for (int i = 0; i < count; i++) {
            final FloatingMenuItem item = menu.getItem(i);
            if (!item.hasSubMenu())
                continue;
            return true;
        }
        return false;
    }

    void setData(FloatingMenuItem item, int maxWidth, int maxHeight, Size size,
                 FloatingActionMode mode) {
        removeAllViews();
        int width;
        int height;
        final FloatingSubMenu sub = item.getSubMenu();
        mTitle.setText(sub.getTitle());
        mTitle.measure(View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST));
        final int titleWidth = mTitle.getMeasuredWidth();
        final int titleHeight = mTitle.getMeasuredHeight();
        if (sub.isCustomMenu()) {
            final View custom = sub.getCustomView();
            custom.measure(
                    View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST));
            width = Math.max(titleWidth, custom.getMeasuredWidth());
            height = titleHeight + custom.getMeasuredHeight();
            mCustom = custom;
            if (custom instanceof FloatingSubMenu.OnAttachStateChangeListener)
                ((FloatingSubMenu.OnAttachStateChangeListener) custom)
                        .onViewAttachedToFloatingActionMode(mode);
        } else {
            final int listMaxWidth = mList.setData(sub);
            mList.measure(
                    View.MeasureSpec.makeMeasureSpec(listMaxWidth, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(mListMaxHeight, View.MeasureSpec.AT_MOST));
            width = Math.max(titleWidth, mList.getMeasuredWidth());
            height = titleHeight + mList.getMeasuredHeight();
            mCustom = null;
        }
        size.set(width, height);
        mBind = true;
    }

    void clear(FloatingActionMode mode) {
        mBind = false;
        removeAllViews();
        mTitle.setText(null);
        mList.clear();
        if (mCustom instanceof FloatingSubMenu.OnAttachStateChangeListener)
            ((FloatingSubMenu.OnAttachStateChangeListener) mCustom)
                    .onViewDetachedFromFloatingActionMode(mode);
        mCustom = null;
    }

    void setReverse(boolean reverse) {
        if (!mBind)
            return;
        if (mCustom == null) {
            if (reverse) {
                addView(mList, mParams);
                addView(mTitle);
            } else {
                addView(mTitle);
                addView(mList, mParams);
            }
        } else {
            final ViewParent parent = mCustom.getParent();
            if (parent instanceof ViewGroup)
                ((ViewGroup) parent).removeView(mCustom);
            if (reverse) {
                addView(mCustom, mParams);
                addView(mTitle);
            } else {
                addView(mTitle);
                addView(mCustom, mParams);
            }
        }
    }

    boolean isBind() {
        return mBind;
    }

    void awakenScrollBar() {
        mList.awakenScrollBar();
    }

    public interface OnSubListener {
        void onSubItemClick(FloatingMenuItem item);
    }
}
