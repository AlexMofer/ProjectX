package io.github.alexmofer.android.support.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.util.TypedValueCompat;

import io.github.alexmofer.android.support.utils.ContextUtils;
import io.github.alexmofer.android.support.utils.TypedValueUtils;
import io.github.alexmofer.android.support.widget.AvoidArea;
import io.github.alexmofer.android.support.window.EdgeToEdge;

/**
 * 居中对话框
 * Created by Alex on 2025/11/26.
 */
public class CenterDialogFragment extends AppCompatDialogFragment {

    private final Runnable mTouchOutsideCallback = this::onTouchOutside;

    public CenterDialogFragment() {
        super();
    }

    public CenterDialogFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new CenterDialog(requireContext(), getTheme());
    }

    /**
     * 对话框完成创建
     *
     * @param dialog 对话框
     */
    protected void onDialogCreated(@NonNull AppCompatDialog dialog) {
        final Window window = dialog.getWindow();
        if (window != null) {
            // 设置绘制系统栏背景，否则系统栏区域绘制时会被裁切呈现黑色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 调整宽高
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    /**
     * 对话框内容 View 发生变化
     * 注：如果需要强制状态栏与导航栏样式，应该在此处调用
     *
     * @param dialog 对话框
     */
    protected void onDialogContentChanged(@NonNull AppCompatDialog dialog) {
        final Window window = dialog.getWindow();
        if (window != null) {
            final WindowManager.LayoutParams lp = window.getAttributes();
            if (Build.VERSION.SDK_INT >= 30) {
                lp.setFitInsetsSides(0);// 不设置布局不会延伸到状态栏与导航栏
            }
            // 默认状态栏与底部栏透明，强制深色
            EdgeToEdge.enable(window,
                    EdgeToEdge.SystemBarStyle.dark(Color.TRANSPARENT),
                    EdgeToEdge.SystemBarStyle.dark(Color.TRANSPARENT));
        }
    }

    /**
     * 获取窗口动画
     *
     * @return 窗口动画
     */
    @StyleRes
    protected int getWindowAnimations() {
        // API 28 之后 windowAnimations 可接受应用内资源
        return android.R.style.Animation_Dialog;
    }

    /**
     * 获取软键盘模式
     *
     * @return 软键盘模式
     */
    protected int getSoftInputMode() {
        // 注意，自行处理IME避让仅适用于 API 30及以上
        return WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
    }

    /**
     * 获取边距
     *
     * @param context Context
     * @param metrics DisplayMetrics
     * @return 边距
     */
    protected int getMargin(@NonNull Context context, @NonNull DisplayMetrics metrics) {
        return TypedValueUtils.getDimensionPixelSize(16, metrics);
    }

    /**
     * 获取左边距
     *
     * @param context Context
     * @param metrics DisplayMetrics
     * @return 左边距
     */
    protected int getMarginStart(@NonNull Context context, @NonNull DisplayMetrics metrics) {
        return getMargin(context, metrics);
    }

    /**
     * 获取上边距
     *
     * @param context Context
     * @param metrics DisplayMetrics
     * @return 上边距
     */
    protected int getMarginTop(@NonNull Context context, @NonNull DisplayMetrics metrics) {
        return getMargin(context, metrics);
    }

    /**
     * 获取右边距
     *
     * @param context Context
     * @param metrics DisplayMetrics
     * @return 右边距
     */
    protected int getMarginEnd(@NonNull Context context, @NonNull DisplayMetrics metrics) {
        return getMargin(context, metrics);
    }

    /**
     * 获取下边距
     *
     * @param context Context
     * @param metrics DisplayMetrics
     * @return 下边距
     */
    protected int getMarginBottom(@NonNull Context context, @NonNull DisplayMetrics metrics) {
        return getMargin(context, metrics);
    }

    /**
     * 获取修正后的限定宽度
     *
     * @param context Context
     * @param metrics DisplayMetrics
     * @param width   推荐限定宽度
     * @param max     允许的最大限定宽度，如果使用该值，则需要自行处理避让
     * @return 修正后的限定宽度
     */
    protected int getContentFixWidth(@NonNull Context context, @NonNull DisplayMetrics metrics,
                                     int width, int max) {
        return Math.min(width, TypedValueUtils.getDimensionPixelOffset(420, metrics));
    }

    /**
     * 获取修正后的限定高度
     *
     * @param context Context
     * @param metrics DisplayMetrics
     * @param height  推荐限定高度
     * @param max     允许的最大限定高度，如果使用该值，则需要自行处理避让
     * @return 修正后的限定高度
     */
    protected int getContentFixHeight(@NonNull Context context, @NonNull DisplayMetrics metrics,
                                      int height, int max) {
        return Math.round(height * 0.9f);
    }

    /**
     * 获取内容背景
     *
     * @param context Context
     * @return 内容背景
     */
    @Nullable
    protected Drawable getContentBackground(@NonNull Context context) {
        final GradientDrawable background = new GradientDrawable();
        if (ContextUtils.isNight(context)) {
            background.setColor(context.getColor(android.R.color.background_dark));
        } else {
            background.setColor(context.getColor(android.R.color.background_light));
        }
        background.setCornerRadius(TypedValueCompat.dpToPx(16,
                context.getResources().getDisplayMetrics()));
        return background;
    }

    /**
     * 获取内容 ViewOutlineProvider
     *
     * @return 内容 ViewOutlineProvider
     */
    @Nullable
    protected ViewOutlineProvider getContentOutlineProvider() {
        return ViewOutlineProvider.BACKGROUND;
    }

    /**
     * 判断内容是否按照边界裁剪
     *
     * @return 内容按照边界裁剪时返回true
     */
    protected boolean getContentClipToOutline() {
        return true;
    }

    /**
     * 获取内容 Elevation
     *
     * @param context Context
     * @return 内容 Elevation
     */
    protected float getContentElevation(@NonNull Context context) {
        return TypedValueCompat.dpToPx(10, context.getResources().getDisplayMetrics());
    }

    /**
     * 获取外部点击回调
     *
     * @return 外部点击回调
     */
    @NonNull
    protected Runnable getTouchOutsideCallback() {
        return mTouchOutsideCallback;
    }

    private void onTouchOutside() {
        if (isCancelable()) {
            requireDialog().cancel();
        }
    }

    private class CenterDialog extends AppCompatDialog {

        public CenterDialog(@NonNull Context context, int theme) {
            super(context, theme);
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            final Window window = getWindow();
            if (window != null) {
                final WindowManager.LayoutParams lp = window.getAttributes();
                lp.windowAnimations = getWindowAnimations();
                lp.softInputMode = getSoftInputMode();
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            onDialogCreated(this);
        }

        @Override
        public void onContentChanged() {
            super.onContentChanged();
            onDialogContentChanged(this);
        }

        @Override
        public void setContentView(@NonNull View view) {
            super.setContentView(new CenterContainer(view, getTouchOutsideCallback()));
        }

        @Override
        public void setContentView(@NonNull View view, ViewGroup.LayoutParams params) {
            setContentView(view);
        }

        @Override
        public void setContentView(int layoutResID) {
            setContentView(View.inflate(getContext(), layoutResID, null));
        }
    }

    protected static abstract class Container extends ViewGroup {

        private final Runnable mTouchOutside;
        private boolean mDownUnderContent;

        public Container(@NonNull Context content, Runnable touchOutside) {
            super(content);
            mTouchOutside = touchOutside;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                mDownUnderContent = isUnderChild(ev.getX(), ev.getY());
                if (mDownUnderContent) {
                    return super.onInterceptTouchEvent(ev);
                } else {
                    super.onInterceptTouchEvent(ev);
                    return true;
                }
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // DOWN 与 UP 事件均不是发生在子项上面则认定为点击外部
            final int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                if (mDownUnderContent) {
                    return super.onTouchEvent(event);
                }
                super.onTouchEvent(event);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!mDownUnderContent) {
                    final boolean underContent = isUnderChild(event.getX(), event.getY());
                    if (!underContent) {
                        mTouchOutside.run();
                        return super.onTouchEvent(event);
                    }
                }
            }
            return super.onTouchEvent(event);
        }

        private boolean isUnderChild(float x, float y) {
            final int childrenCount = getChildCount();
            for (int i = 0; i < childrenCount; i++) {
                final View child = getChildAt(i);
                if (child.getLeft() <= x && x <= child.getRight()
                        && child.getTop() <= y && y <= child.getBottom()) {
                    return true;
                }
            }
            return false;
        }
    }

    private class CenterContainer extends Container {

        private final View mContent;
        private int mMarginLeft;
        private int mMarginTop;
        private int mMarginRight;
        private int mMarginBottom;

        public CenterContainer(@NonNull View content, Runnable touchOutside) {
            super(content.getContext(), touchOutside);
            final Context context = getContext();
            final FrameLayout warp = new FrameLayout(context);
            final LayoutParams lp = content.getLayoutParams();
            if (lp != null) {
                addView(warp, new LayoutParams(lp));
            } else {
                addView(warp);
            }
            warp.addView(content,
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            warp.setBackground(getContentBackground(context));
            warp.setOutlineProvider(getContentOutlineProvider());
            warp.setClipToOutline(getContentClipToOutline());
            warp.setElevation(getContentElevation(context));
            mContent = warp;
            AvoidArea.paddingAll(this);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final Context context = getContext();
            final DisplayMetrics metrics = getResources().getDisplayMetrics();
            final int width = MeasureSpec.getSize(widthMeasureSpec);
            final int height = MeasureSpec.getSize(heightMeasureSpec);
            final int paddingLeft = getPaddingLeft();
            final int paddingTop = getPaddingTop();
            final int paddingRight = getPaddingRight();
            final int paddingBottom = getPaddingBottom();
            if (getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                mMarginLeft = getMarginEnd(context, metrics);
                mMarginRight = getMarginStart(context, metrics);
            } else {
                mMarginLeft = getMarginStart(context, metrics);
                mMarginRight = getMarginEnd(context, metrics);
            }
            mMarginTop = getMarginTop(context, metrics);
            mMarginBottom = getMarginBottom(context, metrics);
            final int contentWidth = width - paddingLeft - paddingRight
                    - mMarginLeft - mMarginRight;
            final int contentHeight = height - paddingTop - paddingBottom
                    - mMarginTop - mMarginBottom;
            final LayoutParams lp = mContent.getLayoutParams();
            final int contentFixWidth =
                    getContentFixWidth(context, metrics, contentWidth, width);
            final int contentFixHeight =
                    getContentFixHeight(context, metrics, contentHeight, height);
            final int childWidthMeasureSpec;
            if (lp.width == LayoutParams.MATCH_PARENT) {
                childWidthMeasureSpec =
                        MeasureSpec.makeMeasureSpec(contentFixWidth, MeasureSpec.EXACTLY);
            } else if (lp.width == LayoutParams.WRAP_CONTENT) {
                childWidthMeasureSpec =
                        MeasureSpec.makeMeasureSpec(contentFixWidth, MeasureSpec.AT_MOST);
            } else {
                childWidthMeasureSpec =
                        MeasureSpec.makeMeasureSpec(
                                Math.min(Math.max(0, lp.width), contentFixWidth),
                                MeasureSpec.EXACTLY);
            }
            final int childHeightMeasureSpec;
            if (lp.height == LayoutParams.MATCH_PARENT) {
                childHeightMeasureSpec =
                        MeasureSpec.makeMeasureSpec(contentFixHeight, MeasureSpec.EXACTLY);
            } else if (lp.height == LayoutParams.WRAP_CONTENT) {
                childHeightMeasureSpec =
                        MeasureSpec.makeMeasureSpec(contentFixHeight, MeasureSpec.AT_MOST);
            } else {
                childHeightMeasureSpec =
                        MeasureSpec.makeMeasureSpec(
                                Math.min(Math.max(0, lp.height), contentFixHeight),
                                MeasureSpec.EXACTLY);
            }
            mContent.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            setMeasuredDimension(width, height);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            final int width = getWidth();
            final int height = getHeight();
            final int paddingLeft = getPaddingLeft();
            final int paddingTop = getPaddingTop();
            final int paddingRight = getPaddingRight();
            final int paddingBottom = getPaddingBottom();
            final int contentWidth = mContent.getMeasuredWidth();
            final int contentHeight = mContent.getMeasuredHeight();
            final int left = Math.round(paddingLeft + mMarginLeft +
                    (width - paddingLeft - paddingRight - mMarginLeft - mMarginRight
                            - contentWidth) * 0.5f);
            final int top = Math.round(paddingTop + mMarginTop +
                    (height - paddingTop - paddingBottom - mMarginTop - mMarginBottom
                            - contentHeight) * 0.5f);
            mContent.layout(left, top, left + contentWidth, top + contentHeight);
        }

        @Override
        protected LayoutParams generateDefaultLayoutParams() {
            return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
    }
}