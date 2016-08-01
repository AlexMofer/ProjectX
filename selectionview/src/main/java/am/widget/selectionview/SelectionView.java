package am.widget.selectionview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * 快速跳选
 * Created by Alex on 2016/7/29.
 */
public class SelectionView extends View {

    public static final int STYLE_LIST = 0;// 列表模式
    public static final int STYLE_SLIDER = 1;// 滑块模式
    public static final int LOCATION_VIEW_CENTER = 0;// View中央
    public static final int LOCATION_SLIDER_TOP = 1;// 与滑块顶部对齐
    private int mBarStyle;
    private Drawable mBarBackground;
    private final Rect mBarPadding = new Rect();
    private int mBarWidth;
    private int mBarHeight;
    private int mBarItemHeight;
    private int mItemWidthActual;
    private float mItemHeightActual;
    private Drawable mBarSlider;
    private int mNoticeLocation;
    private int mNoticeWidth;
    private int mNoticeHeight;
    private Drawable mNoticeBackground;
    private final Rect mNoticePadding = new Rect();
    private Selection mSelection;

    public SelectionView(Context context) {
        this(context, null);
    }

    public SelectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int barStyle = STYLE_LIST;
        Drawable barBackground;
        int barPadding = 0;
        int barPaddingLeft;
        int barPaddingTop;
        int barPaddingRight;
        int barPaddingBottom;
        int barWidth = 0;
        int barItemHeight = 0;
        Drawable barSlider;
        int noticeLocation = LOCATION_VIEW_CENTER;
        int noticeWidth = 0;
        int noticeHeight = 0;
        Drawable noticeBackground;
        int noticePadding = 0;
        int noticePaddingLeft;
        int noticePaddingTop;
        int noticePaddingRight;
        int noticePaddingBottom;
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.SelectionView);
        barStyle = custom.getInt(R.styleable.SelectionView_svBarStyle, barStyle);
        barBackground = custom.getDrawable(R.styleable.SelectionView_svBarBackground);
        barPadding = custom.getDimensionPixelOffset(R.styleable.SelectionView_svBarPadding, barPadding);
        barPaddingLeft = custom.getDimensionPixelOffset(R.styleable.SelectionView_svBarPaddingLeft, barPadding);
        barPaddingTop = custom.getDimensionPixelOffset(R.styleable.SelectionView_svBarPaddingTop, barPadding);
        barPaddingRight = custom.getDimensionPixelOffset(R.styleable.SelectionView_svBarPaddingRight, barPadding);
        barPaddingBottom = custom.getDimensionPixelOffset(R.styleable.SelectionView_svBarPaddingBottom, barPadding);
        barWidth = custom.getDimensionPixelOffset(R.styleable.SelectionView_svBarWidth, barWidth);
        barItemHeight = custom.getDimensionPixelOffset(R.styleable.SelectionView_svBarItemHeight, barItemHeight);
        barSlider = custom.getDrawable(R.styleable.SelectionView_svBarSlider);
        noticeLocation = custom.getInt(R.styleable.SelectionView_svNoticeLocation, noticeLocation);
        noticeWidth = custom.getDimensionPixelOffset(R.styleable.SelectionView_svNoticeWidth, noticeWidth);
        noticeHeight = custom.getDimensionPixelOffset(R.styleable.SelectionView_svNoticeHeight, noticeHeight);
        noticeBackground = custom.getDrawable(R.styleable.SelectionView_svNoticeBackground);
        noticePadding = custom.getDimensionPixelOffset(R.styleable.SelectionView_svNoticePadding, noticePadding);
        noticePaddingLeft = custom.getDimensionPixelOffset(R.styleable.SelectionView_svNoticePaddingLeft, noticePadding);
        noticePaddingTop = custom.getDimensionPixelOffset(R.styleable.SelectionView_svNoticePaddingTop, noticePadding);
        noticePaddingRight = custom.getDimensionPixelOffset(R.styleable.SelectionView_svNoticePaddingRight, noticePadding);
        noticePaddingBottom = custom.getDimensionPixelOffset(R.styleable.SelectionView_svNoticePaddingBottom, noticePadding);
        custom.recycle();
        setBarStyle(barStyle);
        setBarBackground(barBackground);
        setBarPadding(barPaddingLeft, barPaddingTop, barPaddingRight, barPaddingBottom);
        setBarWidth(barWidth);
        setBarItemHeight(barItemHeight);
        setBarSlider(barSlider);
        setNoticeLocation(noticeLocation);
        setNoticeWidth(noticeWidth);
        setNoticeHeight(noticeHeight);
        setNoticeBackground(noticeBackground);
        setNoticePadding(noticePaddingLeft, noticePaddingTop, noticePaddingRight,
                noticePaddingBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int suggestedMinimumWidth = getSuggestedMinimumWidth();
        final int suggestedMinimumHeight = getSuggestedMinimumHeight();
        final int paddingStart = Compat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int paddingBottom = getPaddingBottom();
        int contentWidth = 0;
        switch (mNoticeLocation) {
            case LOCATION_SLIDER_TOP:
                contentWidth = mNoticeWidth + mBarWidth;
                break;
            case LOCATION_VIEW_CENTER:
                contentWidth = mBarWidth + mNoticeWidth + mBarWidth;
                break;
        }
        final int barHeight = mBarPadding.top + mBarPadding.bottom +
                (mSelection == null ? 0 : mSelection.getItemCount()) * mBarItemHeight;
        int contentHeight = Math.max(barHeight, mNoticeHeight);
        final int width = Math.max(
                contentWidth + paddingStart + paddingEnd, suggestedMinimumWidth);
        final int height = Math.max(
                contentHeight + paddingTop + paddingBottom, suggestedMinimumHeight);
        setMeasuredDimension(
                resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
        final int measuredHeight = getMeasuredHeight();
        mBarHeight = measuredHeight - paddingTop - paddingBottom;
        mItemWidthActual = mBarWidth - mBarPadding.left - mBarPadding.right;
        mItemHeightActual = (mBarHeight - mBarPadding.top - mBarPadding.bottom) /
                ((mSelection == null || mSelection.getItemCount() == 0) ?
                        1 : mSelection.getItemCount());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBarBackground(canvas);
        drawBarItems(canvas);
    }

    private void drawBarBackground(Canvas canvas) {
        if (mBarBackground == null)
            return;
        final int paddingEnd = Compat.getPaddingEnd(this);
        mBarBackground.setBounds(0, 0, mBarWidth, mBarHeight);
        canvas.save();
        canvas.translate(getWidth() - paddingEnd - mBarWidth, getPaddingTop());
        mBarBackground.draw(canvas);
        canvas.restore();
    }

    private void drawBarItems(Canvas canvas) {
        if (mSelection == null || mSelection.getItemCount() <= 0)
            return;
        final int paddingEnd = Compat.getPaddingEnd(this);
        canvas.save();
        canvas.translate(getWidth() - paddingEnd - mBarWidth + mBarPadding.left,
                getPaddingTop() + mBarPadding.top);
        for (int position = 0; position < mSelection.getItemCount(); position++) {
            Drawable item = mSelection.getItemBar(position);
            if (item != null) {
                item.setBounds(0, 0, mItemWidthActual, (int) mItemHeightActual);
                item.draw(canvas);
            }
            canvas.translate(0, mItemHeightActual);
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean touch = false;
        final int width = getWidth();
        final int height = getHeight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int startX = width - paddingEnd - mBarWidth;
        if (mBarBackground != null)
            Compat.setHotspot(mBarBackground, event.getX() - startX, event.getY() - paddingTop);
        if (event.getX() > startX) {
            // TODO
            setClickable(true);
            touch = true;
        }
        boolean superTouch = super.onTouchEvent(event);
        if (isClickable() && (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL)) {
            setClickable(false);
        }
        return superTouch || touch;
    }

    @Override
    protected void drawableStateChanged() {
        if (mBarBackground != null && mBarBackground.isStateful()) {
            mBarBackground.setState(getDrawableState());
        }
        super.drawableStateChanged();

    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        boolean isPress = false;
        if (mBarBackground != null && who == mBarBackground) {
            isPress = true;
        }
        return super.verifyDrawable(who) || isPress;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mBarBackground != null) {
            mBarBackground.setCallback(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mBarBackground != null) {
            mBarBackground.setCallback(null);
        }
        super.onDetachedFromWindow();
    }

    /**
     * 设置控制条风格
     *
     * @param style 风格
     */
    public void setBarStyle(int style) {
        if ((style == STYLE_LIST || style == STYLE_SLIDER) && mBarStyle != style) {
            mBarStyle = style;
            invalidate();
        }
    }

    /**
     * 设置控制条背景
     *
     * @param background 背景
     */
    public void setBarBackground(Drawable background) {
        if (mBarBackground != background) {
            if (mBarBackground != null)
                mBarBackground.setCallback(null);
            mBarBackground = background;
            if (mBarBackground != null)
                mBarBackground.setCallback(this);
            invalidate();
        }
    }

    /**
     * 设置控制条Padding
     *
     * @param left   左
     * @param top    上
     * @param right  右
     * @param bottom 下
     */
    public void setBarPadding(int left, int top, int right, int bottom) {
        left = left < 0 ? 0 : left;
        top = top < 0 ? 0 : top;
        right = right < 0 ? 0 : right;
        bottom = bottom < 0 ? 0 : bottom;
        if (left != mBarPadding.left || top != mBarPadding.top ||
                right != mBarPadding.right || bottom != mBarPadding.bottom) {
            mBarPadding.set(left, top, right, bottom);
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置控制条宽度
     *
     * @param width 宽
     */
    public void setBarWidth(int width) {
        if (width != mBarWidth) {
            mBarWidth = width;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置控制条子项高度（实际子项高度由布局模式决定）
     *
     * @param height 高度
     */
    public void setBarItemHeight(int height) {
        if (mBarItemHeight != height) {
            mBarItemHeight = height;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置滑块（仅在STYLE_SLIDER模式下有效）
     *
     * @param slider 滑块
     */
    public void setBarSlider(Drawable slider) {
        if (mBarSlider != slider) {
            if (mBarSlider != null)
                mBarSlider.setCallback(null);
            mBarSlider = slider;
            if (mBarSlider != null)
                mBarSlider.setCallback(this);
            invalidate();
        }
    }

    /**
     * 设置提示位置
     *
     * @param location 位置
     */
    public void setNoticeLocation(int location) {
        if ((location == LOCATION_SLIDER_TOP || location == LOCATION_VIEW_CENTER)
                && mNoticeLocation != location) {
            mNoticeLocation = location;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置提示宽度
     *
     * @param width 宽度
     */
    public void setNoticeWidth(int width) {
        if (mNoticeWidth != width) {
            mNoticeWidth = width;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置提示高度
     *
     * @param height 高度
     */
    public void setNoticeHeight(int height) {
        if (mNoticeHeight != height) {
            mNoticeHeight = height;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置提示背景
     *
     * @param background 背景
     */
    public void setNoticeBackground(Drawable background) {
        if (mNoticeBackground != background) {
            mNoticeBackground = background;
            invalidate();
        }
    }

    /**
     * 设置提示Padding
     *
     * @param left   左
     * @param top    上
     * @param right  右
     * @param bottom 下
     */
    public void setNoticePadding(int left, int top, int right, int bottom) {
        left = left < 0 ? 0 : left;
        top = top < 0 ? 0 : top;
        right = right < 0 ? 0 : right;
        bottom = bottom < 0 ? 0 : bottom;
        if (left != mNoticePadding.left || top != mNoticePadding.top ||
                right != mNoticePadding.right || bottom != mNoticePadding.bottom) {
            mNoticePadding.set(left, top, right, bottom);
            invalidate();
        }
    }

    /**
     * 设置数据源
     *
     * @param selection 数据源
     */
    public void setSelection(Selection selection) {
        if (mSelection != selection) {
            mSelection = selection;
            requestLayout();
            invalidate();
        }
    }
}
