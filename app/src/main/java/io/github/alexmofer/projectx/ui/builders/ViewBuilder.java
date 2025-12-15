package io.github.alexmofer.projectx.ui.builders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * View 构建器
 * Created by Alex on 2025/6/24.
 */
public class ViewBuilder {

    private final View mView;

    public ViewBuilder(View view) {
        this.mView = view;
    }

    public ViewBuilder(Context context) {
        this(new View(context));
    }

    public static ViewBuilder newDivider(Context context, @ColorInt int color) {
        return new ViewBuilder(new View(context))
                .setBackgroundColor(color);
    }

    public static ViewBuilder newDivider(Context context, Drawable background) {
        return new ViewBuilder(new View(context))
                .setBackground(background);
    }

    /**
     * 强制转换
     */
    public <T extends ViewBuilder> T cast() {
        //noinspection unchecked
        return (T) this;
    }

    /**
     * 构建
     *
     * @return View
     */
    public View build() {
        return mView;
    }

    /**
     * 修改未定义属性
     *
     * @param consumer 回调View
     */
    public <T> ViewBuilder changeAttribute(Consumer<T> consumer) {
        //noinspection unchecked
        consumer.accept((T) mView);
        return this;
    }

    /**
     * 设置背景色
     *
     * @param color 背景色
     */
    public ViewBuilder setBackgroundColor(@ColorInt int color) {
        mView.setBackgroundColor(color);
        return this;
    }

    public ViewBuilder setBackground(Drawable background) {
        mView.setBackground(background);
        return this;
    }

    public ViewBuilder setPadding(int left, int top, int right, int bottom) {
        mView.setPadding(left, top, right, bottom);
        return this;
    }

    public ViewBuilder setPaddingHorizontal(int horizontal) {
        mView.setPadding(horizontal, mView.getPaddingTop(), horizontal, mView.getPaddingBottom());
        return this;
    }

    public ViewBuilder setPaddingVertical(int vertical) {
        mView.setPaddingRelative(mView.getPaddingStart(), vertical, mView.getPaddingEnd(), vertical);
        return this;
    }

    public ViewBuilder setPaddingRelative(int start, int top, int end, int bottom) {
        mView.setPaddingRelative(start, top, end, bottom);
        return this;
    }

    public ViewBuilder setPadding(int padding) {
        mView.setPadding(padding, padding, padding, padding);
        return this;
    }

    public ViewBuilder setPaddingTop(int top) {
        mView.setPaddingRelative(mView.getPaddingStart(), top,
                mView.getPaddingEnd(), mView.getPaddingBottom());
        return this;
    }

    public ViewBuilder setMinimumHeight(int minHeight) {
        mView.setMinimumHeight(minHeight);
        return this;
    }

    public ViewBuilder setMinimumWidth(int minWidth) {
        mView.setMinimumWidth(minWidth);
        return this;
    }

    public ViewBuilder setLayoutParams(ViewGroup.LayoutParams params) {
        mView.setLayoutParams(params);
        return this;
    }

    public ViewBuilder setAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
        mView.setAlpha(alpha);
        return this;
    }

    public ViewBuilder setOnClickListener(@Nullable View.OnClickListener l) {
        mView.setOnClickListener(l);
        return this;
    }

    public ViewBuilder setOutlineProvider(ViewOutlineProvider provider) {
        mView.setOutlineProvider(provider);
        return this;
    }

    public ViewBuilder setClipToOutline(boolean clipToOutline) {
        mView.setClipToOutline(clipToOutline);
        return this;
    }

    public ViewBuilder setElevation(float elevation) {
        mView.setElevation(elevation);
        return this;
    }

    public ViewBuilder setVisibility(int visibility) {
        mView.setVisibility(visibility);
        return this;
    }

    public ViewBuilder setVisible(LifecycleOwner owner, LiveData<Boolean> visible, boolean gone) {
        visible.observe(owner, value -> mView.setVisibility(Boolean.TRUE.equals(value) ?
                View.VISIBLE : (gone ? View.GONE : View.INVISIBLE)));
        return this;
    }

    public ViewBuilder setGone(LifecycleOwner owner, LiveData<Boolean> value, boolean inverse) {
        value.observe(owner, v -> {
            if (inverse) {
                if (Boolean.TRUE.equals(v)) {
                    mView.setVisibility(View.VISIBLE);
                } else {
                    mView.setVisibility(View.GONE);
                }
            } else {
                if (Boolean.FALSE.equals(v)) {
                    mView.setVisibility(View.VISIBLE);
                } else {
                    mView.setVisibility(View.GONE);
                }
            }
        });
        return this;
    }

    public ViewBuilder setDuplicateParentStateEnabled(boolean enabled) {
        mView.setDuplicateParentStateEnabled(enabled);
        return this;
    }

    public ViewBuilder setActivated(boolean activated) {
        mView.setActivated(activated);
        return this;
    }

    public ViewBuilder setActivated(LifecycleOwner owner, LiveData<Boolean> value) {
        value.observe(owner, v -> mView.setActivated(Boolean.TRUE.equals(v)));
        return this;
    }

    public ViewBuilder setActivated(LifecycleOwner owner, LiveData<Boolean> value, boolean inverse) {
        value.observe(owner, v -> {
            if (inverse) {
                mView.setActivated(!Boolean.TRUE.equals(v));
            } else {
                mView.setActivated(Boolean.TRUE.equals(v));
            }
        });
        return this;
    }

    public ViewBuilder setTooltipText(@Nullable CharSequence tooltipText) {
        ViewCompat.setTooltipText(mView, tooltipText);
        return this;
    }

    public ViewBuilder setTooltipText(@StringRes int tooltipText) {
        return setTooltipText(mView.getContext().getString(tooltipText));
    }

    public ViewBuilder setOnTouchListener(View.OnTouchListener l) {
        mView.setOnTouchListener(l);
        return this;
    }

    public ViewBuilder setForeground(Drawable foreground) {
        mView.setForeground(foreground);
        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    public ViewBuilder setTouchInterceptor(@NonNull BooleanSupplier condition,
                                           @Nullable Consumer<View> callback) {
        mView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (condition.getAsBoolean()) {
                    if (callback != null) {
                        callback.accept(v);
                    }
                    return true;
                }
            }
            return false;
        });
        return this;
    }

    public ViewBuilder setTouchInterceptor(@NonNull TouchInterceptor interceptor) {

        return setTouchInterceptor(interceptor.getCondition(), interceptor.getCallback());
    }

    public ViewBuilder setId(@IdRes int id) {
        mView.setId(id);
        return this;
    }

    public interface TouchInterceptor {

        /**
         * 获取拦截条件
         *
         * @return 拦截条件
         */
        @NonNull
        BooleanSupplier getCondition();

        /**
         * 获取拦截回调
         *
         * @return 拦截回调
         */
        @Nullable
        Consumer<View> getCallback();
    }
}
