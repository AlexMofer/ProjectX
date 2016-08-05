package am.widget.selectionview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;


/**
 * 快速跳选
 * Created by Alex on 2016/7/29.
 */
public class SelectionView extends View {

    public static final int STYLE_LIST = 0;// 列表模式
    public static final int STYLE_SLIDER = 1;// 滑块模式
    public static final int LOCATION_VIEW_CENTER = 0;// View中央
    public static final int LOCATION_SLIDER_TOP = 1;// 与滑块顶部对齐
    private static final float MAX_ALPHA = 30;
    private static final int FRAME = 10;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
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
    private boolean showNotice = false;
    private int mNoticePosition = 0;
    private boolean mNoticeAnimation;
    private int mNoticeAlpha = 0;
    private float mTouchY = 0;
    private int mSliderHeight = 0;
    private float mSliderCenter = 0;
    private Selection mSelection;
    private OnSelectedListener listener;

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
        boolean animation;
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
        animation = custom.getBoolean(R.styleable.SelectionView_svNoticeAnimation, false);
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
        setNoticeAnimation(animation);
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
        drawBar(canvas);
        drawNotice(canvas);
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

    private void drawBar(Canvas canvas) {
        if (mSelection == null || mSelection.getItemCount() <= 0)
            return;
        switch (mBarStyle) {
            case STYLE_LIST:
                drawBarList(canvas);
                break;
            case STYLE_SLIDER:
                drawBarSlider(canvas);
                break;
        }
    }

    private void drawBarList(Canvas canvas) {
        final int paddingEnd = Compat.getPaddingEnd(this);
        canvas.save();
        canvas.translate(getWidth() - paddingEnd - mBarWidth + mBarPadding.left,
                getPaddingTop() + mBarPadding.top);
        for (int position = 0; position < mSelection.getItemCount(); position++) {
            Drawable item = mSelection.getBar(position);
            if (item != null) {
                item.setBounds(0, 0, mItemWidthActual, (int) mItemHeightActual);
                item.draw(canvas);
            }
            canvas.translate(0, mItemHeightActual);
        }
        canvas.restore();
    }

    private void drawBarSlider(Canvas canvas) {
        if (mBarSlider == null)
            return;
        int mSliderWidth = mBarSlider.getIntrinsicWidth();
        mSliderHeight = mBarSlider.getIntrinsicHeight();
        if (mSliderWidth <= 0 || mSliderHeight <= 0)
            return;
        if (mSliderWidth > mBarWidth - mBarPadding.left - mBarPadding.right) {
            mSliderWidth = mBarWidth - mBarPadding.left - mBarPadding.right;
            mSliderHeight = mSliderHeight * mSliderWidth / mBarSlider.getIntrinsicWidth();
        }
        final float centerStartY = getPaddingTop() + mBarPadding.top + mSliderHeight * 0.5f;
        final float centerEndY = getHeight() - getPaddingBottom() - mBarPadding.bottom
                - mSliderHeight * 0.5f;
        mSliderCenter = mTouchY < centerStartY ? centerStartY :
                (mTouchY > centerEndY ? centerEndY : mTouchY);
        canvas.save();
        canvas.translate(getWidth() - Compat.getPaddingEnd(this) - mBarPadding.right -
                        (mBarWidth - mBarPadding.left - mBarPadding.right) * 0.5f
                        - mSliderWidth * 0.5f,
                mSliderCenter - mSliderHeight * 0.5f);
        mBarSlider.setBounds(0, 0, mSliderWidth, mSliderHeight);
        mBarSlider.draw(canvas);
        canvas.restore();
    }

