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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import am.widget.floatingactionmode.FloatingMenuItem;
import am.widget.floatingactionmode.R;

/**
 * 菜单子项布局
 * Created by Alex on 2018/10/18.
 */
final class MenuItemView extends LinearLayout {

    private final int mPadding;
    private final int mPaddingEdge;
    private final ImageView mIcon;
    private final TextView mText;
    private final int mPaddingText;
    private final int mSize;
    private boolean mFirst = false;
    private boolean mLast = false;

    MenuItemView(Context context) {
        super(context);
        final Resources resources = context.getResources();
        int size = resources.getDimensionPixelOffset(R.dimen.floatingActionModeItemSize);
        int padding = resources.getDimensionPixelOffset(R.dimen.floatingActionModeItemPadding);
        int paddingEdge = resources.getDimensionPixelOffset(
                R.dimen.floatingActionModeItemPaddingEdge);
        int paddingIcon = resources.getDimensionPixelOffset(
                R.dimen.floatingActionModeItemPaddingIcon);
        int widthIcon = resources.getDimensionPixelOffset(
                R.dimen.floatingActionModeItemIconWidth);
        int paddingText = resources.getDimensionPixelOffset(
                R.dimen.floatingActionModeItemTextPaddingIcon);
        int textColor = resources.getColor(R.color.floatingActionModeItemTextColor);
        float textSize = resources.getDimension(R.dimen.floatingActionModeItemTextSize);
        @SuppressLint("CustomViewStyleable") final TypedArray custom =
                context.obtainStyledAttributes(R.styleable.FloatingActionMode);
        size = custom.getDimensionPixelOffset(
                R.styleable.FloatingActionMode_floatingActionModeItemSize, size);
        final Drawable background = custom.getDrawable(
                R.styleable.FloatingActionMode_floatingActionModeItemBackground);
        if (background == null) {
            final TypedArray a = context.obtainStyledAttributes(
                    new int[]{android.R.attr.selectableItemBackground});
            setBackgroundResource(a.getResourceId(0, 0));
            a.recycle();
        } else {
            setBackgroundDrawable(background);
        }
        padding = custom.getDimensionPixelOffset(
                R.styleable.FloatingActionMode_floatingActionModeItemPadding, padding);
        paddingEdge = custom.getDimensionPixelOffset(
                R.styleable.FloatingActionMode_floatingActionModeItemPaddingEdge, paddingEdge);
        paddingIcon = custom.getDimensionPixelOffset(
                R.styleable.FloatingActionMode_floatingActionModeItemPaddingIcon, paddingIcon);
        widthIcon = custom.getDimensionPixelOffset(
                R.styleable.FloatingActionMode_floatingActionModeItemIconWidth, widthIcon);
        paddingText = custom.getDimensionPixelOffset(
                R.styleable.FloatingActionMode_floatingActionModeItemTextPaddingIcon, paddingText);
        final int textAppearance = custom.getResourceId(
                R.styleable.FloatingActionMode_floatingActionModeItemTextAppearance, 0);
        textColor = custom.getColor(
                R.styleable.FloatingActionMode_floatingActionModeItemTextColor, textColor);
        textSize = custom.getDimension(
                R.styleable.FloatingActionMode_floatingActionModeItemTextSize, textSize);
        custom.recycle();


        setOrientation(HORIZONTAL);
        setMinimumWidth(size);
        setMinimumHeight(size);

        mPadding = padding;
        mPaddingEdge = paddingEdge;
        mPaddingText = paddingText;
        mSize = size;
        updatePadding();

        mIcon = new ImageView(context);
        mIcon.setBackgroundDrawable(null);
        mIcon.setFocusable(false);
        mIcon.setFocusableInTouchMode(false);
        mIcon.setPadding(paddingIcon, 0, paddingIcon, 0);
        mIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        if (Build.VERSION.SDK_INT >= 16)
            mIcon.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(mIcon, new LayoutParams(widthIcon, size));

        mText = new TextView(context);
        if (textAppearance != 0)
            mText.setTextAppearance(context, textAppearance);
        else {
            final TypedArray a = context.obtainStyledAttributes(
                    new int[]{android.R.attr.textAppearanceListItemSmall});
            mText.setTextAppearance(context, a.getResourceId(0, 0));
            a.recycle();
        }
        mText.setBackgroundDrawable(null);
        mText.setEllipsize(TextUtils.TruncateAt.END);
        mText.setFocusable(false);
        mText.setFocusableInTouchMode(false);
        mText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        mText.setGravity(Gravity.CENTER);
        mText.setSingleLine();
        mText.setTextColor(textColor);
        mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        if (Build.VERSION.SDK_INT >= 16)
            mText.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(mText, new LayoutParams(LayoutParams.WRAP_CONTENT, size));

    }

    void setData(FloatingMenuItem item) {
        mText.setEllipsize(null);
        switch (item.getShowType()) {
            case FloatingMenuItem.SHOW_TYPE_AUTO:
                if (!TextUtils.isEmpty(item.getTitle()) && item.getIcon() != null)
                    setAll(item);
                else if (item.getIcon() != null)
                    setIconOnly(item);
                else
                    setTextOnly(item);
                break;
            case FloatingMenuItem.SHOW_TYPE_TEXT:
                setTextOnly(item);
                break;
            case FloatingMenuItem.SHOW_TYPE_ICON:
                setIconOnly(item);
                break;
            case FloatingMenuItem.SHOW_TYPE_ALL:
                setAll(item);
                break;
        }
        setContentDescription(item.getContentDescription());
    }

    private void setTextOnly(FloatingMenuItem item) {
        mIcon.setVisibility(GONE);
        mText.setVisibility(VISIBLE);
        mText.setPadding(0, 0, 0, 0);
        mText.setText(item.getTitle());
    }

    private void setIconOnly(FloatingMenuItem item) {
        mIcon.setVisibility(VISIBLE);
        mIcon.setImageDrawable(item.getIcon());
        mText.setVisibility(GONE);
    }

    private void setAll(FloatingMenuItem item) {
        mIcon.setVisibility(VISIBLE);
        mIcon.setImageDrawable(item.getIcon());
        mText.setVisibility(VISIBLE);
        if (Build.VERSION.SDK_INT >= 16)
            mText.setPaddingRelative(mPaddingText, 0, 0, 0);
        else
            mText.setPadding(mPaddingText, 0, 0, 0);
        mText.setText(item.getTitle());
    }

    void setFirst(boolean first) {
        if (mFirst == first)
            return;
        mFirst = first;
        updatePadding();
    }

    void setLast(boolean last) {
        if (mLast == last)
            return;
        mLast = last;
        updatePadding();
    }

    int getSize() {
        return mSize;
    }

    private void updatePadding() {
        if (mFirst && mLast)
            setPaddingCompat(mPaddingEdge, 0, mPaddingEdge, 0);
        else if (mFirst)
            setPaddingCompat(mPaddingEdge, 0, mPadding, 0);
        else if (mLast)
            setPaddingCompat(mPadding, 0, mPaddingEdge, 0);
        else
            setPaddingCompat(mPadding, 0, mPadding, 0);
    }

    @SuppressWarnings("SameParameterValue")
    private void setPaddingCompat(int start, int top, int end, int bottom) {
        if (Build.VERSION.SDK_INT >= 17)
            setPaddingRelative(start, top, end, bottom);
        else
            setPadding(start, top, end, bottom);
    }
}
