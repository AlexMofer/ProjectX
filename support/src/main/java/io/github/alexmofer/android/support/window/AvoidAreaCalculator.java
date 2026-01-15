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
package io.github.alexmofer.android.support.window;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import androidx.annotation.GravityInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.util.TypedValueCompat;
import androidx.core.view.DisplayCutoutCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.alexmofer.android.support.function.FunctionPInt;
import io.github.alexmofer.android.support.function.FunctionRInt;

/**
 * 避让区域计算器
 * Created by Alex on 2026/1/14.
 */
public final class AvoidAreaCalculator extends ViewModel {
    public static final int LEFT = 1;
    public static final int TOP = 1 << 1;
    public static final int RIGHT = 1 << 2;
    public static final int BOTTOM = 1 << 3;
    public static final int HORIZONTAL = LEFT | RIGHT;
    public static final int VERTICAL = TOP | BOTTOM;
    public static final int ALL = HORIZONTAL | VERTICAL;

    private final MutableLiveData<WindowInsetsCompat> mWindowInsets =
            new MutableLiveData<>(new WindowInsetsCompat(null));
    private final OnApplyWindowInsetsListener mListener = this::onApplyWindowInsets;
    private View mView;

    @NonNull
    public static AvoidAreaCalculator getInstance(@NonNull ViewModelStoreOwner owner,
                                                  @Nullable View view) {
        final AvoidAreaCalculator calculator =
                new ViewModelProvider(owner).get(AvoidAreaCalculator.class);
        calculator.onStart(view);
        return calculator;
    }

    @NonNull
    public static AvoidAreaCalculator getInstance(@NonNull FragmentActivity activity) {
        return getInstance(activity, activity.getWindow().getDecorView());
    }

    @NonNull
    public static AvoidAreaCalculator getInstance(@NonNull DialogFragment fragment) {
        View view = null;
        final Dialog dialog = fragment.getDialog();
        if (dialog != null) {
            final Window window = dialog.getWindow();
            if (window != null) {
                view = window.getDecorView();
            }
        }
        return getInstance(fragment, view);
    }