    private void drawNotice(Canvas canvas) {
        if (!showNotice && mNoticeAlpha <= 0)
            return;
        int alpha = (int) (255 * INTERPOLATOR.getInterpolation(mNoticeAlpha / MAX_ALPHA));
        switch (mNoticeLocation) {
            case LOCATION_VIEW_CENTER:
                drawNoticeViewCenter(canvas, alpha);
                break;
            case LOCATION_SLIDER_TOP:
                drawNoticeSliderTop(canvas, alpha);
                break;
        }
        if (showNotice) {
            if (mNoticeAlpha < MAX_ALPHA) {
                mNoticeAlpha++;
                postInvalidateDelayed(FRAME);
            }
        } else {
            mNoticeAlpha--;
            postInvalidateDelayed(FRAME);
        }
    }

    private void drawNoticeViewCenter(Canvas canvas, int alpha) {
        final int paddingStart = Compat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int paddingBottom = getPaddingBottom();
        final int width = getWidth();
        final int height = getHeight();
        canvas.save();
        canvas.translate(paddingStart + (width - paddingStart - paddingEnd) * 0.5f -
                        mNoticeWidth * 0.5f,
                paddingTop + (height - paddingTop - paddingBottom) * 0.5f -
                        mNoticeHeight * 0.5f);
        if (mNoticeBackground != null) {
            mNoticeBackground.setBounds(0, 0, mNoticeWidth, mNoticeHeight);
            mNoticeBackground.setAlpha(alpha);
            mNoticeBackground.draw(canvas);
        }
        if (mSelection == null || mSelection.getItemCount() <= 0)
            return;
        Drawable notice = mSelection.getNotice(mNoticePosition);
        if (notice != null) {
            notice.setBounds(mNoticePadding.left, mNoticePadding.top,
                    mNoticeWidth - mNoticePadding.right, mNoticeHeight - mNoticePadding.bottom);
            notice.setAlpha(alpha);
            notice.draw(canvas);
        }
    }

    private void drawNoticeSliderTop(Canvas canvas, int alpha) {
        canvas.save();
        final int noticeEndY = getHeight() - getPaddingBottom() - mNoticeHeight;
        float moveY = mTouchY - mNoticeHeight;
        moveY = moveY < getPaddingTop() ? getPaddingTop() : moveY;
        moveY = moveY > noticeEndY ? noticeEndY : moveY;
        canvas.translate(getWidth() - Compat.getPaddingEnd(this) - mBarWidth - mNoticeWidth, moveY);
        if (mNoticeBackground != null) {
            mNoticeBackground.setBounds(0, 0, mNoticeWidth, mNoticeHeight);
            mNoticeBackground.setAlpha(alpha);
            mNoticeBackground.draw(canvas);
        }
        if (mSelection == null || mSelection.getItemCount() <= 0)
            return;
        Drawable notice = mSelection.getNotice(mNoticePosition);
        if (notice != null) {
            notice.setBounds(mNoticePadding.left, mNoticePadding.top,
                    mNoticeWidth - mNoticePadding.right, mNoticeHeight - mNoticePadding.bottom);
            notice.setAlpha(alpha);
            notice.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (mBarStyle) {
            case STYLE_LIST:
                return onTouchEventList(event);
            case STYLE_SLIDER:
                return onTouchEventSlider(event);
            default:
                return super.onTouchEvent(event);
        }
    }

    private boolean onTouchEventList(MotionEvent event) {
        boolean touch = false;
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        if (mBarBackground != null)
            Compat.setHotspot(mBarBackground,
                    x - (getWidth() - Compat.getPaddingEnd(this) - mBarWidth), y - getPaddingTop());
        boolean superTouch;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (startTouchList(x, y)) {
                    setClickable(true);
                    touch = true;
                    showNotice = true;
                    mNoticePosition = getTouchPositionList(x, y);
                    mTouchY = y;
                    invalidate();
                    notifyListener();
                }
                superTouch = super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                superTouch = super.onTouchEvent(event);
                if (showNotice) {
                    showNotice = false;
                    int positionOld = mNoticePosition;
                    mNoticePosition = getTouchPositionList(x, y);
                    mTouchY = y;
                    invalidate();
                    if (positionOld != mNoticePosition) {
                        notifyListener();
                    }
                }
                if (isClickable())
                    setClickable(false);
                break;
            case MotionEvent.ACTION_MOVE:
                if (showNotice) {
                    touch = true;
                    int position = getTouchPositionList(x, y);
                    boolean isPositionChanged = false;
                    if (mNoticePosition != position) {
                        mNoticePosition = position;
                        isPositionChanged = true;
                    }
                    switch (mNoticeLocation) {
                        case LOCATION_VIEW_CENTER:
                            if (isPositionChanged) {
                                invalidate();
                                notifyListener();
                            }
                            break;
                        case LOCATION_SLIDER_TOP:
                            mTouchY = y;
                            invalidate();
                            if (isPositionChanged)
                                notifyListener();
                            break;
                    }
                    superTouch = super.onTouchEvent(event);
                    if (x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight() && !isPressed())
                        setPressed(true);
                } else {
                    superTouch = super.onTouchEvent(event);
                }
                break;
            default:
                superTouch = super.onTouchEvent(event);
                break;
        }
        return superTouch || touch;
    }

