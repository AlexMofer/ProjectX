package am.widget.tagtabstrip;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import am.widget.basetabstrip.BaseTabStrip;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

/**
 * ViewPager 小点
 *
 * @author Alex
 */
public class TagTabStrip extends BaseTabStrip {

    private final static int DEFAULT_SIZE = 8;// 默认图片dp
    private final static int DEFAULT_DRAWABLE_SELECTED = 0xff808080;
    private final static int DEFAULT_DRAWABLE_NORMAL = 0x80808080;
    private int defaultDrawableSize;
    private int mGravity = Gravity.CENTER;
    private int drawablePadding;
    private Drawable mSingleDrawable;
    private Drawable mSelectedDrawable;
    private Drawable mNormalDrawable;
    private int mCurrentPager = 0;
    private int mNextPager = 0;
    private float mOffset = 1;
    private int mScaleSpaceX;
    private int mScaleSpaceY;
    private float mOffsetX;
    private float mOffsetY;
    private float mScale = 1;
    private static final int[] ATTRS = new int[]{android.R.attr.gravity, android.R.attr.drawablePadding};

    public TagTabStrip(Context context) {
        this(context, null);
    }

    public TagTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagTabStrip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(false);
        defaultDrawableSize = (int) (getResources().getDisplayMetrics().density * DEFAULT_SIZE);
        int gravity = Gravity.CENTER;
        int padding = 0;
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    gravity = a.getInt(attr, gravity);
                    break;
                case 1:
                    padding = a.getDimensionPixelSize(attr, padding);
                    break;
            }
        }
        a.recycle();
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.TagTabStrip);
        Drawable single = custom.getDrawable(R.styleable.TagTabStrip_ttsDrawable);
        padding = custom.getDimensionPixelSize(R.styleable.TagTabStrip_ttsDrawablePadding, padding);
        float scale = custom.getFloat(R.styleable.TagTabStrip_ttsScale, 1);
        custom.recycle();
        setGravity(gravity);
        setDrawablePadding(padding);
        setScale(scale);
        if (single == null) {
            setDrawables(getDefaultDrawable(false), getDefaultDrawable(true));
        } else {
            setDrawable(single);
        }
    }


    private Drawable getDefaultDrawable(boolean selected) {
        if (selected) {
            final GradientDrawable mBackground = new GradientDrawable();
            mBackground.setShape(GradientDrawable.OVAL);
            mBackground.setColor(DEFAULT_DRAWABLE_SELECTED);
            mBackground.setSize(defaultDrawableSize, defaultDrawableSize);
            return mBackground;
        } else {
            final GradientDrawable mBackground = new GradientDrawable();
            mBackground.setShape(GradientDrawable.OVAL);
            mBackground.setColor(DEFAULT_DRAWABLE_NORMAL);
            mBackground.setSize(defaultDrawableSize, defaultDrawableSize);
            return mBackground;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        final int totalWidth = getTotalWidth();
        final int totalHeight = getTotalHeight();
        int width;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = Math.max(totalWidth, getMinWidth());
            if (widthMode == MeasureSpec.AT_MOST)
                width = Math.min(width, widthSize);
        }
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = Math.max(totalHeight, getMinHeight());
            if (heightMode == MeasureSpec.AT_MOST)
                height = Math.min(height, heightSize);
        }
        setMeasuredDimension(width, height);
        makeGravity(width, height, totalWidth, totalHeight);
    }

    @SuppressWarnings("all")
    private void makeGravity(int width, int height, int totalWidth, int totalHeight) {
        switch (GravityCompat.getAbsoluteGravity(mGravity, ViewCompat.getLayoutDirection(this))) {
            default:
            case Gravity.CENTER:
                mOffsetX = (width - totalWidth) * 0.5f;
                mOffsetY = (height - totalHeight) * 0.5f;
                break;
            case Gravity.CENTER_HORIZONTAL:
            case Gravity.CENTER_HORIZONTAL | Gravity.TOP:
                mOffsetX = (width - totalWidth) * 0.5f;
                mOffsetY = 0;
                break;
            case Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM:
                mOffsetX = (width - totalWidth) * 0.5f;
                mOffsetY = height - totalHeight;
                break;
            case Gravity.CENTER_VERTICAL:
            case Gravity.CENTER_VERTICAL | GravityCompat.START:
            case Gravity.CENTER_VERTICAL | Gravity.LEFT:
                mOffsetX = 0;
                mOffsetY = (height - totalHeight) * 0.5f;
                break;
            case Gravity.CENTER_VERTICAL | GravityCompat.END:
            case Gravity.CENTER_VERTICAL | Gravity.RIGHT:
                mOffsetX = width - totalWidth;
                mOffsetY = (height - totalHeight) * 0.5f;
                break;
            case GravityCompat.START:
            case Gravity.LEFT:
            case Gravity.TOP:
            case GravityCompat.START | Gravity.TOP:
            case Gravity.LEFT | Gravity.TOP:
                mOffsetX = 0;
                mOffsetY = 0;
                break;
            case GravityCompat.END:
            case GravityCompat.END | Gravity.TOP:
            case Gravity.RIGHT | Gravity.TOP:
                mOffsetX = width - totalWidth;
                mOffsetY = 0;
                break;
            case GravityCompat.END | Gravity.BOTTOM:
            case Gravity.RIGHT | Gravity.BOTTOM:
                mOffsetX = width - totalWidth;
                mOffsetY = height - totalHeight;
                break;
            case Gravity.BOTTOM:
            case Gravity.BOTTOM | GravityCompat.START:
            case Gravity.BOTTOM | Gravity.LEFT:
                mOffsetX = 0;
                mOffsetY = height - totalHeight;
                break;
        }
    }

    /**
     * 获取控件所需的总宽度
     *
     * @return 控件所需的总宽度
     */
    private int getTotalWidth() {
        int gap = getItemCount() > 0 ? drawablePadding * (getItemCount() - 1) : 0;
        final int itemWidth = getItemWidth();
        mScaleSpaceX = 0;
        if (mScale > 1) {
            mScaleSpaceX = (int) (Math.ceil(((float) itemWidth) * mScale - itemWidth) * 0.5f) + 1;
        }
        return mScaleSpaceX * 2 + itemWidth * getItemCount() + gap +
                ViewCompat.getPaddingStart(this) + ViewCompat.getPaddingEnd(this);
    }


    /**
     * 获取控件所需的总高度
     *
     * @return 控件所需的总高度
     */
    private int getTotalHeight() {
        final int itemHeight = getItemHeight();
        mScaleSpaceY = 0;
        if (mScale > 1) {
            mScaleSpaceY = (int) (Math.ceil(((float) itemHeight) * mScale - itemHeight) * 0.5f) + 1;
        }
        return mScaleSpaceY * 2 + itemHeight + getPaddingTop() + getPaddingBottom();
    }

    /**
     * 获取子项宽度
     *
     * @return 子项宽度
     */
    private int getItemWidth() {
        if (!hasDrawable()) {
            return 0;
        }
        int itemWidth = defaultDrawableSize;
        int drawableWidth = 0;
        if (mSingleDrawable != null) {
            drawableWidth = mSingleDrawable.getIntrinsicWidth();
        }
        if (mNormalDrawable != null && mSelectedDrawable != null) {
            drawableWidth = Math.max(mNormalDrawable.getIntrinsicWidth(),
                    mSelectedDrawable.getIntrinsicWidth());
        }
        return Math.max(itemWidth, drawableWidth);
    }

    /**
     * 获取子项高度
     *
     * @return 子项高度
     */
    private int getItemHeight() {
        if (!hasDrawable()) {
            return 0;
        }
        int itemHeight = defaultDrawableSize;
        int drawableHeight = 0;
        if (mSingleDrawable != null) {
            drawableHeight = mSingleDrawable.getIntrinsicHeight();
        }
        if (mNormalDrawable != null && mSelectedDrawable != null) {
            drawableHeight = Math.max(mNormalDrawable.getIntrinsicHeight(),
                    mSelectedDrawable.getIntrinsicHeight());
        }
        return Math.max(itemHeight, drawableHeight);
    }

    /**
     * 判断是否有子项
     *
     * @return 是否有子项
     */
    private boolean hasDrawable() {
        return mSingleDrawable != null || (mNormalDrawable != null && mSelectedDrawable != null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            mNextPager = 2;
        }
        super.onDraw(canvas);
        if (!hasDrawable())
            return;
        final int startX = getPaddingLeft();
        final int startY = getPaddingTop();
        final int itemWidth = getItemWidth();
        final int itemHeight = getItemHeight();
        canvas.save();
        canvas.translate(mOffsetX, mOffsetY);
        canvas.translate(startX, startY);
        canvas.translate(mScaleSpaceX, mScaleSpaceY);
        float alphaNormal;
        float alphaSelected;
        float scale;
        for (int i = 0; i < getItemCount(); i++) {
            if (i == mNextPager) {
                alphaNormal = 1 - mOffset;
                alphaSelected = mOffset;
                scale = 1 + (mScale - 1) * mOffset;
            } else if (i == mCurrentPager) {
                alphaNormal = mOffset;
                alphaSelected = 1 - mOffset;
                scale = 1 + (mScale - 1) * (1 - mOffset);
            } else {
                alphaNormal = 1;
                alphaSelected = 0;
                scale = 1;
            }
            if (scale != 1) {
                canvas.save();
                canvas.scale(scale, scale, itemWidth * 0.5f, itemHeight * 0.5f);
            }

            if (mSingleDrawable != null) {
                mSingleDrawable.setBounds(0, 0, itemWidth, itemHeight);
                mSingleDrawable.draw(canvas);
            }
            if (mNormalDrawable != null && mSelectedDrawable != null) {
                mNormalDrawable.setAlpha((int) Math.ceil(0xFF * alphaNormal));
                mSelectedDrawable.setAlpha((int) Math.ceil(0xFF * alphaSelected));
                mNormalDrawable.setBounds(0, 0, itemWidth, itemHeight);
                mSelectedDrawable.setBounds(0, 0, itemWidth, itemHeight);
                mNormalDrawable.draw(canvas);
                mSelectedDrawable.draw(canvas);
            }
            if (scale > 1) {
                canvas.restore();
            }
            canvas.translate(itemWidth + drawablePadding, 0);
        }
        canvas.restore();
    }

    @Override
    protected void jumpTo(int current) {
        mCurrentPager = current - 1;
        mNextPager = current;
        mOffset = 1;
        invalidate();
    }

    @Override
    protected void gotoLeft(int current, int next, float offset) {
        mCurrentPager = current;
        mNextPager = next;
        mOffset = 1 - offset;
        invalidate();
    }

    @Override
    protected void gotoRight(int current, int next, float offset) {
        mCurrentPager = current;
        mNextPager = next;
        mOffset = offset;
        invalidate();
    }

    @Override
    protected int getItemCount() {
        if (isInEditMode()) {
            return 5;
        }
        return super.getItemCount();
    }

    /**
     * 设置排版方式
     *
     * @param gravity 排版方式
     */
    public void setGravity(int gravity) {
        mGravity = gravity;
        requestLayout();
        invalidate();
    }

    /**
     * 获取排版方式
     *
     * @return 排版方式
     */
    @SuppressWarnings("unused")
    public int getGravity() {
        return mGravity;
    }

    /**
     * 设置子项间距
     *
     * @param padding 子项间距
     */
    public void setDrawablePadding(int padding) {
        drawablePadding = padding;
        requestLayout();
        invalidate();
    }

    /**
     * 获取子项间距
     *
     * @return 子项间距
     */
    @SuppressWarnings("unused")
    public int getDrawablePadding() {
        return drawablePadding;
    }

    /**
     * 设置选中子项缩放比
     *
     * @param scale 选中子项缩放比
     */
    public void setScale(float scale) {
        if (scale > 0 && scale != mScale) {
            mScale = scale;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 获取选中子项缩放比
     *
     * @return 选中子项缩放比
     */
    @SuppressWarnings("unused")
    public float getScale() {
        return mScale;
    }

    /**
     * 设置子项图片
     *
     * @param item 子项图片
     */
    public void setDrawable(Drawable item) {
        if (item != null && item.isStateful()) {
            Drawable normal = item.getConstantState().newDrawable();
            Drawable selected = item.getConstantState().newDrawable();
            selected.setState(SELECTED_STATE_SET);
            setDrawables(normal, selected);
        } else {
            mSingleDrawable = item;
            mNormalDrawable = null;
            mSelectedDrawable = null;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置图片
     *
     * @param normal   普通图
     * @param selected 选中图
     */
    public void setDrawables(Drawable normal, Drawable selected) {
        mSingleDrawable = null;
        mNormalDrawable = normal;
        mSelectedDrawable = selected;
        requestLayout();
        invalidate();
    }

    /**
     * 设置图片
     *
     * @param normal   普通图
     * @param selected 选中图
     */
    @SuppressWarnings("unused")
    public void setDrawables(int normal, int selected) {
        setDrawables(ContextCompat.getDrawable(getContext(), normal),
                ContextCompat.getDrawable(getContext(), selected));
    }

    /**
     * 设置子项图片
     *
     * @param item 子项图片
     */
    public void setDrawable(int item) {
        setDrawable(ContextCompat.getDrawable(getContext(), item));
    }

}
