package io.github.alexmofer.android.support.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import androidx.core.util.TypedValueCompat;

import io.github.alexmofer.android.support.utils.ContextUtils;
import io.github.alexmofer.android.support.utils.ViewOutlineProviderUtils;
import io.github.alexmofer.android.support.widget.AvoidArea;
import io.github.alexmofer.android.support.window.EdgeToEdge;
import io.github.alexmofer.android.support.window.WindowMetricsHelper;

/**
 * 底部对话框
 * Created by Alex on 2025/11/26.
 */
public class SheetDialogFragment extends CenterDialogFragment {

    private final boolean mEdgeToEdge;
    private boolean mSheet;

    public SheetDialogFragment(boolean edgeToEdge) {
        super();
        mEdgeToEdge = edgeToEdge;
    }

    public SheetDialogFragment() {
        this(false);
    }

    public SheetDialogFragment(@LayoutRes int contentLayoutId, boolean edgeToEdge) {
        super(contentLayoutId);
        mEdgeToEdge = edgeToEdge;
    }

    public SheetDialogFragment(@LayoutRes int contentLayoutId) {
        this(contentLayoutId, false);
    }

    /**
     * 分离
     *
     * @param savedInstanceState 保存的状态
     * @return 返回 true 时进入 Sheet 模式，反之进入居中对话框模式
     */
    protected boolean onBranch(@Nullable Bundle savedInstanceState) {
        return !WindowMetricsHelper.computeCurrentWindowMetrics(requireActivity()).isSW480();
    }

    /**
     * 判断是否为 Sheet 模式
     *
     * @return 为 Sheet 模式时返回 true
     */
    protected final boolean isSheet() {
        return mSheet;
    }

    /**
     * 判断是否为边到边
     *
     * @return 为边到边时返回 true
     */
    protected final boolean isEdgeToEdge() {
        return mEdgeToEdge;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mSheet = onBranch(savedInstanceState);
        return mSheet ? new SheetDialog(requireContext(), getTheme())
                : super.onCreateDialog(savedInstanceState);
    }

    @Override
    protected final void onDialogCreated(@NonNull AppCompatDialog dialog) {
        onDialogCreated(dialog, false, false);
    }

    /**
     * 对话框完成创建
     *
     * @param dialog     对话框
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     */
    protected void onDialogCreated(@NonNull AppCompatDialog dialog,
                                   boolean sheet, boolean edgeToEdge) {
        super.onDialogCreated(dialog);
    }

    @Override
    protected final void onDialogContentChanged(@NonNull AppCompatDialog dialog) {
        onDialogContentChanged(dialog, false, false);
    }

