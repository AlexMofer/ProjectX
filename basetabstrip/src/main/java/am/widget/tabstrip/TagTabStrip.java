package am.widget.tabstrip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

public class TagTabStrip extends TabStripView {

    private final static int DEFAULT_SIZE = 8;// 默认图片dp
    private final static int DEFAULT_DRAWABLE_SELECTED = 0xff808080;
    private final static int DEFAULT_DRAWABLE_NORMAL = 0x80808080;
    private int mPosition = 0;
    private float mOffset = 0;
    private int mCount = 0;
    private Drawable mSelected;
    private Drawable mNormal;
    private float mScale = 1;
    private int mItemPadding;
    private float mFirstCenterX;
    private float mFirstCenterY;
    private float mItemCenterOffset;

    public TagTabStrip(Context context) {
        super(context);
        initView(context, null);
    }

    public TagTabStrip(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TagTabStrip(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        mSelected = getDefaultDrawable(true);
        mNormal = getDefaultDrawable(false);
        mScale = 1.5f;
        mItemPadding = 10;
    }

    private Drawable getDefaultDrawable(boolean selected) {
        final int size = (int) (getResources().getDisplayMetrics().density * DEFAULT_SIZE);
        if (selected) {
            final GradientDrawable mBackground = new GradientDrawable();
            mBackground.setShape(GradientDrawable.OVAL);
            mBackground.setColor(DEFAULT_DRAWABLE_SELECTED);
            mBackground.setSize(size, size);
            return mBackground;
        } else {
            final GradientDrawable mBackground = new GradientDrawable();
            mBackground.setShape(GradientDrawable.OVAL);
            mBackground.setColor(DEFAULT_DRAWABLE_NORMAL);
            mBackground.setSize(size, size);
            return mBackground;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) {
            mCount = 5;
            mPosition = 2;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingEnd = ViewCompat.getPaddingEnd(this);
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int suggestedMinimumWidth = getSuggestedMinimumWidth();
        final int suggestedMinimumHeight = getSuggestedMinimumHeight();
        final int itemWidth = Math.max(mNormal.getIntrinsicWidth(),
                mSelected.getIntrinsicWidth());
        final int itemHeight = Math.max(mNormal.getIntrinsicHeight(),
                mSelected.getIntrinsicHeight());
        final int width = mCount <= 0 ? 0 : mCount * itemWidth + mItemPadding * (mCount - 1) +
                (mScale > 1 ? (int) Math.ceil(itemWidth * (mScale - 1)) : 0);
        final int height = mScale > 1 ? (int) Math.ceil(itemHeight * mScale) : itemHeight;
        setMeasuredDimension(
                resolveSize(Math.max(width + paddingStart + paddingEnd, suggestedMinimumWidth),
                        widthMeasureSpec),
                resolveSize(Math.max(height + paddingTop + paddingBottom, suggestedMinimumHeight),
                        heightMeasureSpec));
        // TODO
        mFirstCenterX = paddingStart + (mScale > 1 ? itemWidth * mScale * 0.5f : itemWidth * 0.5f);
        mFirstCenterY = (mScale > 1 ? itemHeight * mScale * 0.5f : itemHeight * 0.5f);
        mItemCenterOffset = itemWidth + mItemPadding;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int count = mCount;
        final int position = mPosition;
        final float offset = mOffset;
        final int anotherPosition = mOffset == 0 ? PagerAdapter.POSITION_NONE : mPosition + 1;
        final Drawable selected = mSelected;
        final Drawable normal = mNormal;
        selected.setBounds(0, 0, selected.getIntrinsicWidth(), selected.getIntrinsicHeight());
        normal.setBounds(0, 0, normal.getIntrinsicWidth(), normal.getIntrinsicHeight());
        canvas.save();
        canvas.translate(mFirstCenterX, mFirstCenterY);
        float dx;
        float dy;
        float scale;
        for (int i = 0; i < count; i++) {
            if (i == position) {
                if (offset == 0) {
                    selected.setAlpha(255);
                    scale = mScale;
                    dx = selected.getIntrinsicWidth() * 0.5f;
                    dy = selected.getIntrinsicHeight() * 0.5f;
                    canvas.save();
                    canvas.translate(-dx, -dy);
                    canvas.scale(scale, scale, dx, dy);
                    selected.draw(canvas);
                    canvas.restore();
                } else {
                    selected.setAlpha((int) Math.ceil(0xFF * (1 - offset)));
                    normal.setAlpha((int) Math.ceil(0xFF * offset));
                    scale = 1 + (mScale - 1) * (1 - offset);
                    dx = normal.getIntrinsicWidth() * 0.5f;
                    dy = normal.getIntrinsicHeight() * 0.5f;
                    canvas.save();
                    canvas.translate(-dx, -dy);
                    canvas.scale(scale, scale, dx, dy);
                    normal.draw(canvas);
                    canvas.restore();
                    dx = selected.getIntrinsicWidth() * 0.5f;
                    dy = selected.getIntrinsicHeight() * 0.5f;
                    canvas.save();
                    canvas.translate(-dx, -dy);
                    canvas.scale(scale, scale, dx, dy);
                    selected.draw(canvas);
                    canvas.restore();
                }
            } else if (i == anotherPosition) {
                selected.setAlpha((int) Math.ceil(0xFF * offset));
                normal.setAlpha((int) Math.ceil(0xFF * (1 - offset)));
                scale = 1 + (mScale - 1) * offset;
                dx = normal.getIntrinsicWidth() * 0.5f;
                dy = normal.getIntrinsicHeight() * 0.5f;
                canvas.save();
                canvas.translate(-dx, -dy);
                canvas.scale(scale, scale, dx, dy);
                normal.draw(canvas);
                canvas.restore();
                dx = selected.getIntrinsicWidth() * 0.5f;
                dy = selected.getIntrinsicHeight() * 0.5f;
                canvas.save();
                canvas.translate(-dx, -dy);
                canvas.scale(scale, scale, dx, dy);
                selected.draw(canvas);
                canvas.restore();
            } else {
                normal.setAlpha(255);
                dx = normal.getIntrinsicWidth() * 0.5f;
                dy = normal.getIntrinsicHeight() * 0.5f;
                canvas.save();
                canvas.translate(-dx, -dy);
                normal.draw(canvas);
                canvas.restore();
            }
            canvas.translate(mItemCenterOffset, 0);
        }
        canvas.restore();
    }

    @Override
    protected void onViewPagerChanged(int position, float offset) {
        if (mPosition == position && offset == mOffset)
            return;
        mPosition = position;
        mOffset = offset;
        invalidate();
    }

    @Override
    protected void onViewPagerAdapterChanged(@Nullable PagerAdapter oldAdapter,
                                             @Nullable PagerAdapter newAdapter) {
        super.onViewPagerAdapterChanged(oldAdapter, newAdapter);
        final int count = getPageCount();
        if (count == mCount)
            return;
        mCount = getPageCount();
        requestLayout();
        invalidate();
    }

    @Override
    protected void onViewPagerAdapterDataChanged() {
        super.onViewPagerAdapterDataChanged();
        final int count = getPageCount();
        if (count == mCount)
            return;
        mCount = getPageCount();
        requestLayout();
        invalidate();
    }


}
