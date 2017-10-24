/*
 * Copyright (C) 2015 AlexMofer
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

package am.widget.headerfootergridview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.util.ArrayList;


/**
 * 头尾GridView
 *
 * @author Alex
 */
public class HeaderFooterGridView extends GridView {

    private final ArrayList<View> mHeaderItems = new ArrayList<>();
    private final ArrayList<View> mFooterItems = new ArrayList<>();
    private final ArrayList<FixedViewInfo> mHeaderItemInfo = new ArrayList<>();
    private final ArrayList<FixedViewInfo> mFooterItemInfo = new ArrayList<>();
    private final ArrayList<View> mHeaderViews = new ArrayList<>();
    private final ArrayList<View> mFooterViews = new ArrayList<>();
    private final LayoutParams abLayoutParams = new LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, 0);
    private HeaderFooterViewListAdapter mAdapter;
    private int mNumColumns;
    private int mHorizontalSpacing;
    private int mRequestedHorizontalSpacing;
    private int mVerticalSpacing;
    private int mColumnWidth;
    private int mRequestedColumnWidth;
    private int mRequestedNumColumns;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private OnItemSelectedListener selectedListener;

    public HeaderFooterGridView(Context context) {
        super(context);
        initView();
    }

    public HeaderFooterGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HeaderFooterGridView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    @TargetApi(21)
    @SuppressWarnings("unused")
    public HeaderFooterGridView(Context context, AttributeSet attrs, int defStyleAttr,
                                int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        HeaderFooterListener listener = new HeaderFooterListener();
        super.setOnItemClickListener(listener);
        super.setOnItemLongClickListener(listener);
        super.setOnItemSelectedListener(listener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int availableSpace = MeasureSpec.getSize(widthMeasureSpec)
                - getPaddingLeft() - getPaddingRight();
        if (mRequestedNumColumns == AUTO_FIT) {
            // AUTO_FIT模式，需要以行间距及列宽来计算。
            if (mRequestedColumnWidth == 0) {
                // 未指定列宽或者指定列宽为0，GridView强制列数为2
                mNumColumns = 2;
            } else {
                mNumColumns = (availableSpace + mRequestedHorizontalSpacing)
                        / (mRequestedColumnWidth + mRequestedHorizontalSpacing);
                mNumColumns = mNumColumns < 1 ? 1 : mNumColumns;// 一行都放不下的特殊情况就不考虑了

            }
        } else {
            mNumColumns = mRequestedNumColumns;
        }
        final int stretchMode = getStretchMode();
        if (stretchMode == NO_STRETCH) {
            // 列间距不变，列宽不变
            mHorizontalSpacing = mRequestedHorizontalSpacing;
            mColumnWidth = mRequestedColumnWidth;
        } else {
            int spaceLeftOver = availableSpace
                    - (mNumColumns * mRequestedColumnWidth)
                    - ((mNumColumns - 1) * mRequestedHorizontalSpacing);
            switch (stretchMode) {
                case STRETCH_COLUMN_WIDTH:
                    // 列间距不变，列宽改变
                    mHorizontalSpacing = mRequestedHorizontalSpacing;
                    mColumnWidth = mRequestedColumnWidth + spaceLeftOver
                            / mNumColumns;
                    break;
                case STRETCH_SPACING:
                    // 列间距改变，列宽不变
                    mColumnWidth = mRequestedColumnWidth;
                    if (mNumColumns > 1) {
                        mHorizontalSpacing = mRequestedHorizontalSpacing
                                + spaceLeftOver / (mNumColumns - 1);
                    } else {
                        mHorizontalSpacing = mRequestedHorizontalSpacing
                                + spaceLeftOver;
                    }
                    break;
                case STRETCH_SPACING_UNIFORM:
                    // 列间距改变，列宽不变
                    mColumnWidth = mRequestedColumnWidth;
                    if (mNumColumns > 1) {
                        mHorizontalSpacing = mRequestedHorizontalSpacing
                                + spaceLeftOver / (mNumColumns + 1);
                    } else {
                        mHorizontalSpacing = mRequestedHorizontalSpacing
                                + spaceLeftOver;
                    }
                    break;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 完成计算，为达到高精确度，进行一下高版本数据确认：
        mNumColumns = getNumColumnsCompat();
        mVerticalSpacing = getVerticalSpacingCompat();
        mHorizontalSpacing = getHorizontalSpacingCompat();
        mColumnWidth = getColumnWidthCompat();
        mRequestedHorizontalSpacing = getRequestedHorizontalSpacingCompat();
        mRequestedColumnWidth = getRequestedColumnWidthCompat();
        if (mAdapter != null && mAdapter.getNumColumns() != mNumColumns)
            mAdapter.setNumColumns(mNumColumns);
        abLayoutParams.width = getWidth() - getPaddingLeft()
                - getPaddingRight();
    }

    @Override
    protected void layoutChildren() {
        // 进行头尾View的长宽控制，View创建及Adapter更改才会调用
        for (View header : mHeaderViews) {
            ViewGroup.LayoutParams p = header.getLayoutParams();
            if (p != null) {
                p.width = getWidth() - getPaddingLeft() - getPaddingRight();
                header.setLayoutParams(p);
            } else {
                header.setLayoutParams(abLayoutParams);
            }
        }
        for (View footer : mFooterViews) {
            ViewGroup.LayoutParams p = footer.getLayoutParams();
            if (p != null) {
                p.width = getWidth() - getPaddingLeft() - getPaddingRight();
                footer.setLayoutParams(p);
            } else {
                footer.setLayoutParams(abLayoutParams);
            }
        }
        super.layoutChildren();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // 设置头尾View的偏移，只重新绘制将要显示的
        if (mAdapter != null) {
            int startHeaderIndex, endHeaderIndex, startFooterIndex, endFooterIndex;
            if (mAdapter.getItemCount() == 0) {
                // 头尾View均有显示
                if (mHeaderViews.size() > 0) {
                    startHeaderIndex = mAdapter
                            .positionToHeaderViewIndex(getFirstVisiblePosition());
                    endHeaderIndex = mAdapter
                            .positionToHeaderViewIndex(getLastVisiblePosition());
                    endHeaderIndex = endHeaderIndex == -1 ? mHeaderViews.size() - 1
                            : endHeaderIndex;
                    translateView(startHeaderIndex, endHeaderIndex,
                            mHeaderViews);
                }
                if (mFooterViews.size() > 0) {
                    startFooterIndex = mAdapter
                            .positionToFooterViewIndex(getFirstVisiblePosition());
                    startFooterIndex = startFooterIndex == -1 ? 0
                            : startFooterIndex;
                    endFooterIndex = mAdapter
                            .positionToFooterViewIndex(getLastVisiblePosition());
                    translateView(startFooterIndex, endFooterIndex,
                            mFooterViews);
                }
            } else if (getFirstVisiblePosition() < mAdapter
                    .getFirstItemPosition()
                    && getLastVisiblePosition() > mAdapter
                    .getLastItemPosition()) {
                // 头尾View均有显示
                if (mHeaderViews.size() > 0) {
                    startHeaderIndex = mAdapter
                            .positionToHeaderViewIndex(getFirstVisiblePosition());
                    endHeaderIndex = mAdapter
                            .positionToHeaderViewIndex(getLastVisiblePosition());
                    endHeaderIndex = endHeaderIndex == -1 ? mHeaderViews.size() - 1
                            : endHeaderIndex;
                    translateView(startHeaderIndex, endHeaderIndex,
                            mHeaderViews);
                }
                if (mFooterViews.size() > 0) {
                    startFooterIndex = mAdapter
                            .positionToFooterViewIndex(getFirstVisiblePosition());
                    startFooterIndex = startFooterIndex == -1 ? 0
                            : startFooterIndex;
                    endFooterIndex = mAdapter
                            .positionToFooterViewIndex(getLastVisiblePosition());
                    translateView(startFooterIndex, endFooterIndex,
                            mFooterViews);
                }
            } else if (getFirstVisiblePosition() < mAdapter
                    .getFirstItemPosition()) {
                // 头View有显示
                if (mHeaderViews.size() > 0) {
                    startHeaderIndex = mAdapter
                            .positionToHeaderViewIndex(getFirstVisiblePosition());
                    endHeaderIndex = mAdapter
                            .positionToHeaderViewIndex(getLastVisiblePosition());
                    endHeaderIndex = endHeaderIndex == -1 ? mHeaderViews.size() - 1
                            : endHeaderIndex;
                    translateView(startHeaderIndex, endHeaderIndex,
                            mHeaderViews);
                }
            } else if (getLastVisiblePosition() > mAdapter
                    .getLastItemPosition()) {
                // 尾View有显示
                if (mFooterViews.size() > 0) {
                    startFooterIndex = mAdapter
                            .positionToFooterViewIndex(getFirstVisiblePosition());
                    startFooterIndex = startFooterIndex == -1 ? 0
                            : startFooterIndex;
                    endFooterIndex = mAdapter
                            .positionToFooterViewIndex(getLastVisiblePosition());
                    translateView(startFooterIndex, endFooterIndex,
                            mFooterViews);
                }
            }
        }
        super.dispatchDraw(canvas);
    }

    private void translateView(int startIndex, int endIndex,
                               ArrayList<View> listViews) {
        for (int i = startIndex; i <= endIndex && i < listViews.size()
                && i >= 0; i++) {
            View header = listViews.get(i);
            header.layout(getPaddingLeft(), header.getTop(), getWidth()
                    - getPaddingRight(), header.getTop() + header.getHeight());
        }
    }

    /**
     * Returns the adapter currently in use in this GridView. The returned adapter
     * might not be the same adapter passed to {@link #setAdapter(ListAdapter)} but
     * might be a {@link WrapperListAdapter}.
     *
     * @return The adapter currently used to display data in this GridView.
     * @see #setAdapter(ListAdapter)
     */
    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Sets the data behind this GridView.
     * The adapter passed to this method may be wrapped by a {@link WrapperListAdapter},
     * depending on the GridView features currently in use. For instance, adding
     * headers and/or footers will cause the adapter to be wrapped.
     *
     * @param adapter The GridView which is responsible for maintaining the
     *                data backing this list and for producing a view to represent an
     *                item in that data set.
     * @see #getAdapter()
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        mAdapter = new HeaderFooterViewListAdapter(mHeaderItemInfo,
                mFooterItemInfo, adapter, mHeaderViews, mFooterViews,
                mNumColumns);
        super.setAdapter(mAdapter);
    }

    @Override
    public void setVerticalSpacing(int verticalSpacing) {
        mVerticalSpacing = verticalSpacing;
        super.setVerticalSpacing(verticalSpacing);
    }

    /**
     * Returns the amount of vertical spacing between each item in the grid.
     *
     * @return The vertical spacing between items in pixels
     * @see #setVerticalSpacing(int)
     * @see #getVerticalSpacing()
     */
    public int getVerticalSpacingCompat() {
        if (Build.VERSION.SDK_INT >= 16)
            return getVerticalSpacing();
        else
            return mVerticalSpacing;
    }

    @Override
    public void setHorizontalSpacing(int horizontalSpacing) {
        mRequestedHorizontalSpacing = horizontalSpacing;
        super.setHorizontalSpacing(horizontalSpacing);
    }

    /**
     * Returns the amount of horizontal spacing currently used between each item in the grid.
     * This is only accurate for the current layout. If {@link #setHorizontalSpacing(int)}
     * has been called but layout is not yet complete, this method may return a stale value.
     * To get the horizontal spacing that was explicitly requested use
     * {@link #getRequestedHorizontalSpacing()}.
     *
     * @return Current horizontal spacing between each item in pixels
     * @see #setHorizontalSpacing(int)
     * @see #getRequestedHorizontalSpacing()
     * @see #getHorizontalSpacing()
     */
    public int getHorizontalSpacingCompat() {
        if (Build.VERSION.SDK_INT >= 16)
            return getHorizontalSpacing();
        else
            return mHorizontalSpacing;
    }

    /**
     * Returns the requested amount of horizontal spacing between each item in the grid.
     * The value returned may have been supplied during inflation as part of a style,
     * the default GridView style, or by a call to {@link #setHorizontalSpacing(int)}.
     * If layout is not yet complete or if GridView calculated a different horizontal spacing
     * from what was requested, this may return a different value from
     * {@link #getHorizontalSpacing()}.
     *
     * @return The currently requested horizontal spacing between items, in pixels
     * @see #setHorizontalSpacing(int)
     * @see #getHorizontalSpacing()
     * @see #getRequestedHorizontalSpacing()
     */
    public int getRequestedHorizontalSpacingCompat() {
        if (Build.VERSION.SDK_INT >= 16)
            return getRequestedHorizontalSpacing();
        else
            return mRequestedHorizontalSpacing;
    }

    @Override
    public void setColumnWidth(int columnWidth) {
        mRequestedColumnWidth = columnWidth;
        super.setColumnWidth(columnWidth);
    }

    /**
     * Return the width of a column in the grid.
     * This may not be valid yet if a layout is pending.
     *
     * @return The column width in pixels
     * @see #setColumnWidth(int)
     * @see #getRequestedColumnWidth()
     * @see #getColumnWidth()
     */
    public int getColumnWidthCompat() {
        if (Build.VERSION.SDK_INT >= 16)
            return getColumnWidth();
        else
            return mColumnWidth;
    }

    /**
     * Return the requested width of a column in the grid.
     * This may not be the actual column width used. Use {@link #getColumnWidth()}
     * to retrieve the current real width of a column.
     *
     * @return The requested column width in pixels
     * @see #setColumnWidth(int)
     * @see #getColumnWidth()
     * @see #getRequestedColumnWidth()
     */
    public int getRequestedColumnWidthCompat() {
        if (Build.VERSION.SDK_INT >= 16)
            return getRequestedColumnWidth();
        else
            return mRequestedColumnWidth;
    }

    @Override
    public void setNumColumns(int numColumns) {
        mRequestedNumColumns = numColumns;
        super.setNumColumns(numColumns);
    }

    /**
     * Get the number of columns in the grid.
     * Returns {@link #AUTO_FIT} if the Grid has never been laid out.
     *
     * @return number of columns
     * @see #setNumColumns(int)
     * @see #getNumColumns()
     */
    public int getNumColumnsCompat() {
        if (Build.VERSION.SDK_INT >= 11)
            return getNumColumns();
        else
            return mNumColumns;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        longClickListener = listener;
    }

    @Override
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        selectedListener = listener;
    }

    @Override
    public void setItemChecked(int position, boolean value) {
        super.setItemChecked(
                position + (mAdapter == null ? 0 : mAdapter.getHeaderViewsCount()), value);
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        return super.performItemClick(view,
                position + (mAdapter == null ? 0 : mAdapter.getHeaderViewsCount()), id);
    }

    /**
     * Add a fixed view to appear at the first of the grid. If this method
     * is called more than once, the views will appear in the order they
     * were added. Views added using this call can take focus if they want.
     * Note: When first introduced, this method could only be called before
     * setting the adapter with {@link #setAdapter(ListAdapter)}. this
     * method may be called at any time. If the GridView's adapter does not
     * extend {@link HeaderFooterViewListAdapter}, it will be wrapped with
     * a supporting instance of {@link WrapperListAdapter}.
     *
     * @param v            The view to add.
     * @param data         Data to associate with this view
     * @param isSelectable whether the item is selectable
     */
    public void addHeaderItem(View v, Object data, boolean isSelectable) {
        if (mHeaderItems.contains(v))
            return;
        mHeaderItems.add(0, v);
        final FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mHeaderItemInfo.add(0, info);
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    /**
     * Add a fixed view to appear at the first of the grid. If this method
     * is called more than once, the views will appear in the order they
     * were added. Views added using this call can take focus if they want.
     * Note: When first introduced, this method could only be called before
     * setting the adapter with {@link #setAdapter(ListAdapter)}. this
     * method may be called at any time. If the GridView's adapter does not
     * extend {@link HeaderFooterViewListAdapter}, it will be wrapped with
     * a supporting instance of {@link WrapperListAdapter}.
     *
     * @param v The view to add.
     */
    @SuppressWarnings("unused")
    public void addHeaderItem(View v) {
        addHeaderItem(v, null, false);
    }

    @SuppressWarnings("unused")
    public int getHeaderItemsCount() {
        return mHeaderItems.size();
    }

    /**
     * Removes a previously-added header view.
     *
     * @param v The view to remove
     * @return true if the view was removed, false if the view was not a
     * header view
     */
    @SuppressWarnings("unused")
    public boolean removeHeaderItem(View v) {
        return mHeaderItems.remove(v) && mAdapter != null && mAdapter.removeHeaderItem(v);
    }

    /**
     * Add a fixed view to appear at the last of the grid. If this method
     * is called more than once, the views will appear in the order they
     * were added. Views added using this call can take focus if they want.
     * Note: When first introduced, this method could only be called before
     * setting the adapter with {@link #setAdapter(ListAdapter)}. this
     * method may be called at any time. If the GridView's adapter does not
     * extend {@link HeaderFooterViewListAdapter}, it will be wrapped with
     * a supporting instance of {@link WrapperListAdapter}.
     *
     * @param v            The view to add.
     * @param data         Data to associate with this view
     * @param isSelectable true if the footer view can be selected
     */
    public void addFooterItem(View v, Object data, boolean isSelectable) {
        if (mFooterItems.contains(v))
            return;
        mFooterItems.add(v);
        final FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mFooterItemInfo.add(info);
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }


    /**
     * Add a fixed view to appear at the last of the grid. If this method
     * is called more than once, the views will appear in the order they
     * were added. Views added using this call can take focus if they want.
     * Note: When first introduced, this method could only be called before
     * setting the adapter with {@link #setAdapter(ListAdapter)}. this
     * method may be called at any time. If the GridView's adapter does not
     * extend {@link HeaderFooterViewListAdapter}, it will be wrapped with
     * a supporting instance of {@link WrapperListAdapter}.
     *
     * @param v The view to add.
     */
    @SuppressWarnings("unused")
    public void addFooterItem(View v) {
        addFooterItem(v, null, false);
    }

    @SuppressWarnings("unused")
    public int getFooterItemsCount() {
        return mFooterItems.size();
    }

    /**
     * Removes a previously-added footer view.
     *
     * @param v The view to remove
     * @return true if the view was removed, false if the view was not a
     * footer view
     */
    @SuppressWarnings("unused")
    public boolean removeFooterItem(View v) {
        return mFooterItems.remove(v) && mAdapter != null && mAdapter.removeFooterItem(v);
    }

    /**
     * 在网格顶部添加View，网格将空出一行用来放置该View，空出的位置由空白View充填，
     * 填充的空白View不可点击，且不具备状态，但是其占据position，通过{@link #getHeaderViewsCount()}
     * 方法可获取占据的空白View位置。
     *
     * @param v 待添加的View
     */
    @SuppressWarnings("unused")
    public void addHeaderView(View v) {
        if (mHeaderViews.contains(v))
            return;
        mHeaderViews.add(0, v);
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    /**
     * 获取网格顶部添加的View数目。
     *
     * @return 网格顶部添加的View数目
     */
    @SuppressWarnings("unused")
    public int getHeaderViewsCount() {
        return mHeaderViews.size();
    }

    /**
     * 获取在网格顶部添加的View导致其占据的空白View数目，并非顶部添加的View个数。跟
     * 顶部添加的View个数及列数目有关。
     *
     * @return 不可见的空白View数目
     */
    @SuppressWarnings("unused")
    public int getHeaderEmptyViewsCount() {
        return mAdapter == null ? 0 : mAdapter.getHeaderViewsCount();
    }

    /**
     * 移除网格顶部添加的View
     *
     * @param v 待移除的View
     * @return 是否成功移除
     */
    @SuppressWarnings("unused")
    public boolean removeHeaderView(View v) {
        boolean finished = mHeaderViews.remove(v);
        if (finished && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        return finished;
    }

    /**
     * 在网格底部添加View，网格将空出一行用来放置该View，空出的位置由空白View充填，
     * 填充的空白View不可点击，且不具备状态，但是其占据position，通过{@link #getHeaderViewsCount()}
     * 方法可获取占据的空白View位置，网格最后一行子项没有站满一行，则也会用空白View
     * 充填至满一行。
     *
     * @param v 待添加的View
     */
    @SuppressWarnings("unused")
    public void addFooterView(View v) {
        if (mFooterViews.contains(v))
            return;
        mFooterViews.add(v);
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    /**
     * 获取网格底部添加的View数目。
     *
     * @return 网格底部添加的View数目
     */
    @SuppressWarnings("unused")
    public int getFooterViewsCount() {
        return mFooterViews.size();
    }

    /**
     * 获取在网格底部添加的View导致其占据的空白View数目，并非底部添加的View个数。跟
     * 底部添加的View个数、列数目以及子项数目有关。
     *
     * @return 不可见的空白View数目
     */
    @SuppressWarnings("unused")
    public int getFooterEmptyViewsCount() {
        return mAdapter == null ? 0 : mAdapter.getFooterViewsCount();
    }

    /**
     * 移除网格底部添加的View
     *
     * @param v 待移除的View
     * @return 是否成功移除
     */
    @SuppressWarnings("unused")
    public boolean removeFooterView(View v) {
        boolean finished = mFooterViews.remove(v);
        if (finished && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        return finished;
    }

    /**
     * 获得实际子项的第一项position，不包含HeaderView及其占用的空白View及HeaderItem
     *
     * @return 第一项position
     */
    @SuppressWarnings("unused")
    public int getFirstWrappedAdapterItemPosition() {
        if (mAdapter != null)
            return mAdapter.getFirstWrappedAdapterItemPosition();
        else
            return AdapterView.INVALID_POSITION;
    }

    /**
     * 获得实际子项的最后一项position，不包含HeaderView及其占用的空白View及HeaderItem
     *
     * @return 最后一项position
     */
    @SuppressWarnings("unused")
    public int getLastWrappedAdapterItemPosition() {
        if (mAdapter != null)
            return mAdapter.getFirstWrappedAdapterItemPosition()
                    + mAdapter.getItemCount() - 1;
        else
            return AdapterView.INVALID_POSITION;
    }

    /**
     * 获取 position 对应的类型
     *
     * @param position 子项位置
     * @return 子项类型，不在范围之内或Adapter为空返回 null
     */
    @SuppressWarnings("unused")
    public HeaderFooterViewListAdapter.PositionType getPositionType(int position) {
        if (mAdapter != null && position >= 0 && position < getCount()) {
            return mAdapter.getPositionType(position);
        } else {
            return null;
        }
    }

    /**
     * A class that represents a fixed view in a grid, for example a header at the top
     * or a footer at the bottom.
     */
    public class FixedViewInfo {
        /**
         * The view to add to the grid
         */
        public View view;
        /**
         * The data backing the view. This is returned from {@link ListAdapter#getItem(int)}.
         */
        public Object data;
        /**
         * <code>true</code> if the fixed view should be selectable in the grid
         */
        public boolean isSelectable;
    }

    private class HeaderFooterListener implements OnItemClickListener, OnItemLongClickListener,
            OnItemSelectedListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (clickListener != null)
                clickListener.onItemClick(parent, view,
                        position - (mAdapter == null ? 0 : mAdapter.getHeaderViewsCount()), id);
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            return longClickListener != null &&
                    longClickListener.onItemLongClick(parent, view,
                            position - (mAdapter == null ? 0 : mAdapter.getHeaderViewsCount()), id);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (selectedListener != null)
                selectedListener.onItemSelected(parent, view,
                        position - (mAdapter == null ? 0 : mAdapter.getHeaderViewsCount()), id);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            if (selectedListener != null)
                selectedListener.onNothingSelected(parent);
        }
    }
}