    /**
     * 对话框内容 View 发生变化
     * 注：如果需要强制状态栏与导航栏样式，应该在此处调用
     *
     * @param dialog     对话框
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     */
    protected void onDialogContentChanged(@NonNull AppCompatDialog dialog,
                                          boolean sheet, boolean edgeToEdge) {
        super.onDialogContentChanged(dialog);
        if (sheet && edgeToEdge) {
            final Window window = dialog.getWindow();
            if (window != null) {
                // 默认状态栏与底部栏透明，强制深色状态栏，导航栏跟随系统
                EdgeToEdge.enable(window,
                        EdgeToEdge.SystemBarStyle.dark(Color.TRANSPARENT),
                        EdgeToEdge.SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT));
            }
        }
    }

    @Override
    protected final int getWindowAnimations() {
        return getWindowAnimations(false, false);
    }

    /**
     * 获取窗口动画
     *
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     * @return 窗口动画
     */
    @StyleRes
    protected int getWindowAnimations(boolean sheet, boolean edgeToEdge) {
        if (sheet) {
            return android.R.style.Animation_InputMethod;
        } else {
            return super.getWindowAnimations();
        }
    }

    @Override
    protected final int getSoftInputMode() {
        return getSoftInputMode(false, false);
    }

    /**
     * 获取软键盘模式
     *
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     * @return 软键盘模式
     */
    protected int getSoftInputMode(boolean sheet, boolean edgeToEdge) {
        return super.getSoftInputMode();
    }

    @Override
    protected final int getMargin(@NonNull Context context, @NonNull DisplayMetrics metrics) {
        return getMargin(context, metrics, false, false);
    }

    /**
     * 获取边距
     *
     * @param context Context
     * @param metrics DisplayMetrics
     * @return 边距
     */
    protected int getMargin(@NonNull Context context, @NonNull DisplayMetrics metrics,
                            boolean sheet, boolean edgeToEdge) {
        if (sheet && edgeToEdge) {
            return 0;
        }
        return super.getMargin(context, metrics);
    }

    @Override
    protected final int getMarginStart(@NonNull Context context, @NonNull DisplayMetrics metrics) {
        return getMarginStart(context, metrics, false, false);
    }

    /**
     * 获取左边距
     *
     * @param context    Context
     * @param metrics    DisplayMetrics
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     * @return 左边距
     */
    protected int getMarginStart(@NonNull Context context, @NonNull DisplayMetrics metrics,
                                 boolean sheet, boolean edgeToEdge) {
        return getMargin(context, metrics, sheet, edgeToEdge);
    }

    @Override
    protected final int getMarginTop(@NonNull Context context, @NonNull DisplayMetrics metrics) {
        return getMarginTop(context, metrics, false, false);
    }

    /**
     * 获取上边距
     *
     * @param context    Context
     * @param metrics    DisplayMetrics
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     * @return 上边距
     */
    protected int getMarginTop(@NonNull Context context, @NonNull DisplayMetrics metrics,
                               boolean sheet, boolean edgeToEdge) {
        return getMargin(context, metrics, sheet, edgeToEdge);
    }

    @Override
    protected final int getMarginEnd(@NonNull Context context, @NonNull DisplayMetrics metrics) {
        return getMarginEnd(context, metrics, false, false);
    }

    /**
     * 获取右边距
     *
     * @param context    Context
     * @param metrics    DisplayMetrics
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     * @return 右边距
     */
    protected int getMarginEnd(@NonNull Context context, @NonNull DisplayMetrics metrics,
                               boolean sheet, boolean edgeToEdge) {
        return getMargin(context, metrics, sheet, edgeToEdge);
    }

    @Override
    protected final int getMarginBottom(@NonNull Context context, @NonNull DisplayMetrics metrics) {
        return getMarginBottom(context, metrics, false, false);
    }

    /**
     * 获取下边距
     *
     * @param context    Context
     * @param metrics    DisplayMetrics
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     * @return 下边距
     */
    protected int getMarginBottom(@NonNull Context context, @NonNull DisplayMetrics metrics,
                                  boolean sheet, boolean edgeToEdge) {
        return getMargin(context, metrics, sheet, edgeToEdge);
    }

    @Override
    protected final int getContentFixWidth(@NonNull Context context, @NonNull DisplayMetrics metrics,
                                           int width, int max) {
        return getContentFixWidth(context, metrics, width, max, false, false);
    }

    /**
     * 获取修正后的限定宽度
     *
     * @param context    Context
     * @param metrics    DisplayMetrics
     * @param width      推荐限定宽度
     * @param max        允许的最大限定宽度，如果使用该值，则需要自行处理避让
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     * @return 修正后的限定宽度
     */
    protected int getContentFixWidth(@NonNull Context context, @NonNull DisplayMetrics metrics,
                                     int width, int max, boolean sheet, boolean edgeToEdge) {
        return super.getContentFixWidth(context, metrics, width, max);
    }

    @Override
    protected final int getContentFixHeight(@NonNull Context context,
                                            @NonNull DisplayMetrics metrics, int height, int max) {
        return getContentFixHeight(context, metrics, height, max, false, false);
    }

    /**
     * 获取修正后的限定高度
     *
     * @param context    Context
     * @param metrics    DisplayMetrics
     * @param height     推荐限定高度
     * @param max        允许的最大限定高度，如果使用该值，则需要自行处理避让
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     * @return 修正后的限定高度
     */
    protected int getContentFixHeight(@NonNull Context context, @NonNull DisplayMetrics metrics,
                                      int height, int max, boolean sheet, boolean edgeToEdge) {
        return super.getContentFixHeight(context, metrics, height, max);
    }

    @Nullable
    @Override
    protected final Drawable getContentBackground(@NonNull Context context) {
        return getContentBackground(context, false, false);
    }

    /**
     * 获取内容背景
     *
     * @param context    Context
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     * @return 内容背景
     */
    @Nullable
    protected Drawable getContentBackground(@NonNull Context context,
                                            boolean sheet, boolean edgeToEdge) {
        if (sheet && edgeToEdge) {
            final GradientDrawable background = new GradientDrawable();
            if (ContextUtils.isNight(context)) {
                background.setColor(context.getColor(android.R.color.background_dark));
            } else {
                background.setColor(context.getColor(android.R.color.background_light));
            }
            return background;
        }
        return super.getContentBackground(context);
    }

    @Nullable
    @Override
    protected final ViewOutlineProvider getContentOutlineProvider() {
        return getContentOutlineProvider(false, false);
    }

    /**
     * 获取内容 ViewOutlineProvider
     *
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     * @return 内容 ViewOutlineProvider
     */
    @Nullable
    protected ViewOutlineProvider getContentOutlineProvider(boolean sheet, boolean edgeToEdge) {
        if (sheet && edgeToEdge) {
            return ViewOutlineProviderUtils.newTopRoundRect(TypedValueCompat.dpToPx(16,
                    requireContext().getResources().getDisplayMetrics()));
        }
        return super.getContentOutlineProvider();
    }

    @Override
    protected final boolean getContentClipToOutline() {
        return getContentClipToOutline(false, false);
    }

    /**
     * 判断内容是否按照边界裁剪
     *
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     * @return 内容按照边界裁剪时返回true
     */
    protected boolean getContentClipToOutline(boolean sheet, boolean edgeToEdge) {
        return super.getContentClipToOutline();
    }

    @Override
    protected final float getContentElevation(@NonNull Context context) {
        return getContentElevation(context, false, false);
    }

    /**
     * 获取内容 Elevation
     *
     * @param context    Context
     * @param sheet      是否为底部对话框模式
     * @param edgeToEdge 是否边到边
     * @return 内容 Elevation
     */
    protected float getContentElevation(@NonNull Context context,
                                        boolean sheet, boolean edgeToEdge) {
        return super.getContentElevation(context);
    }

    private class SheetDialog extends AppCompatDialog {

        public SheetDialog(@NonNull Context context, int theme) {
            super(context, theme);
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            final Window window = getWindow();
            if (window != null) {
                final WindowManager.LayoutParams lp = window.getAttributes();
                lp.windowAnimations = getWindowAnimations(true, mEdgeToEdge);
                lp.softInputMode = getSoftInputMode(true, mEdgeToEdge);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            onDialogCreated(this, true, mEdgeToEdge);
        }

        @Override
        public void onContentChanged() {
            super.onContentChanged();
            onDialogContentChanged(this, true, mEdgeToEdge);
        }

        @Override
        public void setContentView(@NonNull View view) {
            super.setContentView(new SheetContainer(view, getTouchOutsideCallback()));
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

    private class SheetContainer extends Container {

        private final View mContent;
        private int mMarginLeft;
        private int mMarginRight;
        private int mMarginBottom;

        public SheetContainer(@NonNull View content, Runnable touchOutside) {
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
            warp.setBackground(getContentBackground(context, true, mEdgeToEdge));
            warp.setOutlineProvider(getContentOutlineProvider(true, mEdgeToEdge));
            warp.setClipToOutline(getContentClipToOutline(true, mEdgeToEdge));
            warp.setElevation(getContentElevation(context, true, mEdgeToEdge));
            mContent = warp;
            if (mEdgeToEdge) {
                AvoidArea.padding(this, AvoidArea.TOP);
            } else {
                AvoidArea.paddingAll(this);
            }
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
                mMarginLeft = getMarginEnd(context, metrics, true, mEdgeToEdge);
                mMarginRight = getMarginStart(context, metrics, true, mEdgeToEdge);
            } else {
                mMarginLeft = getMarginStart(context, metrics, true, mEdgeToEdge);
                mMarginRight = getMarginEnd(context, metrics, true, mEdgeToEdge);
            }
            final int marginTop = getMarginTop(context, metrics, true, mEdgeToEdge);
            mMarginBottom = getMarginBottom(context, metrics, true, mEdgeToEdge);
            final int contentWidth = width - paddingLeft - paddingRight
                    - mMarginLeft - mMarginRight;
            final int contentHeight = height - paddingTop - paddingBottom
                    - marginTop - mMarginBottom;
            final LayoutParams lp = mContent.getLayoutParams();
            final int contentFixWidth =
                    getContentFixWidth(context, metrics, contentWidth, width, true, mEdgeToEdge);
            final int contentFixHeight =
                    getContentFixHeight(context, metrics, contentHeight, height, true, mEdgeToEdge);
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
            final int paddingRight = getPaddingRight();
            final int paddingBottom = getPaddingBottom();
            final int contentWidth = mContent.getMeasuredWidth();
            final int contentHeight = mContent.getMeasuredHeight();
            final int left = Math.round(paddingLeft + mMarginLeft +
                    (width - paddingLeft - paddingRight - mMarginLeft - mMarginRight
                            - contentWidth) * 0.5f);
            final int bottom = height - paddingBottom - mMarginBottom;
            mContent.layout(left, bottom - contentHeight, left + contentWidth, bottom);
        }

        @Override
        protected LayoutParams generateDefaultLayoutParams() {
            return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
    }
}
