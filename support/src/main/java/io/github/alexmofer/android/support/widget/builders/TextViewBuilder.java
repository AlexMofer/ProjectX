/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.widget.builders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.MovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.TypefaceCompat;
import androidx.core.widget.TextViewCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.Objects;
import java.util.function.Consumer;

import io.github.alexmofer.android.support.other.StringAdapter;
import io.github.alexmofer.android.support.other.StringResource;
import io.github.alexmofer.android.support.utils.ColorUtils;
import io.github.alexmofer.android.support.utils.DrawableUtils;
import io.github.alexmofer.android.support.utils.InputMethodManagerUtils;
import io.github.alexmofer.android.support.utils.TextViewUtils;

/**
 * TextView 构建器
 * Created by Alex on 2025/6/24.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class TextViewBuilder extends ViewBuilder {
    private final TextView mView;

    public TextViewBuilder(@NonNull TextView view) {
        super(view);
        this.mView = view;
    }

    public TextViewBuilder(@NonNull Context context) {
        this(build(context));
    }

    @NonNull
    public static TextView build(@NonNull Context context) {
        return new AppCompatTextView(context, null, 0);
    }

    public static void setWeight(@NonNull TextView view, @Nullable Typeface family,
                                 @IntRange(from = 1, to = 1000) int weight, boolean italic) {
        view.setTypeface(TypefaceCompat.create(view.getContext(), family, weight, italic));
    }

    public static void setWeight(TextView view, @IntRange(from = 1, to = 1000) int weight) {
        setWeight(view, null, weight, false);
    }

    public static void setTextStyle(@NonNull TextView view, @Nullable Typeface family, int style) {
        view.setTypeface(TypefaceCompat.create(view.getContext(), family, style));
    }

    public static void setTextStyle(@NonNull TextView view, int style) {
        setTextStyle(view, null, style);
    }

    @NonNull
    @Override
    public TextView build() {
        return buildCast();
    }

    public TextViewBuilder setMinWidth(int minPixels) {
        mView.setMinWidth(minPixels);
        return this;
    }

    public TextViewBuilder setMinHeight(int minPixels) {
        mView.setMinHeight(minPixels);
        return this;
    }

    public TextViewBuilder setSingleLine() {
        mView.setSingleLine();
        return this;
    }

    public TextViewBuilder setMaxLines(int maxLines) {
        mView.setMaxLines(maxLines);
        return this;
    }

    public TextViewBuilder setEllipsize(TextUtils.TruncateAt where) {
        mView.setEllipsize(where);
        return this;
    }

    public TextViewBuilder setTextSize(float size) {
        mView.setTextSize(size);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public TextViewBuilder setAutoSizeTextTypeUniformWithConfiguration(int autoSizeMinTextSize,
                                                                       int autoSizeMaxTextSize,
                                                                       int autoSizeStepGranularity,
                                                                       int unit) {
        mView.setAutoSizeTextTypeUniformWithConfiguration(autoSizeMinTextSize, autoSizeMaxTextSize,
                autoSizeStepGranularity, unit);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public TextViewBuilder setAutoSizeTextTypeUniformWithConfiguration(int autoSizeMinTextSize,
                                                                       int autoSizeMaxTextSize) {
        return setAutoSizeTextTypeUniformWithConfiguration(autoSizeMinTextSize, autoSizeMaxTextSize, 1, TypedValue.COMPLEX_UNIT_SP);
    }

    public TextViewBuilder setTypeface(@Nullable Typeface tf) {
        mView.setTypeface(tf);
        return this;
    }

    public TextViewBuilder setWeight(@IntRange(from = 1, to = 1000) int weight) {
        setWeight(mView, weight);
        return this;
    }

    public TextViewBuilder setTextStyle(int style) {
        setTextStyle(mView, style);
        return this;
    }

    public TextViewBuilder setGravity(int gravity) {
        mView.setGravity(gravity);
        return this;
    }

    public TextViewBuilder setTextColor(@ColorInt int color) {
        mView.setTextColor(color);
        return this;
    }

    public TextViewBuilder setTextColor(ColorStateList colors) {
        mView.setTextColor(colors);
        return this;
    }

    public TextViewBuilder setText(@StringRes int resid) {
        if (resid == ResourcesCompat.ID_NULL) {
            mView.setText(null);
        } else {
            mView.setText(resid);
        }
        return this;
    }

    public TextViewBuilder setText(CharSequence text) {
        mView.setText(text);
        return this;
    }

    public TextViewBuilder setText(CharSequence text, boolean autoGone) {
        mView.setText(text);
        if (autoGone) {
            mView.setVisibility(text == null ? View.GONE : View.VISIBLE);
        }
        return this;
    }

    public TextViewBuilder setText(StringResource text) {
        StringResource.setText(mView, text);
        return this;
    }

    public TextViewBuilder setText(StringAdapter text) {
        mView.setText(text == null ? null : text.getString(mView.getContext()));
        return this;
    }

    public TextViewBuilder setText(LifecycleOwner owner, LiveData<?> text,
                                   boolean autoGone) {
        text.observe(owner, value -> {
            if (value == null) {
                setText((CharSequence) null);
                if (autoGone) {
                    mView.setVisibility(View.GONE);
                }
            } else {
                if (value instanceof CharSequence) {
                    setText((CharSequence) value);
                } else if (value instanceof Integer) {
                    setText((Integer) value);
                } else if (value instanceof StringResource) {
                    setText((StringResource) value);
                } else if (value instanceof StringAdapter) {
                    setText((StringAdapter) value);
                } else {
                    setText(value.toString());
                }
                if (autoGone) {
                    mView.setVisibility(View.VISIBLE);
                }
            }
        });
        return this;
    }

    public TextViewBuilder setTextByToString(@NonNull LifecycleOwner owner,
                                             @NonNull LiveData<?> text) {
        text.observe(owner, value -> setText(value == null ? null : value.toString()));
        return this;
    }

    public TextViewBuilder setText(LifecycleOwner owner, LiveData<?> text) {
        return setText(owner, text, false);
    }

    public TextViewBuilder setRefreshTextSignal(@NonNull LifecycleOwner owner, @NonNull LiveData<?> signal,
                                                @NonNull Consumer<TextView> callback) {
        signal.observe(owner, unused -> callback.accept(mView));
        return this;
    }

    public TextViewBuilder setInputType(int type) {
        mView.setInputType(type);
        return this;
    }

    public TextViewBuilder setHint(CharSequence hint) {
        mView.setHint(hint);
        return this;
    }

    public TextViewBuilder setHint(@StringRes int resid) {
        mView.setHint(resid);
        return this;
    }

    public TextViewBuilder setHintTextColor(@ColorInt int color) {
        mView.setHintTextColor(color);
        return this;
    }

    public TextViewBuilder setHintTextColor(ColorStateList colors) {
        mView.setHintTextColor(colors);
        return this;
    }

    public TextViewBuilder setMaxWidth(int maxPixels) {
        mView.setMaxWidth(maxPixels);
        return this;
    }

    public TextViewBuilder setCompoundDrawablePadding(int pad) {
        mView.setCompoundDrawablePadding(pad);
        return this;
    }

    public TextViewBuilder setCompoundDrawablesRelativeWithIntrinsicBounds(@DrawableRes int start,
                                                                           @DrawableRes int top, @DrawableRes int end, @DrawableRes int bottom) {
        mView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        return this;
    }

    public TextViewBuilder setCompoundDrawableStart(@DrawableRes int start) {
        return setCompoundDrawablesRelativeWithIntrinsicBounds(start,
                ResourcesCompat.ID_NULL, ResourcesCompat.ID_NULL, ResourcesCompat.ID_NULL);
    }

    public TextViewBuilder setCompoundDrawableTop(@DrawableRes int top) {
        return setCompoundDrawablesRelativeWithIntrinsicBounds(ResourcesCompat.ID_NULL,
                top, ResourcesCompat.ID_NULL, ResourcesCompat.ID_NULL);
    }

    public TextViewBuilder setCompoundDrawableTop(Drawable top) {
        return setCompoundDrawablesRelativeWithIntrinsicBounds(null, top, null, null);
    }

    public TextViewBuilder setCompoundDrawableEnd(@DrawableRes int end) {
        return setCompoundDrawablesRelativeWithIntrinsicBounds(ResourcesCompat.ID_NULL,
                ResourcesCompat.ID_NULL, end, ResourcesCompat.ID_NULL);
    }

    public TextViewBuilder setCompoundDrawableEnd(Drawable end) {
        return setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, end, null);
    }

    public TextViewBuilder setCompoundDrawableEnd(LifecycleOwner owner, LiveData<Integer> end) {
        end.observe(owner, value ->
                mView.setCompoundDrawablesRelativeWithIntrinsicBounds(ResourcesCompat.ID_NULL,
                        ResourcesCompat.ID_NULL,
                        Objects.requireNonNullElse(value, ResourcesCompat.ID_NULL),
                        ResourcesCompat.ID_NULL));
        return this;
    }

    public TextViewBuilder setCompoundDrawableBottom(@DrawableRes int bottom) {
        return setCompoundDrawablesRelativeWithIntrinsicBounds(ResourcesCompat.ID_NULL,
                ResourcesCompat.ID_NULL, ResourcesCompat.ID_NULL, bottom);
    }

    public TextViewBuilder setCompoundDrawablesRelativeWithIntrinsicBounds(@Nullable Drawable start,
                                                                           @Nullable Drawable top,
                                                                           @Nullable Drawable end,
                                                                           @Nullable Drawable bottom) {
        mView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        return this;
    }

    public TextViewBuilder setCompoundDrawableStart(@Nullable Drawable start) {
        return setCompoundDrawablesRelativeWithIntrinsicBounds(start, null, null, null);
    }

    public TextViewBuilder setCompoundDrawableTintList(@Nullable ColorStateList tint) {
        TextViewCompat.setCompoundDrawableTintList(mView, tint);
        return this;
    }

    public TextViewBuilder setLineHeight(int unit, float lineHeight) {
        TextViewCompat.setLineHeight(mView, unit, lineHeight);
        return this;
    }

    public TextViewBuilder setMovementMethod(MovementMethod movement) {
        mView.setMovementMethod(movement);
        return this;
    }

    public TextViewBuilder setImeOptions(int imeOptions) {
        mView.setImeOptions(imeOptions);
        return this;
    }

    public TextViewBuilder setTextIsSelectable(boolean selectable) {
        mView.setTextIsSelectable(selectable);
        return this;
    }

    public TextViewBuilder setLinkTextColor(@ColorInt int color) {
        mView.setLinkTextColor(color);
        return this;
    }

    public TextViewBuilder setHighlightColor(@ColorInt int color) {
        mView.setHighlightColor(color);
        return this;
    }

    public TextViewBuilder setAutoMarquee() {
        TextViewUtils.setAutoMarquee(mView);
        return this;
    }

    public TextViewBuilder showSoftInputWhenAttached(long delayMillis) {
        InputMethodManagerUtils.setAutoFocus(mView, delayMillis);
        return this;
    }

    public TextViewBuilder addOnTextChangedListener(Consumer<Editable> consumer) {
        TextViewUtils.addOnTextChangedListener(mView, consumer);
        return this;
    }

    public TextViewBuilder setOnEditorActionListener(TextView.OnEditorActionListener l) {
        mView.setOnEditorActionListener(l);
        return this;
    }

    /**
     * 设置选择器颜色
     * 包括：光标、输入手柄、左右选择手柄、选中高亮（该颜色的 0.5的不透明）
     * 注意：Android Q 之前无效果
     *
     * @param color 颜色
     */
    public TextViewBuilder setTextSelectTint(@ColorInt int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return this;
        }
        final Drawable cursor = mView.getTextCursorDrawable();
        if (cursor != null) {
            DrawableUtils.setTint(cursor, color);
        }
        final Drawable selectHandle = mView.getTextSelectHandle();
        if (selectHandle != null) {
            DrawableUtils.setTint(selectHandle, color);
        }
        final Drawable selectHandleLeft = mView.getTextSelectHandleLeft();
        if (selectHandleLeft != null) {
            DrawableUtils.setTint(selectHandleLeft, color);
        }
        final Drawable selectHandleRight = mView.getTextSelectHandleRight();
        if (selectHandleRight != null) {
            DrawableUtils.setTint(selectHandleRight, color);
        }
        mView.setHighlightColor(ColorUtils.getColor(color, 0.3f));
        return this;
    }

    public TextViewBuilder setSelectAllOnFocus(boolean selectAllOnFocus) {
        mView.setSelectAllOnFocus(selectAllOnFocus);
        return this;
    }

    public TextViewBuilder setMaxLength(int maxLength) {
        TextViewUtils.setMaxLength(mView, maxLength);
        return this;
    }

    public TextViewBuilder setAllCaps(boolean allCaps) {
        mView.setAllCaps(allCaps);
        return this;
    }
}