    private void onStart(@Nullable View view) {
        if (view == null || mView == view) {
            return;
        }
        if (mView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mView, null);
        }
        mView = view;
        ViewCompat.setOnApplyWindowInsetsListener(mView, mListener);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mView = null;
    }

    @NonNull
    private WindowInsetsCompat onApplyWindowInsets(@NonNull View v,
                                                   @NonNull WindowInsetsCompat windowInsets) {
        mWindowInsets.setValue(new WindowInsetsCompat(windowInsets));
        return windowInsets;
    }

    /**
     * 计算
     *
     * @param owner      LifecycleOwner
     * @param calculator 计算器
     */
    public void calculate(@NonNull LifecycleOwner owner, @NonNull Calculator calculator) {
        mWindowInsets.observe(owner, calculator::onCalculate);
    }

    /**
     * 计算
     * <p>不会处理布局方向，请在应用时自行根据 View 布局方向进行处理
     *
     * @param owner    LifecycleOwner
     * @param edge     待计算的边
     * @param type     要处理的裁剪类型
     * @param callback 回调，未处理布局方向
     */
    public void calculate(@NonNull LifecycleOwner owner,
                          @WindowInsetsCompat.Type.InsetsType int type,
                          @Edge int edge, @NonNull Callback callback) {
        calculate(owner, windowInsets -> {
            final Insets insets = windowInsets.getInsets(type);
            final int left = (edge & LEFT) == LEFT ? insets.left : 0;
            final int top = (edge & TOP) == TOP ? insets.top : 0;
            final int right = (edge & RIGHT) == RIGHT ? insets.right : 0;
            final int bottom = (edge & BOTTOM) == BOTTOM ? insets.bottom : 0;
            callback.onChanged(left, top, right, bottom);
        });
    }

    /**
     * 计算
     * <p>有些计算条件可能会处理布局方向
     *
     * @param owner      LifecycleOwner
     * @param conditions 计算条件
     * @param callback   回调
     */
    public void calculate(@NonNull LifecycleOwner owner,
                          @NonNull Conditions conditions,
                          @NonNull Callback callback) {
        calculate(owner, windowInsets -> {
            final int[] result = conditions.calculate(windowInsets);
            if (result != null) {
                callback.onChanged(result[0], result[1], result[2], result[3]);
            }
        });
    }

    /**
     * 计算顶部
     *
     * @param owner    LifecycleOwner
     * @param callback 回调
     */
    public void calculateTop(@NonNull LifecycleOwner owner, @NonNull FunctionPInt callback) {
        calculate(owner, windowInsets -> {
            final Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout());
            callback.execute(insets.top);
        });
    }

    /**
     * 计算顶部
     *
     * @param owner    LifecycleOwner
     * @param callback 回调
     */
    public void calculateBottom(@NonNull LifecycleOwner owner, @NonNull FunctionPInt callback) {
        calculate(owner, windowInsets -> {
            final Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout() // 一般底部不会挖孔，此处包含也没有问题
                            | WindowInsetsCompat.Type.ime());
            callback.execute(insets.bottom);
        });
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {LEFT, TOP, RIGHT, BOTTOM, HORIZONTAL, VERTICAL, ALL})
    public @interface Edge {
    }

    /**
     * 计算器
     */
    public interface Calculator {

        /**
         * 计算
         *
         * @param windowInsets 窗口裁剪
         */
        void onCalculate(@NonNull WindowInsetsCompat windowInsets);
    }

    /**
     * 回调
     */
    public interface Callback {
        /**
         * 发生变化
         * <p>窗口变化并不考虑布局方向，请在应用时自行根据 View 布局方向进行处理
         *
         * @param start  左或右边距，未设置处理时固定返回 0
         * @param top    上边距，未设置处理时固定返回 0
         * @param end    右或左边距，未设置处理时固定返回 0
         * @param bottom 下边距，未设置处理时固定返回 0
         */
        void onChanged(int start, int top, int end, int bottom);
    }

    /**
     * 计算条件
     */
    public interface Condition {

        /**
         * 计算
         *
         * @param windowInsets           窗口裁剪
         * @param layoutDirectionAdapter 布局方向提供者
         */
        int onCalculate(@NonNull WindowInsetsCompat windowInsets,
                        @Nullable FunctionRInt layoutDirectionAdapter);

        static boolean isRTL(@Nullable FunctionRInt layoutDirectionAdapter) {
            return layoutDirectionAdapter != null
                    && layoutDirectionAdapter.execute() == View.LAYOUT_DIRECTION_RTL;
        }
    }

    /**
     * 计算条件集合
     */
    public static final class Conditions {
        private Condition mLeft;
        private Condition mTop;
        private Condition mRight;
        private Condition mBottom;
        private FunctionRInt mLayoutDirectionAdapter;
        private Condition mStart;
        private Condition mEnd;

        public Conditions() {
        }

        public Conditions(@Nullable Condition left, @Nullable Condition top,
                          @Nullable Condition right, @Nullable Condition bottom) {
            mLeft = left;
            mTop = top;
            mRight = right;
            mBottom = bottom;
        }

        public Conditions(@NonNull Context context) {
            mLayoutDirectionAdapter = newLayoutDirectionAdapter(context);
        }

        public static FunctionRInt newLayoutDirectionAdapter(@NonNull View view) {
            return view::getLayoutDirection;
        }

        public static FunctionRInt newLayoutDirectionAdapter(@NonNull Context context) {
            return () -> context.getResources().getConfiguration().getLayoutDirection();
        }

        /**
         * 新建顶部靠左避让条件
         * <p>会智能忽略位于中间的挖孔区域
         *
         * @param context Context
         * @param size    尺寸
         * @return 条件集
         */
        public static Conditions newTopStart(@NonNull Context context, float size) {
            return new Conditions(context)
                    .setTop(NormalCondition.newTop())
                    .setStart(new MaxCondition(
                            NormalCondition.newStartSystemBars(),
                            DisplayCutoutIgnoreByCoverCondition.newTopStart(context, size)));
        }

        /**
         * 新建顶部靠右避让条件
         * <p>会智能忽略位于中间的挖孔区域
         *
         * @param context Context
         * @param size    尺寸
         * @return 条件集
         */
        public static Conditions newTopEnd(@NonNull Context context, float size) {
            return new Conditions(context)
                    .setTop(NormalCondition.newTop())
                    .setEnd(new MaxCondition(
                            NormalCondition.newEndSystemBars(),
                            DisplayCutoutIgnoreByCoverCondition.newTopEnd(context, size)));
        }

        /**
         * 新建顶部栏避让条件
         * <p>会智能忽略位于中间的挖孔区域
         *
         * @param context Context
         * @param size    尺寸
         * @return 条件集
         */
        public static Conditions newTopBar(@NonNull Context context, float size) {
            return new Conditions(context)
                    .setTop(NormalCondition.newTop())
                    .setStart(new MaxCondition(
                            NormalCondition.newStartSystemBars(),
                            DisplayCutoutIgnoreByCoverCondition.newTopStart(context, size)))
                    .setEnd(new MaxCondition(
                            NormalCondition.newEndSystemBars(),
                            DisplayCutoutIgnoreByCoverCondition.newTopEnd(context, size)));
        }

        public Conditions setLeft(@Nullable Condition left) {
            mLeft = left;
            return this;
        }

        public Conditions setTop(@Nullable Condition top) {
            mTop = top;
            return this;
        }

        public Conditions setRight(@Nullable Condition right) {
            mRight = right;
            return this;
        }

        public Conditions setBottom(@Nullable Condition bottom) {
            mBottom = bottom;
            return this;
        }

        public Conditions setLayoutDirectionAdapter(@Nullable FunctionRInt layoutDirectionAdapter) {
            mLayoutDirectionAdapter = layoutDirectionAdapter;
            return this;
        }

        public Conditions setStart(@Nullable Condition start) {
            mStart = start;
            return this;
        }

        public Conditions setEnd(@Nullable Condition end) {
            mEnd = end;
            return this;
        }

        @Nullable
        private int[] calculate(@NonNull WindowInsetsCompat windowInsets) {
            final Condition start;
            final Condition top = mTop;
            final Condition end;
            final Condition bottom = mBottom;
            if (mLayoutDirectionAdapter != null) {
                start = mStart;
                end = mEnd;
            } else {
                start = mLeft;
                end = mRight;
            }
            if (start == null && top == null && end == null && bottom == null) {
                return null;
            }
            final int[] result = new int[4];
            if (start != null) {
                result[0] = start.onCalculate(windowInsets, mLayoutDirectionAdapter);
            }
            if (top != null) {
                result[1] = top.onCalculate(windowInsets, mLayoutDirectionAdapter);
            }
            if (end != null) {
                result[2] = end.onCalculate(windowInsets, mLayoutDirectionAdapter);
            }
            if (bottom != null) {
                result[3] = bottom.onCalculate(windowInsets, mLayoutDirectionAdapter);
            }
            return result;
        }
    }

    /**
     * 最大条件
     */
    public static final class MaxCondition implements Condition {

        private final ArrayList<Condition> mConditions = new ArrayList<>();

        public MaxCondition(Condition c1, Condition c2, Condition... more) {
            mConditions.add(c1);
            mConditions.add(c2);
            mConditions.addAll(Arrays.asList(more));
        }

        @Override
        public int onCalculate(@NonNull WindowInsetsCompat windowInsets,
                               @Nullable FunctionRInt layoutDirectionAdapter) {
            int value = 0;
            for (Condition condition : mConditions) {
                value = Math.max(condition.onCalculate(windowInsets, layoutDirectionAdapter),
                        value);
            }
            return value;
        }
    }

    /**
     * 常规条件
     */
    public static final class NormalCondition implements Condition {

        private final int mType;
        private final int mGravity;

        private NormalCondition(@WindowInsetsCompat.Type.InsetsType int type,
                                @GravityInt int gravity) {
            mType = type;
            mGravity = gravity;
        }

        public static NormalCondition newTop(@WindowInsetsCompat.Type.InsetsType int type) {
            return new NormalCondition(type, Gravity.TOP);
        }

        public static NormalCondition newTop() {
            return newTop(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
        }

        public static NormalCondition newBottom(@WindowInsetsCompat.Type.InsetsType int type) {
            return new NormalCondition(type, Gravity.BOTTOM);
        }

        public static NormalCondition newBottom() {
            return newTop(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout()
                    | WindowInsetsCompat.Type.ime());
        }

        public static NormalCondition newLeft(@WindowInsetsCompat.Type.InsetsType int type) {
            return new NormalCondition(type, Gravity.LEFT);
        }

        public static NormalCondition newLeft() {
            return newLeft(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
        }

        public static NormalCondition newRight(@WindowInsetsCompat.Type.InsetsType int type) {
            return new NormalCondition(type, Gravity.RIGHT);
        }

        public static NormalCondition newRight() {
            return newRight(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
        }

        public static NormalCondition newStart(@WindowInsetsCompat.Type.InsetsType int type) {
            return new NormalCondition(type, Gravity.START);
        }

        public static NormalCondition newStart() {
            return newStart(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
        }

        public static NormalCondition newStartSystemBars() {
            return newStart(WindowInsetsCompat.Type.systemBars());
        }

        public static NormalCondition newEnd(@WindowInsetsCompat.Type.InsetsType int type) {
            return new NormalCondition(type, Gravity.END);
        }

        public static NormalCondition newEnd() {
            return newEnd(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
        }

        public static NormalCondition newEndSystemBars() {
            return newEnd(WindowInsetsCompat.Type.systemBars());
        }

        @Override
        public int onCalculate(@NonNull WindowInsetsCompat windowInsets,
                               @Nullable FunctionRInt layoutDirectionAdapter) {
            final Insets insets = windowInsets.getInsets(mType);
            switch (mGravity) {
                case Gravity.LEFT:
                    return insets.left;
                case Gravity.TOP:
                    return insets.top;
                case Gravity.RIGHT:
                    return insets.right;
                case Gravity.BOTTOM:
                    return insets.bottom;
                case Gravity.START:
                    return (layoutDirectionAdapter != null && layoutDirectionAdapter.execute() == View.LAYOUT_DIRECTION_RTL) ? insets.right : insets.left;
                case Gravity.END:
                    return (layoutDirectionAdapter != null && layoutDirectionAdapter.execute() == View.LAYOUT_DIRECTION_RTL) ? insets.left : insets.right;
            }
            return 0;
        }
    }

    /**
     * 挖孔屏尺寸忽略条件
     * <p>该条件只处理水平方向上的挖孔窗口裁剪
     * <p>挖孔屏大小小于一定尺寸时则会忽略
     */
    public static final class DisplayCutoutIgnoreBySizeCondition implements Condition {

        private final Context mContext;
        private final float mMaxSize;
        private final int mGravity;


        private DisplayCutoutIgnoreBySizeCondition(@NonNull Context context,
                                                   float maxSize,
                                                   @GravityInt int gravity) {
            mContext = context;
            mMaxSize = maxSize;
            mGravity = gravity;
        }

        public static DisplayCutoutIgnoreBySizeCondition newLeft(@NonNull Context context,
                                                                 float maxSize) {
            return new DisplayCutoutIgnoreBySizeCondition(context, maxSize, Gravity.LEFT);
        }

        public static DisplayCutoutIgnoreBySizeCondition newLeft(@NonNull Context context) {
            return newLeft(context, 50);
        }

        public static DisplayCutoutIgnoreBySizeCondition newRight(@NonNull Context context,
                                                                  float maxSize) {
            return new DisplayCutoutIgnoreBySizeCondition(context, maxSize, Gravity.RIGHT);
        }

        public static DisplayCutoutIgnoreBySizeCondition newRight(@NonNull Context context) {
            return newRight(context, 50);
        }

        public static DisplayCutoutIgnoreBySizeCondition newStart(@NonNull Context context,
                                                                  float maxSize) {
            return new DisplayCutoutIgnoreBySizeCondition(context, maxSize, Gravity.START);
        }

        public static DisplayCutoutIgnoreBySizeCondition newStart(@NonNull Context context) {
            return newStart(context, 50);
        }

        public static DisplayCutoutIgnoreBySizeCondition newEnd(@NonNull Context context,
                                                                float maxSize) {
            return new DisplayCutoutIgnoreBySizeCondition(context, maxSize, Gravity.END);
        }

        public static DisplayCutoutIgnoreBySizeCondition newEnd(@NonNull Context context) {
            return newEnd(context, 50);
        }

        @Override
        public int onCalculate(@NonNull WindowInsetsCompat windowInsets,
                               @Nullable FunctionRInt layoutDirectionAdapter) {
            final DisplayCutoutCompat cutout = windowInsets.getDisplayCutout();
            if (cutout == null) {
                return 0;
            }
            boolean ignore = true;
            final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            final float maxSize = TypedValueCompat.dpToPx(mMaxSize, metrics);
            final List<Rect> boundingRects = cutout.getBoundingRects();
            for (Rect rect : boundingRects) {
                if (rect.width() > maxSize || rect.height() > maxSize) {
                    ignore = false;
                    break;
                }
            }
            if (ignore) {
                return 0;
            }
            switch (mGravity) {
                case Gravity.LEFT:
                    return cutout.getSafeInsetLeft();
                case Gravity.RIGHT:
                    return cutout.getSafeInsetRight();
                case Gravity.START:
                    return Condition.isRTL(layoutDirectionAdapter) ? cutout.getSafeInsetRight() : cutout.getSafeInsetLeft();
                case Gravity.END:
                    return Condition.isRTL(layoutDirectionAdapter) ? cutout.getSafeInsetLeft() : cutout.getSafeInsetRight();
            }
            return 0;
        }
    }

    /**
     * 挖孔屏遮盖忽略条件
     * <p>该条件只处理水平方向上的挖孔窗口裁剪
     * <p>一般挖孔屏忽略用于屏幕左右两边避让。比如：位于屏幕中间的小孔挖孔屏，则手机横屏时顶部栏可以忽略掉该屏幕裁剪。
     */
    public static final class DisplayCutoutIgnoreByCoverCondition implements Condition {

        private final Context mContext;
        private final int mViewGravity;
        private final float mViewWidth;
        private final float mViewHeight;
        private final int mGravity;


        private DisplayCutoutIgnoreByCoverCondition(@NonNull Context context,
                                                    @GravityInt int viewGravity,
                                                    float viewWidth, float viewHeight,
                                                    @GravityInt int gravity) {
            mContext = context;
            mViewGravity = viewGravity;
            mViewWidth = viewWidth;
            mViewHeight = viewHeight;
            mGravity = gravity;
        }

        public static DisplayCutoutIgnoreByCoverCondition newTopStart(Context context,
                                                                      float viewWidth, float viewHeight) {
            return new DisplayCutoutIgnoreByCoverCondition(context,
                    Gravity.TOP | Gravity.START, viewWidth, viewHeight, Gravity.START);
        }

        public static DisplayCutoutIgnoreByCoverCondition newTopStart(Context context, float size) {
            return newTopStart(context, size, size);
        }

        public static DisplayCutoutIgnoreByCoverCondition newTopEnd(Context context,
                                                                    float viewWidth, float viewHeight) {
            return new DisplayCutoutIgnoreByCoverCondition(context,
                    Gravity.TOP | Gravity.END, viewWidth, viewHeight, Gravity.END);
        }

        public static DisplayCutoutIgnoreByCoverCondition newTopEnd(Context context, float size) {
            return newTopEnd(context, size, size);
        }

        @Override
        public int onCalculate(@NonNull WindowInsetsCompat windowInsets,
                               @Nullable FunctionRInt layoutDirectionAdapter) {
            final DisplayCutoutCompat cutout = windowInsets.getDisplayCutout();
            if (cutout == null) {
                return 0;
            }
            boolean ignore = true;
            final boolean rtl = Condition.isRTL(layoutDirectionAdapter);
            if (isCover(rtl, cutout.getBoundingRects())) {
                // 挖孔区遮盖住了 View 显示区域，只是预估值，此处不考虑其他避让区域导致的 View 偏移，所以设置较大的容错空间
                switch (mGravity) {
                    case Gravity.LEFT:
                        return cutout.getSafeInsetLeft();
                    case Gravity.RIGHT:
                        return cutout.getSafeInsetRight();
                    case Gravity.START:
                        return rtl ? cutout.getSafeInsetRight() : cutout.getSafeInsetLeft();
                    case Gravity.END:
                        return rtl ? cutout.getSafeInsetLeft() : cutout.getSafeInsetRight();
                }
            }
            return 0;
        }

        private boolean isCover(boolean rtl, @NonNull List<Rect> boundingRects) {
            switch (mViewGravity) {
                case Gravity.TOP | Gravity.LEFT: {
                    final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                    final float viewBottom = TypedValueCompat.dpToPx(mViewHeight, metrics);
                    boolean allBottom = true;
                    for (Rect rect : boundingRects) {
                        if (rect.top < viewBottom) {
                            allBottom = false;
                            break;
                        }
                    }
                    if (allBottom) {
                        // 全在下方，不存在遮盖
                        return false;
                    }
                    final float viewRight = TypedValueCompat.dpToPx(mViewWidth, metrics);
                    boolean allRight = true;
                    for (Rect rect : boundingRects) {
                        if (rect.left < viewRight) {
                            allRight = false;
                            break;
                        }
                    }
                    return !allRight;
                }
                case Gravity.TOP | Gravity.START: {
                    final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                    final float viewBottom = TypedValueCompat.dpToPx(mViewHeight, metrics);
                    boolean allBottom = true;
                    for (Rect rect : boundingRects) {
                        if (rect.top < viewBottom) {
                            allBottom = false;
                            break;
                        }
                    }
                    if (allBottom) {
                        // 全在下方，不存在遮盖
                        return false;
                    }
                    if (rtl) {
                        // 右到左布局，实际显示在右上角
                        final float viewLeft =
                                WindowMetricsHelper.computeCurrentWindowMetrics(mContext).getWidth()
                                        - TypedValueCompat.dpToPx(mViewWidth, metrics);
                        boolean allLeft = true;
                        for (Rect rect : boundingRects) {
                            if (rect.right > viewLeft) {
                                allLeft = false;
                                break;
                            }
                        }
                        return !allLeft;
                    } else {
                        final float viewRight = TypedValueCompat.dpToPx(mViewWidth, metrics);
                        boolean allRight = true;
                        for (Rect rect : boundingRects) {
                            if (rect.left < viewRight) {
                                allRight = false;
                                break;
                            }
                        }
                        return !allRight;
                    }
                }
                case Gravity.TOP | Gravity.RIGHT: {
                    final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                    final float viewBottom = TypedValueCompat.dpToPx(mViewHeight, metrics);
                    boolean allBottom = true;
                    for (Rect rect : boundingRects) {
                        if (rect.top < viewBottom) {
                            allBottom = false;
                            break;
                        }
                    }
                    if (allBottom) {
                        // 全在下方，不存在遮盖
                        return false;
                    }
                    final float viewLeft =
                            WindowMetricsHelper.computeCurrentWindowMetrics(mContext).getWidth()
                                    - TypedValueCompat.dpToPx(mViewWidth, metrics);
                    boolean allLeft = true;
                    for (Rect rect : boundingRects) {
                        if (rect.right > viewLeft) {
                            allLeft = false;
                            break;
                        }
                    }
                    return !allLeft;
                }
                case Gravity.TOP | Gravity.END: {
                    final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                    final float viewBottom = TypedValueCompat.dpToPx(mViewHeight, metrics);
                    boolean allBottom = true;
                    for (Rect rect : boundingRects) {
                        if (rect.top < viewBottom) {
                            allBottom = false;
                            break;
                        }
                    }
                    if (allBottom) {
                        // 全在下方，不存在遮盖
                        return false;
                    }
                    if (rtl) {
                        // 右到左布局，实际显示在左上角
                        final float viewRight = TypedValueCompat.dpToPx(mViewWidth, metrics);
                        boolean allRight = true;
                        for (Rect rect : boundingRects) {
                            if (rect.left < viewRight) {
                                allRight = false;
                                break;
                            }
                        }
                        return !allRight;
                    } else {
                        final float viewLeft =
                                WindowMetricsHelper.computeCurrentWindowMetrics(mContext).getWidth()
                                        - TypedValueCompat.dpToPx(mViewWidth, metrics);
                        boolean allLeft = true;
                        for (Rect rect : boundingRects) {
                            if (rect.right > viewLeft) {
                                allLeft = false;
                                break;
                            }
                        }
                        return !allLeft;
                    }
                }
            }
            return false;
        }
    }
}
