package io.github.alexmofer.projectx.ui.builders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.MovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.TypefaceCompat;
import androidx.core.widget.TextViewCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.Objects;

import io.github.alexmofer.android.support.other.StringResource;

/**
 * TextView 构建器
 * Created by Alex on 2025/6/24.
 */
public class TextViewBuilder extends ViewBuilder {

    private final TextView mView;

    public TextViewBuilder(TextView view) {
        super(view);
        this.mView = view;
    }

    public TextViewBuilder(Context context) {
        this(new AppCompatTextView(context, null, 0));
    }

    public static void setTypefaceBold(TextView view) {
        view.setTypeface(TypefaceCompat.create(view.getContext(), null, 600, false));
    }

    /**
     * 新建渐变文本
     *
     * @param context    Context
     * @param startX     开始坐标点X轴
     * @param startY     开始坐标点Y轴
     * @param startColor 开始颜色
     * @param endX       结束坐标X轴
     * @param endY       结束坐标Y轴
     * @param endColor   结束颜色
     */
    public static TextViewBuilder newGradient(@NonNull Context context,
                                              float startX, float startY, int startColor,
                                              float endX, float endY, int endColor) {
        return new TextViewBuilder(new GradientTextView(context,
                startX, startY, startColor, endX, endY, endColor));
    }

    @Override
    public TextView build() {
        return mView;
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

    public TextViewBuilder setAutoSizeTextTypeUniformWithConfiguration(int autoSizeMinTextSize,
                                                                       int autoSizeMaxTextSize,
                                                                       int autoSizeStepGranularity,
                                                                       int unit) {
        mView.setAutoSizeTextTypeUniformWithConfiguration(autoSizeMinTextSize, autoSizeMaxTextSize,
                autoSizeStepGranularity, unit);
        return this;
    }

    public TextViewBuilder setTypeface(@Nullable Typeface tf) {
        mView.setTypeface(tf);
        return this;
    }

    public TextViewBuilder setTypefaceBold(Context context) {
        return setTypeface(TypefaceCompat.create(context, null, 600, false));
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
        mView.setText(resid);
        return this;
    }

    public TextViewBuilder setText(CharSequence text) {
        mView.setText(text);
        return this;
    }

    public TextViewBuilder setText(StringResource text) {
        StringResource.setText(mView, text);
        return this;
    }

    public TextViewBuilder setText(LifecycleOwner owner, LiveData<StringResource> text,
                                   boolean autoGone) {
        text.observe(owner, value -> {
            if (value == null) {
                mView.setText(null);
                if (autoGone) {
                    mView.setVisibility(View.GONE);
                }
            } else {
                StringResource.setText(mView, value);
                if (autoGone) {
                    mView.setVisibility(View.VISIBLE);
                }
            }
        });
        return this;
    }

    public TextViewBuilder setText(LifecycleOwner owner, LiveData<StringResource> text) {
        return setText(owner, text, false);
    }

    public TextViewBuilder setInputType(int type) {
        mView.setInputType(type);
        return this;
    }

    public TextViewBuilder setHint(CharSequence hint) {
        mView.setHint(hint);
        return this;
    }

    public TextViewBuilder setHintTextColor(@ColorInt int color) {
        mView.setHintTextColor(color);
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

    /**
     * 文字颜色渐变
     */
    @SuppressLint("ViewConstructor")
    public static class GradientTextView extends AppCompatTextView {

        private int mStartColor;
        private float mStartX;
        private float mStartY;
        private int mEndColor;
        private float mEndX;
        private float mEndY;
        private boolean mGradientEnable = true;

        public GradientTextView(@NonNull Context context,
                                float startX, float startY, int startColor,
                                float endX, float endY, int endColor) {
            super(context);
            mStartX = startX;
            mStartY = startY;
            mStartColor = startColor;
            mEndX = endX;
            mEndY = endY;
            mEndColor = endColor;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (mGradientEnable) {
                setShader();
            } else {
                getPaint().setShader(null);
            }
        }

        private void setShader() {
            final TextPaint paint = getPaint();
            final int width = getWidth();
            final int height = getHeight();
            final int paddingLeft = getPaddingLeft();
            final int paddingTop = getPaddingTop();
            final int paddingRight = getPaddingRight();
            final int paddingBottom = getPaddingBottom();
            final int contentWidth = width - paddingLeft - paddingRight;
            final int contentHeight = height - paddingTop - paddingBottom;
            final float x0 = paddingLeft + contentWidth * mStartX;
            final float y0 = paddingTop + contentHeight * mStartY;
            final float x1 = paddingLeft + contentWidth * mEndX;
            final float y1 = paddingBottom + contentHeight * mEndY;
            paint.setShader(new LinearGradient(x0, y0, x1, y1, mStartColor, mEndColor,
                    Shader.TileMode.CLAMP));
        }

        /**
         * 设置是否开启渐变
         *
         * @param enable 是否开启渐变
         */
        public void setGradientEnable(boolean enable) {
            if (mGradientEnable == enable) {
                return;
            }
            mGradientEnable = enable;
            if (mGradientEnable) {
                final int w = getWidth();
                final int h = getHeight();
                if (w > 0 || h > 0) {
                    setShader();
                    invalidate();
                }
            } else {
                getPaint().setShader(null);
                invalidate();
            }
        }

        /**
         * 设置渐变
         *
         * @param startX     开始坐标点X轴
         * @param startY     开始坐标点Y轴
         * @param startColor 开始颜色
         * @param endX       结束坐标X轴
         * @param endY       结束坐标Y轴
         * @param endColor   结束颜色
         */
        public void setGradient(float startX, float startY, int startColor,
                                float endX, float endY, int endColor) {
            mStartX = startX;
            mStartY = startY;
            mStartColor = startColor;
            mEndX = endX;
            mEndY = endY;
            mEndColor = endColor;
            if (mGradientEnable) {
                final int w = getWidth();
                final int h = getHeight();
                if (w > 0 || h > 0) {
                    setShader();
                    invalidate();
                }
            }
        }
    }
}
