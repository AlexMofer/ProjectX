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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Space;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import io.github.alexmofer.android.support.function.FunctionPObject;
import io.github.alexmofer.android.support.function.FunctionPObjectBoolean;
import io.github.alexmofer.android.support.function.FunctionPObjectObject;
import io.github.alexmofer.android.support.utils.InputMethodManagerUtils;
import io.github.alexmofer.android.support.utils.TypedValueUtils;
import io.github.alexmofer.android.support.window.AvoidAreaCalculator;

/**
 * View 构建器
 * Created by Alex on 2025/6/24.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ViewBuilder {
    private final View mView;

    public ViewBuilder(@NonNull View view) {
        mView = view;
    }

    @NonNull
    public static ViewBuilder newSpace(Context context) {
        return new ViewBuilder(new Space(context));
    }

    @NonNull
    public static ViewBuilder newColor(@NonNull Context context, @ColorInt int color) {
        return new ViewBuilder(new View(context))
                .setBackgroundColor(color);
    }

    public static void setTooltipText(@NonNull View view, @StringRes int tooltipText) {
        if (tooltipText == ResourcesCompat.ID_NULL) {
            ViewCompat.setTooltipText(view, null);
        } else {
            ViewCompat.setTooltipText(view, view.getContext().getString(tooltipText));
        }
    }

    @NonNull
    public View build() {
        return mView;
    }

    @NonNull
    public final <T extends View> T buildCast() {
        //noinspection unchecked
        return (T) mView;
    }

    /**
     * 强制转换
     */
    @NonNull
    public <T extends ViewBuilder> T cast() {
        //noinspection unchecked
        return (T) this;
    }

    /**
     * 挂钩子
     *
     * @param function 方法
     * @return 自身
     */
    public <T extends ViewBuilder> ViewBuilder hook(@Nullable FunctionPObject<T> function) {
        if (function != null) {
            //noinspection unchecked
            function.execute((T) this);
        }
        return this;
    }

    /**
     * 解包装
     *
     * @param function 方法
     * @return 自身
     */
    public <T extends View> ViewBuilder unwrap(@Nullable FunctionPObject<T> function) {
        if (function != null) {
            //noinspection unchecked
            function.execute((T) mView);
        }
        return this;
    }

    public <V extends View, T> ViewBuilder setLiveData(@NonNull LifecycleOwner owner,
                                                       @NonNull LiveData<T> markdown,
                                                       @NonNull FunctionPObjectObject<V, T> function) {
        //noinspection unchecked
        markdown.observe(owner, value -> function.execute((V) mView, value));
        return this;
    }

    public <T extends ViewBuilder> ViewBuilder doOnAttachAndDetach(@Nullable FunctionPObject<T> attach,
                                                                   @Nullable FunctionPObject<T> detach) {
        //noinspection unchecked
        final T target = (T) this;
        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                if (attach != null) {
                    attach.execute(target);
                }
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                if (detach != null) {
                    detach.execute(target);
                }
            }
        });
        return this;
    }

    public <T extends ViewBuilder> ViewBuilder doOnAttach(@Nullable FunctionPObject<T> attach) {
        return doOnAttachAndDetach(attach, null);
    }

    public <T extends ViewBuilder> ViewBuilder doOnDetach(@Nullable FunctionPObject<T> detach) {
        return doOnAttachAndDetach(null, detach);
    }

    public Context getContext() {
        return mView.getContext();
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
        mView.setPadding(mView.getPaddingLeft(), vertical, mView.getPaddingRight(), vertical);
        return this;
    }

    public ViewBuilder setPadding(int padding) {
        mView.setPadding(padding, padding, padding, padding);
        return this;
    }

    public ViewBuilder setPaddingTop(int top) {
        mView.setPadding(mView.getPaddingLeft(), top,
                mView.getPaddingRight(), mView.getPaddingBottom());
        return this;
    }

    public ViewBuilder setPaddingBottom(int bottom) {
        mView.setPadding(mView.getPaddingLeft(), mView.getPaddingTop(),
                mView.getPaddingRight(), bottom);
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

    @NonNull
    private ViewGroup.LayoutParams getOrCreateLayoutParams() {
        final ViewGroup.LayoutParams lp = mView.getLayoutParams();
        if (lp == null) {
            return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return lp;
    }

    protected int dpToPx(float dp) {
        return TypedValueUtils.getDimensionPixelSize(dp, mView.getResources().getDisplayMetrics());
    }

    public ViewBuilder setLayoutParams(int width, int height) {
        final ViewGroup.LayoutParams lp = getOrCreateLayoutParams();
        lp.width = width;
        lp.height = height;
        return setLayoutParams(lp);
    }

    public ViewBuilder setLayoutParams(float width, float height) {
        return setLayoutParams(dpToPx(width), dpToPx(height));
    }

    public ViewBuilder setWidth(int width) {
        final ViewGroup.LayoutParams lp = getOrCreateLayoutParams();
        lp.width = width;
        return setLayoutParams(lp);
    }

    public ViewBuilder setWidth(float dp) {
        return setWidth(dpToPx(dp));
    }

    public ViewBuilder wrapWidth() {
        final ViewGroup.LayoutParams lp = getOrCreateLayoutParams();
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        return setLayoutParams(lp);
    }

    public ViewBuilder matchWidth() {
        final ViewGroup.LayoutParams lp = getOrCreateLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        return setLayoutParams(lp);
    }

    public ViewBuilder setHeight(int height) {
        final ViewGroup.LayoutParams lp = getOrCreateLayoutParams();
        lp.height = height;
        return setLayoutParams(lp);
    }

    public ViewBuilder setHeight(float dp) {
        return setHeight(dpToPx(dp));
    }

    public ViewBuilder wrapHeight() {
        final ViewGroup.LayoutParams lp = getOrCreateLayoutParams();
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        return setLayoutParams(lp);
    }

    public ViewBuilder matchHeight() {
        final ViewGroup.LayoutParams lp = getOrCreateLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        return setLayoutParams(lp);
    }

    public ViewBuilder wrapAll() {
        return setLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public ViewBuilder matchAll() {
        return setLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public ViewBuilder setSize(int size) {
        return setLayoutParams(size, size);
    }

    public ViewBuilder setSize(float dp) {
        return setSize(dpToPx(dp));
    }

    @NonNull
    private ViewGroup.MarginLayoutParams getOrCreateMarginLayoutParams() {
        final ViewGroup.LayoutParams lp = getOrCreateLayoutParams();
        return lp instanceof ViewGroup.MarginLayoutParams ?
                (ViewGroup.MarginLayoutParams) lp : new ViewGroup.MarginLayoutParams(lp);
    }

    public ViewBuilder setMarginLeft(int left) {
        final ViewGroup.MarginLayoutParams mlp = getOrCreateMarginLayoutParams();
        mlp.leftMargin = left;
        return setLayoutParams(mlp);
    }

    public ViewBuilder setMarginTop(int top) {
        final ViewGroup.MarginLayoutParams mlp = getOrCreateMarginLayoutParams();
        mlp.topMargin = top;
        return setLayoutParams(mlp);
    }

    public ViewBuilder setMarginRight(int right) {
        final ViewGroup.MarginLayoutParams mlp = getOrCreateMarginLayoutParams();
        mlp.rightMargin = right;
        return setLayoutParams(mlp);
    }

    public ViewBuilder setMarginBottom(int bottom) {
        final ViewGroup.MarginLayoutParams mlp = getOrCreateMarginLayoutParams();
        mlp.bottomMargin = bottom;
        return setLayoutParams(mlp);
    }

    public ViewBuilder setMarginStart(int start) {
        final ViewGroup.MarginLayoutParams mlp = getOrCreateMarginLayoutParams();
        mlp.setMarginStart(start);
        return setLayoutParams(mlp);
    }

    public ViewBuilder setMarginEnd(int end) {
        final ViewGroup.MarginLayoutParams mlp = getOrCreateMarginLayoutParams();
        mlp.setMarginEnd(end);
        return setLayoutParams(mlp);
    }

    public ViewBuilder setMarginVertical(int vertical) {
        final ViewGroup.MarginLayoutParams mlp = getOrCreateMarginLayoutParams();
        mlp.topMargin = vertical;
        mlp.bottomMargin = vertical;
        return setLayoutParams(mlp);
    }

    public ViewBuilder setMarginHorizontal(int horizontal) {
        final ViewGroup.MarginLayoutParams mlp = getOrCreateMarginLayoutParams();
        mlp.leftMargin = horizontal;
        mlp.rightMargin = horizontal;
        return setLayoutParams(mlp);
    }

    public ViewBuilder setMargin(int left, int top, int right, int bottom) {
        final ViewGroup.MarginLayoutParams mlp = getOrCreateMarginLayoutParams();
        mlp.setMargins(left, top, right, bottom);
        return setLayoutParams(mlp);
    }

    public ViewBuilder setMargin(int margin) {
        return setMargin(margin, margin, margin, margin);
    }

    public ViewBuilder setMarginAndHandleAvoidArea(int left, int top, int right, int bottom,
                                                   @NonNull AvoidAreaCalculator calculator,
                                                   @NonNull LifecycleOwner owner,
                                                   @WindowInsetsCompat.Type.InsetsType int type,
                                                   @AvoidAreaCalculator.Edge int edge) {
        calculator.calculate(owner, type, edge,
                (l, t, r, b) -> setMargin(left + l, top + t, right + r, bottom + b));
        return this;
    }

    public ViewBuilder setMarginAndHandleAvoidArea(int horizontal, int vertical,
                                                   @NonNull AvoidAreaCalculator calculator,
                                                   @NonNull LifecycleOwner owner,
                                                   @WindowInsetsCompat.Type.InsetsType int type,
                                                   @AvoidAreaCalculator.Edge int edge) {
        return setMarginAndHandleAvoidArea(horizontal, vertical, horizontal, vertical,
                calculator, owner, type, edge);
    }

    public ViewBuilder setMarginAndHandleAvoidArea(int margin,
                                                   @NonNull AvoidAreaCalculator calculator,
                                                   @NonNull LifecycleOwner owner,
                                                   @WindowInsetsCompat.Type.InsetsType int type,
                                                   @AvoidAreaCalculator.Edge int edge) {
        return setMarginAndHandleAvoidArea(margin, margin, margin, margin,
                calculator, owner, type, edge);
    }

    public ViewBuilder setMarginAndHandleAvoidArea(@NonNull AvoidAreaCalculator calculator,
                                                   boolean ignoreIME,
                                                   @NonNull LifecycleOwner owner) {
        calculator.calculate(owner, windowInsets -> {
            final Insets insets = windowInsets.getInsets(
                    ignoreIME ?
                            WindowInsetsCompat.Type.systemBars()
                                    | WindowInsetsCompat.Type.displayCutout() :
                            WindowInsetsCompat.Type.systemBars()
                                    | WindowInsetsCompat.Type.displayCutout()
                                    | WindowInsetsCompat.Type.ime());
            setMargin(insets.left, insets.top, insets.right, insets.bottom);
        });
        return this;
    }

    public ViewBuilder setMarginTopAndHandleAvoidArea(int top,
                                                      @NonNull AvoidAreaCalculator calculator,
                                                      @NonNull LifecycleOwner owner) {
        calculator.calculateTop(owner, t -> setMarginTop(top + t));
        return this;
    }

    public ViewBuilder setMarginBottomAndHandleAvoidArea(int bottom,
                                                         @NonNull AvoidAreaCalculator calculator,
                                                         boolean ignoreIME,
                                                         @NonNull LifecycleOwner owner) {
        calculator.calculateBottom(owner, ignoreIME, b -> setMarginBottom(bottom + b));
        return this;
    }

    public ViewBuilder setMarginBottomAndHandleAvoidArea(int bottom,
                                                         @NonNull AvoidAreaCalculator calculator,
                                                         @NonNull LifecycleOwner owner) {
        calculator.calculateBottom(owner, b -> setMarginBottom(bottom + b));
        return this;
    }

    public ViewBuilder setMarginAndHandleAvoidArea(int left, int top, int right, int bottom,
                                                   @NonNull AvoidAreaCalculator calculator,
                                                   @NonNull LifecycleOwner owner,
                                                   @NonNull AvoidAreaCalculator.Conditions conditions) {
        calculator.calculate(owner, conditions,
                (l, t, r, b) -> setMargin(left + l, top + t, right + r, bottom + b));
        return this;
    }

    public ViewBuilder setMarginAndHandleAvoidArea(int horizontal, int vertical,
                                                   @NonNull AvoidAreaCalculator calculator,
                                                   @NonNull LifecycleOwner owner,
                                                   @NonNull AvoidAreaCalculator.Conditions conditions) {
        return setMarginAndHandleAvoidArea(horizontal, vertical, horizontal, vertical,
                calculator, owner, conditions);
    }

    public ViewBuilder setMarginAndHandleAvoidArea(int margin,
                                                   @NonNull AvoidAreaCalculator calculator,
                                                   @NonNull LifecycleOwner owner,
                                                   @NonNull AvoidAreaCalculator.Conditions conditions) {
        return setMarginAndHandleAvoidArea(margin, margin, margin, margin,
                calculator, owner, conditions);
    }

    public ViewBuilder setMarginAndHandleAvoidArea(@NonNull AvoidAreaCalculator calculator,
                                                   @NonNull LifecycleOwner owner,
                                                   @NonNull AvoidAreaCalculator.Conditions conditions) {
        return setMarginAndHandleAvoidArea(0, 0, 0, 0,
                calculator, owner, conditions);
    }

    public ViewBuilder setPaddingAndHandleAvoidArea(int left, int top, int right, int bottom,
                                                    @NonNull AvoidAreaCalculator calculator,
                                                    @NonNull LifecycleOwner owner,
                                                    @NonNull AvoidAreaCalculator.Conditions conditions) {
        calculator.calculate(owner, conditions,
                (l, t, r, b) -> setPadding(left + l, top + t, right + r, bottom + b));
        return this;
    }

    public ViewBuilder setPaddingTopAndHandleAvoidArea(int top,
                                                       @NonNull AvoidAreaCalculator calculator,
                                                       @NonNull LifecycleOwner owner) {
        calculator.calculateTop(owner, t -> setPaddingTop(top + t));
        return this;
    }

    public ViewBuilder setPaddingTopAndHandleAvoidArea(@NonNull AvoidAreaCalculator calculator,
                                                       @NonNull LifecycleOwner owner) {
        calculator.calculateTop(owner, this::setPaddingTop);
        return this;
    }


    public ViewBuilder setPaddingBottomAndHandleAvoidArea(int bottom,
                                                          @NonNull AvoidAreaCalculator calculator,
                                                          boolean ignoreIME,
                                                          @NonNull LifecycleOwner owner) {
        calculator.calculateBottom(owner, ignoreIME, b -> setPaddingBottom(bottom + b));
        return this;
    }

    public ViewBuilder setPaddingBottomAndHandleAvoidArea(@NonNull AvoidAreaCalculator calculator,
                                                          boolean ignoreIME,
                                                          @NonNull LifecycleOwner owner) {
        calculator.calculateBottom(owner, ignoreIME, this::setPaddingBottom);
        return this;
    }

    public ViewBuilder setFrameLayoutLayoutParamsGravity(int gravity) {
        final ViewGroup.LayoutParams lp = getOrCreateLayoutParams();
        final FrameLayout.LayoutParams flp = lp instanceof FrameLayout.LayoutParams ?
                (FrameLayout.LayoutParams) lp : new FrameLayout.LayoutParams(lp);
        flp.gravity = gravity;
        return setLayoutParams(flp);
    }

    public ViewBuilder setLinearLayoutLayoutParamsGravity(int gravity) {
        final ViewGroup.LayoutParams lp = getOrCreateLayoutParams();
        final LinearLayout.LayoutParams flp = lp instanceof LinearLayout.LayoutParams ?
                (LinearLayout.LayoutParams) lp : new LinearLayout.LayoutParams(lp);
        flp.gravity = gravity;
        return setLayoutParams(flp);
    }

    public ViewBuilder setLinearLayoutLayoutParamsWeight(float weight) {
        final ViewGroup.LayoutParams lp = getOrCreateLayoutParams();
        final LinearLayout.LayoutParams flp = lp instanceof LinearLayout.LayoutParams ?
                (LinearLayout.LayoutParams) lp : new LinearLayout.LayoutParams(lp);
        flp.weight = weight;
        return setLayoutParams(flp);
    }

    public ViewBuilder setLinearLayoutLayoutParamsFillRemainingWidth() {
        final ViewGroup.LayoutParams lp = getOrCreateLayoutParams();
        final LinearLayout.LayoutParams flp = lp instanceof LinearLayout.LayoutParams ?
                (LinearLayout.LayoutParams) lp : new LinearLayout.LayoutParams(lp);
        flp.weight = 1;
        flp.width = 0;
        return setLayoutParams(flp);
    }

    public ViewBuilder setLinearLayoutLayoutParamsFillRemainingHeight() {
        final ViewGroup.LayoutParams lp = getOrCreateLayoutParams();
        final LinearLayout.LayoutParams flp = lp instanceof LinearLayout.LayoutParams ?
                (LinearLayout.LayoutParams) lp : new LinearLayout.LayoutParams(lp);
        flp.weight = 1;
        flp.height = 0;
        return setLayoutParams(flp);
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

    public ViewBuilder setOutlineShadowColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mView.setOutlineAmbientShadowColor(color);
            mView.setOutlineSpotShadowColor(color);
        }
        return this;
    }

    public ViewBuilder setVisibility(int visibility) {
        mView.setVisibility(visibility);
        return this;
    }

    public ViewBuilder setVisible(LifecycleOwner owner, LiveData<Boolean> visible,
                                  boolean inverse, @Nullable Transition transition) {
        visible.observe(owner, value -> {
            if (transition != null) {
                final ViewParent parent = mView.getParent();
                if (parent instanceof ViewGroup) {
                    TransitionManager.beginDelayedTransition(((ViewGroup) parent), transition);
                }
            }
            if (inverse) {
                if (Boolean.TRUE.equals(value)) {
                    mView.setVisibility(View.INVISIBLE);
                } else {
                    mView.setVisibility(View.VISIBLE);
                }
            } else {
                if (Boolean.TRUE.equals(value)) {
                    mView.setVisibility(View.VISIBLE);
                } else {
                    mView.setVisibility(View.INVISIBLE);
                }
            }
        });
        return this;
    }

    public ViewBuilder setVisible(LifecycleOwner owner, LiveData<Boolean> visible,
                                  @Nullable Transition transition) {
        return setVisible(owner, visible, false, transition);
    }


    public ViewBuilder setVisible(LifecycleOwner owner, LiveData<Boolean> visible,
                                  boolean inverse) {
        return setVisible(owner, visible, inverse, null);
    }

    public ViewBuilder setVisible(LifecycleOwner owner, LiveData<Boolean> visible) {
        return setVisible(owner, visible, false);
    }

    public ViewBuilder setGone(LifecycleOwner owner, LiveData<Boolean> gone,
                               boolean inverse, @Nullable Transition transition) {
        gone.observe(owner, value -> {
            if (transition != null) {
                final ViewParent parent = mView.getParent();
                if (parent instanceof ViewGroup) {
                    TransitionManager.beginDelayedTransition(((ViewGroup) parent), transition);
                }
            }
            if (inverse) {
                if (Boolean.TRUE.equals(value)) {
                    mView.setVisibility(View.VISIBLE);
                } else {
                    mView.setVisibility(View.GONE);
                }
            } else {
                if (Boolean.TRUE.equals(value)) {
                    mView.setVisibility(View.GONE);
                } else {
                    mView.setVisibility(View.VISIBLE);
                }
            }
        });
        return this;
    }

    public ViewBuilder setGone(LifecycleOwner owner, LiveData<Boolean> gone,
                               @Nullable Transition transition) {
        return setGone(owner, gone, false, transition);
    }


    public ViewBuilder setGone(LifecycleOwner owner, LiveData<Boolean> gone,
                               boolean inverse) {
        return setGone(owner, gone, inverse, null);
    }

    public ViewBuilder setGone(LifecycleOwner owner, LiveData<Boolean> gone) {
        return setGone(owner, gone, false);
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

    public ViewBuilder setEnabled(boolean enabled) {
        mView.setEnabled(enabled);
        return this;
    }

    public ViewBuilder setEnabled(LifecycleOwner owner, LiveData<Boolean> enabled,
                                  @Nullable FunctionPObjectBoolean<View> effective) {
        if (enabled == null) {
            return this;
        }
        if (effective != null) {
            enabled.observe(owner, v ->
                    effective.execute(mView, Boolean.TRUE.equals(v)));
        } else {
            enabled.observe(owner, v -> mView.setEnabled(Boolean.TRUE.equals(v)));
        }
        return this;
    }

    public ViewBuilder setEnabled(LifecycleOwner owner, LiveData<Boolean> enabled) {
        return setEnabled(owner, enabled, null);
    }

    public ViewBuilder setDisable(LifecycleOwner owner, LiveData<Boolean> disable,
                                  @Nullable FunctionPObjectBoolean<View> effective) {
        if (disable == null) {
            return this;
        }
        if (effective != null) {
            disable.observe(owner, v ->
                    effective.execute(mView, !Boolean.TRUE.equals(v)));
        } else {
            disable.observe(owner, v -> mView.setEnabled(!Boolean.TRUE.equals(v)));
        }
        return this;
    }

    public ViewBuilder setDisable(LifecycleOwner owner, LiveData<Boolean> disable) {
        return setDisable(owner, disable, null);
    }

    public ViewBuilder setTooltipText(@Nullable CharSequence tooltipText) {
        ViewCompat.setTooltipText(mView, tooltipText);
        return this;
    }

    public ViewBuilder setTooltipText(@StringRes int tooltipText) {
        setTooltipText(mView, tooltipText);
        return this;
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

    public ViewBuilder setSelected(boolean selected) {
        mView.setSelected(selected);
        return this;
    }

    public ViewBuilder setSelected(LifecycleOwner owner, LiveData<Boolean> selected) {
        if (selected == null) {
            return this;
        }
        selected.observe(owner, v -> mView.setSelected(Boolean.TRUE.equals(v)));
        return this;
    }

    public ViewBuilder setSelected(LifecycleOwner owner, LiveData<Boolean> selected,
                                   boolean reverse) {
        if (selected == null) {
            return this;
        }
        if (reverse) {
            selected.observe(owner, v -> mView.setSelected(!Boolean.TRUE.equals(v)));
        } else {
            selected.observe(owner, v -> mView.setSelected(Boolean.TRUE.equals(v)));
        }
        return this;
    }

    public ViewBuilder setSelected(LifecycleOwner owner, LiveData<?> selected, Object value) {
        if (selected == null) {
            return this;
        }
        selected.observe(owner, v -> mView.setSelected(Objects.equals(value, v)));
        return this;
    }

    public ViewBuilder setTransitionName(@Nullable String transitionName) {
        ViewCompat.setTransitionName(mView, transitionName);
        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    public ViewBuilder blockTouch() {
        mView.setOnTouchListener((v, event) -> true);
        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    public ViewBuilder hideSoftInputWhenTouchUp(boolean clearFocus) {
        mView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                final View focus = v.findFocus();
                if (focus != null) {
                    InputMethodManagerUtils.hideSoftInput(focus, clearFocus);
                }
            }
            return true;
        });
        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    public ViewBuilder hideSoftInputWhenTouchUp() {
        return hideSoftInputWhenTouchUp(false);
    }

    public ViewBuilder setTag(final Object tag) {
        mView.setTag(tag);
        return this;
    }

    public ViewBuilder setTag(@IdRes int key, final Object tag) {
        mView.setTag(key, tag);
        return this;
    }

    public ViewBuilder setOnFocusChangeListener(View.OnFocusChangeListener l) {
        mView.setOnFocusChangeListener(l);
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
