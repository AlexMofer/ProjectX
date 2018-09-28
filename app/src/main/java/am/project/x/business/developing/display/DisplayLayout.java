package am.project.x.business.developing.display;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * 显示布局
 * Created by Alex on 2017/11/10.
 */

public class DisplayLayout extends ViewGroup {

    public static final int AUTO_FIT_HORIZONTAL = 0;
    public static final int AUTO_FIT_VERTICAL = 1;
    private int mDisplayWidth = 0;
    private int mDisplayHeight = 0;
    private float mRequestedMaxWidth = 0;
    private float mRequestedMaxHeight = 0;
    private float mRequestedWidth = 0;
    private float mRequestedHeight = 0;
    private float mScale = 1.0f;
    private int mAutoFit = AUTO_FIT_HORIZONTAL;
    private DisplayRenderView mRender;

    public DisplayLayout(Context context) {
        super(context);
        initView(context);
    }

    public DisplayLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DisplayLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mRender = new DisplayRenderView(context);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke((int) (10 * context.getResources().getDisplayMetrics().density),
                0xff4788f4);
        drawable.setColor(0x804788f4);
        setBackgroundDrawable(drawable);
        addView(mRender);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        removeAllViews();
        addView(mRender);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int minWidth = getSuggestedMinimumWidth();
        final int minHeight = getSuggestedMinimumHeight();
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();
        int needWidth;
        int needHeight;
        if (mAutoFit == AUTO_FIT_HORIZONTAL) {
            // 水平方向上充填
            float p = mDisplayWidth / mRequestedMaxWidth;
            if (mRequestedWidth == mRequestedMaxWidth && mScale == 1) {
                needWidth = mDisplayWidth;
                needHeight = Math.round(mRequestedHeight * p);
            } else {
                needWidth = Math.round(mRequestedWidth * p * mScale);
                needHeight = Math.round(mRequestedHeight * p * mScale);
            }
        } else {
            // 垂直方向上充填
            float p = mDisplayHeight / mRequestedMaxHeight;
            if (mRequestedHeight == mRequestedMaxHeight && mScale == 1) {
                needHeight = mDisplayHeight;
                needWidth = Math.round(mRequestedWidth * p);
            } else {
                needHeight = Math.round(mRequestedHeight * p * mScale);
                needWidth = Math.round(mRequestedWidth * p * mScale);
            }
        }
        final int width = Math.max(needWidth + paddingLeft + paddingRight, minWidth);
        final int height = Math.max(needHeight + paddingTop + paddingBottom, minHeight);
        mRender.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int left = getPaddingLeft();
        final int top = getPaddingTop();
        final int right = getMeasuredWidth() - getPaddingRight();
        final int bottom = getMeasuredHeight() - getPaddingBottom();
        mRender.layout(left, top, right, bottom);
    }

    public void setDisplaySize(int width, int height) {
        if (mDisplayWidth != width || mDisplayHeight != height) {
            mDisplayWidth = width;
            mDisplayHeight = height;
            requestLayout();
            invalidate();
        }
    }

    public void setRequestedMaxSize(float width, float height) {
        if (mRequestedMaxWidth != width || mRequestedMaxHeight != height) {
            mRequestedMaxWidth = width;
            mRequestedMaxHeight = height;
            requestLayout();
            invalidate();
        }
    }

    public void setRequestedSize(float width, float height) {
        if (mRequestedWidth != width || mRequestedHeight != height) {
            mRequestedWidth = width;
            mRequestedHeight = height;
            requestLayout();
            invalidate();
        }
    }

    public void setScale(float scale) {
        if (mScale != scale) {
            mScale = scale;
            requestLayout();
            invalidate();
        }
    }

    public void setAutoFit(int type) {
        mAutoFit = type;
    }

    public DisplayRenderView getRender() {
        return mRender;
    }
}
