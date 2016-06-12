package am.widget.wraplayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 自动换行布局
 */
public class WrapLayout extends ViewGroup {

    private static final int[] ATTRS = new int[]{android.R.attr.horizontalSpacing,
            android.R.attr.verticalSpacing};
    private int mVerticalSpacing;
    private int mHorizontalSpacing;
    private int mNumRows = 0;
    private ArrayList<Integer> mNumColumns = new ArrayList<>();

    public WrapLayout(Context context) {
        this(context, null);
    }

    public WrapLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS, defStyleAttr, 0);
        int n = a.getIndexCount();
        int horizontalSpacing = 0;
        int verticalSpacing = 0;
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    horizontalSpacing = a.getDimensionPixelSize(attr, horizontalSpacing);
                    break;
                case 1:
                    verticalSpacing = a.getDimensionPixelSize(attr, verticalSpacing);
                    break;
            }
        }
        a.recycle();
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.WrapLayout);
        horizontalSpacing = custom.getDimensionPixelSize(
                R.styleable.WrapLayout_wlyHorizontalSpacing, horizontalSpacing);
        verticalSpacing = custom.getDimensionPixelSize(
                R.styleable.WrapLayout_wlyVerticalSpacing, verticalSpacing);
        custom.recycle();
        setHorizontalSpacing(horizontalSpacing);
        setVerticalSpacing(verticalSpacing);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int paddingStart = Compat.getPaddingStart(this);
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int suggestedMinimumWidth = getSuggestedMinimumWidth();
        final int suggestedMinimumHeight = getSuggestedMinimumHeight();
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int itemsWidth = 0;
        int itemsHeight = 0;
        mNumRows = 0;
        mNumColumns.clear();
        if (getChildCount() > 0) {
            if (widthMode == MeasureSpec.UNSPECIFIED) {
                int numColumns = 0;
                for (int index = 0; index < getChildCount(); index++) {
                    View child = getChildAt(index);
                    if (child.getVisibility() == View.GONE) {
                        continue;
                    }
                    if (mNumRows == 0)
                        mNumRows = 1;
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                    final int childWidth = child.getMeasuredWidth();
                    final int childHeight = child.getMeasuredHeight();
                    if (numColumns == 0) {
                        itemsWidth = -mHorizontalSpacing;
                    }
                    itemsWidth += mHorizontalSpacing + childWidth;
                    itemsHeight = Math.max(childHeight, itemsHeight);
                    numColumns++;
                }
                if (numColumns != 0)
                    mNumColumns.add(numColumns);
            } else {
                int numColumns = 0;
                final int maxItemsWidth = widthSize - paddingStart - paddingEnd;
                int rowWidth = 0;
                int rowHeight = 0;
                for (int index = 0; index < getChildCount(); index++) {
                    View child = getChildAt(index);
                    if (child.getVisibility() == View.GONE) {
                        continue;
                    }
                    if (mNumRows == 0)
                        mNumRows = 1;
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                    final int childWidth = child.getMeasuredWidth();
                    final int childHeight = child.getMeasuredHeight();
                    if (numColumns == 0) {
                        rowWidth = -mHorizontalSpacing;
                    }
                    if (rowWidth + childWidth + mHorizontalSpacing <= maxItemsWidth) {
                        rowWidth += childWidth + mHorizontalSpacing;
                        rowHeight = Math.max(childHeight, rowHeight);
                        numColumns++;
                    } else {
                        itemsWidth = Math.max(rowWidth, itemsWidth);
                        itemsHeight += mNumRows == 1 ? rowHeight : mVerticalSpacing + rowHeight;
                        mNumColumns.add(numColumns);
                        mNumRows++;
                        rowWidth = 0;
                        rowHeight = 0;
                        numColumns = 0;
                        rowWidth += childWidth;
                        rowHeight = Math.max(childHeight, rowHeight);
                        numColumns++;
                    }
                }
                if (numColumns != 0) {
                    itemsHeight += mNumRows == 1 ? rowHeight : mVerticalSpacing + rowHeight;
                    mNumColumns.add(numColumns);
                }
            }
        }
        itemsWidth = Math.max(paddingStart + itemsWidth + paddingEnd, suggestedMinimumWidth);
        itemsHeight = Math.max(paddingTop + itemsHeight + paddingBottom, suggestedMinimumHeight);
        setMeasuredDimension(resolveSize(itemsWidth, widthMeasureSpec),
                resolveSize(itemsHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingStart = Compat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        int numChild = 0;
        int columnTop = paddingTop - mVerticalSpacing;
        for (int row = 0; row < mNumRows; row++) {
            int numColumn = mNumColumns.get(row);
            int maxChildHeight = 0;
            int startX = paddingStart - mHorizontalSpacing;
            for (int index = 0; index < numColumn; ) {
                View childView = getChildAt(numChild);
                numChild++;
                if (childView == null || childView.getVisibility() == View.GONE) {
                    continue;
                }
                final int childWidth = childView.getMeasuredWidth();
                final int childHeight = childView.getMeasuredHeight();
                maxChildHeight = Math.max(childHeight, maxChildHeight);
                startX += mHorizontalSpacing;
                int startY = columnTop + mVerticalSpacing;
                childView.layout(startX, startY, startX + childWidth, startY + childHeight);
                startX += childWidth;
                index++;
            }
            columnTop += mVerticalSpacing + maxChildHeight;
        }
    }

    /**
     * 获取水平间距
     *
     * @return 水平间距
     */
    @SuppressWarnings("unused")
    public int getHorizontalSpacing() {
        return mHorizontalSpacing;
    }

    /**
     * 设置水平间距
     *
     * @param pixelSize 水平间距
     */
    public void setHorizontalSpacing(int pixelSize) {
        mHorizontalSpacing = pixelSize;
        requestLayout();
    }

    /**
     * 获取垂直间距
     *
     * @return 垂直间距
     */
    @SuppressWarnings("unused")
    public int getVerticalSpacing() {
        return mVerticalSpacing;
    }

    /**
     * 设置垂直间距
     *
     * @param pixelSize 垂直间距
     */
    public void setVerticalSpacing(int pixelSize) {
        mVerticalSpacing = pixelSize;
        requestLayout();
    }

    /**
     * 获取行数目
     *
     * @return 行数目
     */
    @SuppressWarnings("unused")
    public int getNumRows() {
        return mNumRows;
    }

    /**
     * 获取某一行列数目
     *
     * @param index 行号
     * @return 列数目
     */
    @SuppressWarnings("unused")
    public int getNumColumns(int index) {
        int numColumns = -1;
        if (index < 0 || index >= mNumColumns.size()) {
            return numColumns;
        }
        return mNumColumns.get(index);
    }

}