    private boolean startTouchList(float x, float y) {
        final int width = getWidth();
        final int height = getHeight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int startX = width - paddingEnd - mBarWidth + mBarPadding.left;
        final int endX = width - paddingEnd - mBarPadding.right;
        final int startY = paddingTop + mBarPadding.top;
        final int endY = height - paddingBottom - mBarPadding.bottom;
        return x >= startX && x <= endX && y >= startY && y <= endY;
    }

    private int getTouchPositionList(float x, float y) {
        if (mSelection == null || mSelection.getItemCount() <= 0)
            return 0;
        final int width = getWidth();
        final int height = getHeight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int startX = width - paddingEnd - mBarWidth + mBarPadding.left;
        final int endX = width - paddingEnd - mBarPadding.right;
        final int startY = paddingTop + mBarPadding.top;
        final int endY = height - paddingBottom - mBarPadding.bottom;
        if (x >= startX && x <= endX && y >= startY && y <= endY) {
            int position = (int) Math.floor((y - startY) / mItemHeightActual);
            position = position < 0 ? 0 :
                    (position >= mSelection.getItemCount() ?
                            mSelection.getItemCount() - 1 : position);
            return position;
        }
        return mNoticePosition;
    }

    private boolean onTouchEventSlider(MotionEvent event) {
        boolean touch = false;
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        if (mBarBackground != null)
            Compat.setHotspot(mBarBackground,
                    x - (getWidth() - Compat.getPaddingEnd(this) - mBarWidth), y - getPaddingTop());
        boolean superTouch;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (startTouchSlider(x, y)) {
                    setClickable(true);
                    touch = true;
                    showNotice = true;
                    mNoticePosition = getTouchPositionSlider(y);
                    mTouchY = getTouchY(y);
                    invalidate();
                    notifyListener();
                }
                superTouch = super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                superTouch = super.onTouchEvent(event);
                if (showNotice) {
                    showNotice = false;
                    int position = getTouchPositionList(x, y);
                    boolean isPositionChanged = false;
                    if (mNoticePosition != position) {
                        mNoticePosition = position;
                        isPositionChanged = true;
                    }
                    mTouchY = getTouchY(y);
                    invalidate();
                    if (isPositionChanged)
                        notifyListener();
                }
                if (isClickable())
                    setClickable(false);
                break;
            case MotionEvent.ACTION_MOVE:
                if (showNotice) {
                    touch = true;
                    int position = getTouchPositionList(x, y);
                    boolean isPositionChanged = false;
                    if (mNoticePosition != position) {
                        mNoticePosition = position;
                        isPositionChanged = true;
                    }
                    mTouchY = getTouchY(y);
                    invalidate();
                    superTouch = super.onTouchEvent(event);
                    if (x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight() && !isPressed())
                        setPressed(true);
                    if (isPositionChanged)
                        notifyListener();
                } else {
                    superTouch = super.onTouchEvent(event);
                }
                break;
            default:
                superTouch = super.onTouchEvent(event);
                break;
        }
        return superTouch || touch;
    }

    private boolean startTouchSlider(float x, float y) {
        final int endX = getWidth() - Compat.getPaddingEnd(this) - mBarPadding.right;
        return x >= endX - mBarWidth && x <= endX && y >= mSliderCenter - mSliderHeight * 0.5f
                && y <= mSliderCenter + mSliderHeight * 0.5f;
    }

    private float getTouchY(float y) {
        if (y >= getPaddingTop() && y <= getHeight() - getPaddingBottom()) {
            return y;
        }
        return mTouchY;
    }

    private int getTouchPositionSlider(float y) {
        final int startY = getPaddingTop() + mBarPadding.top;
        int position = (int) Math.floor((y - startY) / mItemHeightActual);
        position = position < 0 ? 0 :
                (position >= mSelection.getItemCount() ?
                        mSelection.getItemCount() - 1 : position);
        return position;
    }

    private void notifyListener() {
        if (listener != null)
            listener.onSelected(mNoticePosition);
    }

    @Override
    protected void drawableStateChanged() {
        if (mBarBackground != null && mBarBackground.isStateful()) {
            mBarBackground.setState(getDrawableState());
        }
        if (mBarSlider != null && mBarSlider.isStateful()) {
            mBarSlider.setState(getDrawableState());
        }
        super.drawableStateChanged();

    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        boolean isPress = false;
        if (mBarBackground != null && who == mBarBackground) {
            isPress = true;
        }
        if (mBarSlider != null && who == mBarSlider) {
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
        if (mBarSlider != null) {
            mBarSlider.setCallback(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mBarBackground != null) {
            mBarBackground.setCallback(null);
        }
        if (mBarSlider != null) {
            mBarSlider.setCallback(null);
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
     * 设置控制条背景
     *
     * @param background 背景
     */
    @SuppressWarnings("unused")
    public void setBarBackground(int background) {
        if (background == 0)
            setBarBackground(null);
        setBarBackground(Compat.getDrawable(getContext(), background));
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
            if (mBarStyle == STYLE_SLIDER)
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
     * 设置提示背景
     *
     * @param background 背景
     */
    @SuppressWarnings("unused")
    public void setNoticeBackground(int background) {
        if (background == 0)
            setNoticeBackground(null);
        setNoticeBackground(Compat.getDrawable(getContext(), background));
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
     * 设置提示动画
     *
     * @param animation 开关提示动画
     */
    public void setNoticeAnimation(boolean animation) {
        if (mNoticeAnimation != animation) {
            mNoticeAnimation = animation;
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

    /**
     * 刷新滑块位置
     *
     * @param firstVisibleItem 列表第一个可见子项
     */
    @SuppressWarnings("unused")
    public void refreshSlider(int firstVisibleItem) {
        if (mSelection == null || mSelection.getItemCount() <= firstVisibleItem
                || firstVisibleItem < 0 || mBarStyle != STYLE_SLIDER)
            return;
        float sliderCenter = getPaddingTop() + mBarPadding.top;
        sliderCenter += firstVisibleItem * mItemHeightActual + mItemHeightActual * 0.5f;
        if (mTouchY != sliderCenter) {
            mTouchY = sliderCenter;
            invalidate();
        }
    }

    /**
     * 设置选择监听器
     *
     * @param listener 监听器
     */
    public void setOnSelectedListener(OnSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * 选择器
     */
    public interface Selection {

        int getItemCount();

        Drawable getBar(int position);

        Drawable getNotice(int position);
    }

    /**
     * 选择监听
     */
    public interface OnSelectedListener {
        void onSelected(int position);
    }
}
