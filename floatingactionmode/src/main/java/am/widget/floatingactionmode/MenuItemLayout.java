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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 菜单子项布局
 * Created by Alex on 2018/10/18.
 */
@SuppressLint("ViewConstructor")
final class MenuItemLayout extends LinearLayout {

    private static final int DIP_PADDING = 11;
    private static final int DIP_ICON_PADDING = 12;
    private static final int DIP_ICON_WIDTH = 24;
    private static final int DIP_TEXT_PADDING = 11;
    private static final int DIP_TEXT_TEXT_SIZE = 14;
    private final int mPadding;
    private final int mPaddingEdge;
    private final ImageView mIcon;
    private final TextView mText;
    private final int mPaddingText;
    private boolean mFirst = false;
    private boolean mLast = false;

    MenuItemLayout(Context context, int height, boolean light) {
        super(context);
        final DisplayMetrics display = context.getResources().getDisplayMetrics();
        setOrientation(HORIZONTAL);
        //noinspection SuspiciousNameCombination
        setMinimumWidth(height);
        setMinimumHeight(height);
        mPadding = TypedValueUtils.complexToDimensionPixelOffset(DIP_PADDING, display);
        mPaddingEdge = (int) (mPadding * 1.5f);
        updatePadding();
        mIcon = new ImageView(context);
        mIcon.setBackgroundDrawable(null);
        mIcon.setFocusable(false);
        mIcon.setFocusableInTouchMode(false);
        if (Build.VERSION.SDK_INT >= 16)
            mIcon.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        final int paddingIcon = TypedValueUtils.complexToDimensionPixelOffset(
                DIP_ICON_PADDING, display);
        mIcon.setPadding(paddingIcon, 0, paddingIcon, 0);
        mIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        addView(mIcon, new LayoutParams(
                TypedValueUtils.complexToDimensionPixelOffset(DIP_ICON_WIDTH, display), height));
        mText = new TextView(context);
        mText.setBackgroundDrawable(null);
        mText.setEllipsize(TextUtils.TruncateAt.END);
        mText.setFocusable(false);
        mText.setFocusableInTouchMode(false);
        mText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        mText.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= 16)
            mText.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        mPaddingText = TypedValueUtils.complexToDimensionPixelOffset(
                DIP_TEXT_PADDING, display);
        mText.setPadding(mPaddingText, 0, mPaddingText, 0);
        mText.setSingleLine();
        final TypedArray a = context.obtainStyledAttributes(
                new int[]{android.R.attr.textAppearanceListItemSmall});
        final int resId = a.getResourceId(0, 0);
        a.recycle();
        if (resId != 0)
            mText.setTextAppearance(context, resId);
        mText.setTextColor(light ? Color.BLACK : Color.WHITE);
        mText.setTextSize(TypedValue.COMPLEX_UNIT_SP, DIP_TEXT_TEXT_SIZE);
        addView(mText, new LayoutParams(LayoutParams.WRAP_CONTENT, height));

    }

    void setLightTheme(boolean light) {
        mText.setTextColor(light ? Color.BLACK : Color.WHITE);
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
