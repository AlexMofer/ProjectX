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

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.util.ArrayList;

/**
 * ListAdapter used when a ListView has header views. This ListAdapter wraps
 * another one and also keeps track of the header views and their associated
 * data objects.
 * This is intended as a base class; you will probably not need to use this
 * class directly in your own code.
 */
@SuppressWarnings("all")
public class HeaderFooterViewListAdapter implements WrapperListAdapter,
        Filterable {

    private static final ArrayList<HeaderFooterGridView.FixedViewInfo> EMPTY_INFO_LIST =
            new ArrayList<>();
    private static final ArrayList<View> EMPTY_VIEW_LIST = new ArrayList<>();
    private final ArrayList<HeaderFooterGridView.FixedViewInfo> mHeaderItemInfo;
    private final ArrayList<HeaderFooterGridView.FixedViewInfo> mFooterItemInfo;
    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    private final boolean mIsFilterable;
    private final ArrayList<View> mHeaderViews;
    private final ArrayList<View> mFooterViews;
    private boolean mAreAllFixedViewsSelectable;
    private ListAdapter mAdapter;
    private int mNumColumns;
    private int unusedPositionCount = 0;

    public HeaderFooterViewListAdapter(
            ArrayList<HeaderFooterGridView.FixedViewInfo> headerItemInfo,
            ArrayList<HeaderFooterGridView.FixedViewInfo> footerItemInfo,
            ListAdapter adapter, ArrayList<View> headerViews,
            ArrayList<View> footerViews, int numColumns) {
        mAdapter = adapter;
        if (headerViews == null) {
            mHeaderViews = EMPTY_VIEW_LIST;
        } else {
            mHeaderViews = headerViews;
        }
        if (footerViews == null) {
            mFooterViews = EMPTY_VIEW_LIST;
        } else {
            mFooterViews = footerViews;
        }
        mIsFilterable = adapter instanceof Filterable;
        if (headerItemInfo == null) {
            mHeaderItemInfo = EMPTY_INFO_LIST;
        } else {
            mHeaderItemInfo = headerItemInfo;
        }
        if (footerItemInfo == null) {
            mFooterItemInfo = EMPTY_INFO_LIST;
        } else {
            mFooterItemInfo = footerItemInfo;
        }
        mAreAllFixedViewsSelectable = areAllListInfoSelectable(mHeaderItemInfo)
                && areAllListInfoSelectable(mFooterItemInfo);
        if (numColumns > 0)
            mNumColumns = numColumns;
        else
            mNumColumns = 1;

    }

    public int getHeaderItemsCount() {
        return mHeaderItemInfo.size();
    }

    public int getFooterItemsCount() {
        return mFooterItemInfo.size();
    }

    @Override
    public boolean isEmpty() {
        return mAdapter == null || mAdapter.isEmpty();
    }

    private boolean areAllListInfoSelectable(ArrayList<HeaderFooterGridView.FixedViewInfo> info) {
        if (mHeaderViews != null && mHeaderViews.size() > 0)
            return false;
        if (mFooterViews != null && mFooterViews.size() > 0)
            return false;
        if (info != null) {
            for (HeaderFooterGridView.FixedViewInfo i : info) {
                if (!i.isSelectable) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean removeHeaderItem(View v) {
        for (int i = 0; i < mHeaderItemInfo.size(); i++) {
            HeaderFooterGridView.FixedViewInfo info = mHeaderItemInfo.get(i);
            if (info.view == v) {
                mHeaderItemInfo.remove(i);

                mAreAllFixedViewsSelectable = areAllListInfoSelectable(mHeaderItemInfo)
                        && areAllListInfoSelectable(mFooterItemInfo);
                notifyDataSetChanged();
                return true;
            }
        }

        return false;
    }

    public boolean removeFooterItem(View v) {
        for (int i = 0; i < mFooterItemInfo.size(); i++) {
            HeaderFooterGridView.FixedViewInfo info = mFooterItemInfo.get(i);
            if (info.view == v) {
                mFooterItemInfo.remove(i);

                mAreAllFixedViewsSelectable = areAllListInfoSelectable(mHeaderItemInfo)
                        && areAllListInfoSelectable(mFooterItemInfo);
                notifyDataSetChanged();
                return true;
            }
        }

        return false;
    }

    @Override
    public int getCount() {
        return getHeaderViewsCount() + getHeaderItemsCount() + getItemCount()
                + getFooterItemsCount() + getFooterViewsCount();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mAdapter == null || mAreAllFixedViewsSelectable && mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        if (position < getHeaderViewsCount()) {
            // HeaderView
            return false;
        } else if (position < getHeaderViewsCount() + getHeaderItemsCount()
                + getItemCount() + getFooterItemsCount()) {
            position = position - getHeaderViewsCount();
            // HeaderItem (negative positions will throw an
            // IndexOutOfBoundsException)
            int numHeaders = getHeaderItemsCount();
            if (position < numHeaders) {
                return mHeaderItemInfo.get(position).isSelectable;
            }

            // Adapter
            final int adjPosition = position - numHeaders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.isEnabled(adjPosition);
                }
            }

            // FooterItem (off-limits positions will throw an
            // IndexOutOfBoundsException)
            return mFooterItemInfo.get(adjPosition - adapterCount).isSelectable;
        } else {
            if (position < getHeaderViewsCount() + getHeaderItemsCount()
                    + getItemCount() + getFooterItemsCount()
                    + unusedPositionCount)
                return false;
            // FooterView
            return false;
        }
    }

    @Override
    public Object getItem(int position) {
        if (position < getHeaderViewsCount()) {
            // HeaderView
            return null;
        } else if (position < getHeaderViewsCount() + getHeaderItemsCount()
                + getItemCount() + getFooterItemsCount()) {
            position = position - getHeaderViewsCount();
            // HeaderItem (negative positions will throw an
            // IndexOutOfBoundsException)
            int numHeaders = getHeaderItemsCount();
            if (position < numHeaders) {
                return mHeaderItemInfo.get(position).data;
            }

            // Adapter
            final int adjPosition = position - numHeaders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItem(adjPosition);
                }
            }

            // FooterItem (off-limits positions will throw an
            // IndexOutOfBoundsException)
            return mFooterItemInfo.get(adjPosition - adapterCount).data;
        } else {
            // FooterView
            return null;
        }
    }

    @Override
    public long getItemId(int position) {

        int numHeaders = getHeaderItemsCount() + getHeaderViewsCount();
        if (mAdapter != null && position >= numHeaders) {
            int adjPosition = position - numHeaders;
            int adapterCount = mAdapter.getCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemId(adjPosition);
            }
        }
        return -1;
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter != null && mAdapter.hasStableIds();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position < getHeaderViewsCount()) {
            // HeaderView
            return position % mNumColumns == 0 ? getHeader(position) :
                    getHideView(getHeader(position));

        } else if (position < getHeaderViewsCount() + getHeaderItemsCount()
                + getItemCount() + getFooterItemsCount()) {
            position = position - getHeaderViewsCount();
            // HeaderItem (negative positions will throw an
            // IndexOutOfBoundsException)
            int numHeaders = getHeaderItemsCount();
            if (position < numHeaders) {
                return mHeaderItemInfo.get(position).view;
            }
            // Adapter
            final int adjPosition = position - numHeaders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getView(adjPosition, convertView, parent);
                }
            }
            // FooterItem (off-limits positions will throw an
            // IndexOutOfBoundsException)
            return mFooterItemInfo.get(adjPosition - adapterCount).view;
        } else if (position < getHeaderViewsCount() + getHeaderItemsCount()
                + getItemCount() + getFooterItemsCount()
                + unusedPositionCount) {
            View simple = null;
            if (mFooterItemInfo.size() > 0)
                simple = mFooterItemInfo.get(mFooterItemInfo.size() - 1).view;
            else if (mAdapter != null && mAdapter.getCount() > 0)
                simple = mAdapter.getView(mAdapter.getCount() - 1, null,
                        parent);
            else if (mHeaderItemInfo.size() > 0)
                simple = mHeaderItemInfo.get(mHeaderItemInfo.size() - 1).view;
            else if (mHeaderViews.size() > 0)
                simple = mHeaderViews.get(mHeaderViews.size() - 1);
            return getHideView(simple);
        } else {
            // FooterView
            return position % mNumColumns == 0 ? getFooter(position) :
                    getHideView(getFooter(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        int numHeaders = getHeaderItemsCount() + getHeaderViewsCount();
        if (mAdapter != null && position >= numHeaders) {
            int adjPosition = position - numHeaders;
            int adapterCount = mAdapter.getCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemViewType(adjPosition);
            }
        }

        return AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
    }

    @Override
    public int getViewTypeCount() {
        if (mAdapter != null) {
            return mAdapter.getViewTypeCount();
        }
        return 1;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(observer);
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(observer);
        }
    }

    @Override
    public Filter getFilter() {
        if (mIsFilterable) {
            return ((Filterable) mAdapter).getFilter();
        }
        return null;
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

    /**
     * 获取实际子项数目
     *
     * @return 实际子项数目
     */
    public int getItemCount() {
        if (mAdapter != null) {
            return mAdapter.getCount();
        } else {
            return 0;
        }
    }

    /**
     * 获取子项的第一项position（包含HeaderItem与FooterItem，不包含HeaderView及其占用的空白View）
     * 原Adapter无Item并且也无HeaderItem与FooterItem时，返回 AdapterView.INVALID_POSITION
     *
     * @return 子项的第一项position
     */
    public int getFirstItemPosition() {
        // adapter有值或者有HeaderItem
        if ((mAdapter != null && mAdapter.getCount() > 0)
                || getHeaderItemsCount() > 0 || getFooterItemsCount() > 0)
            return getHeaderViewsCount();
        else
            return AdapterView.INVALID_POSITION;
    }

    /**
     * 获取子项的最后项position（包含HeaderItem与FooterItem，不包含FooterView及其占用的空白View）
     * 原Adapter无Item并且也无HeaderItem与FooterItem时，返回 AdapterView.INVALID_POSITION
     *
     * @return 获取子项的最后项position
     */
    public int getLastItemPosition() {
        // adapter有值或者有HeaderItem
        if ((mAdapter != null && mAdapter.getCount() > 0)
                || getHeaderItemsCount() > 0 || getFooterItemsCount() > 0)
            return getHeaderViewsCount() + getHeaderItemsCount() + getItemCount()
                    + getFooterItemsCount() - 1;
        else
            return AdapterView.INVALID_POSITION;
    }

    /**
     * 获取末个HeaderItem位置
     * 无HeaderItem则返回 AdapterView.INVALID_POSITION
     *
     * @return 末个HeaderItem位置
     */
    @SuppressWarnings("unused")
    public int getLastHeaderItemPosition() {
        // Header
        if (mHeaderItemInfo.size() > 0) {
            return getHeaderViewsCount() + getHeaderItemsCount() - 1;
        } else {
            return AdapterView.INVALID_POSITION;
        }
    }

    /**
     * 获取实际子项的第一项position
     * 原Adapter无Item时，返回 AdapterView.INVALID_POSITION
     *
     * @return 实际子项的第一项position
     */
    public int getFirstWrappedAdapterItemPosition() {
        // adapter有值
        if (mAdapter != null && mAdapter.getCount() > 0) {
            return getHeaderViewsCount() + getHeaderItemsCount();
        } else {
            return AdapterView.INVALID_POSITION;
        }
    }

    /**
     * 获取首个FooterItem位置
     * 无FooterItem则返回 AdapterView.INVALID_POSITION
     *
     * @return 首个FooterItem位置
     */
    public int getFirstFooterItemPosition() {
        // Footer
        if (mFooterItemInfo.size() > 0) {
            return getHeaderViewsCount() + getHeaderItemsCount() + getItemCount();
        } else {
            return AdapterView.INVALID_POSITION;
        }
    }

    /**
     * 获取头Item的集合位置
     * 返回 AdapterView.INVALID_POSITION 表明其对应的不是头Item
     *
     * @param position 位置
     * @return 头Item的集合位置
     */
    @SuppressWarnings("unused")
    public int positionToHeaderItemIndex(int position) {
        final int firstFooterItem = getFirstFooterItemPosition();
        if (getHeaderItemsCount() > 0 && firstFooterItem > position
                && position <= getHeaderViewsCount()) {
            return position - getHeaderViewsCount();
        } else {
            return AdapterView.INVALID_POSITION;
        }
    }

    /**
     * 获取尾Item的集合位置
     * 返回 AdapterView.INVALID_POSITION 表明其对应的不是尾Item
     *
     * @param position 位置
     * @return 尾Item的集合位置
     */
    @SuppressWarnings("unused")
    public int positionToFooterItemIndex(int position) {
        final int firstFooterItem = getFirstFooterItemPosition();
        if (getFooterItemsCount() > 0 && firstFooterItem >= position
                && position < firstFooterItem + getFooterItemsCount()) {
            return position - firstFooterItem;
        } else {
            return AdapterView.INVALID_POSITION;
        }
    }

    /**
     * 获取HeaderView的集合位置（HeaderView占用的空白位置也算作HeaderView）
     * 返回 AdapterView.INVALID_POSITION 表明其对应的不是HeaderView
     *
     * @param position 位置
     * @return HeaderView的集合位置
     */
    public int positionToHeaderViewIndex(int position) {

        if (getHeaderViewsCount() > 0 && position < getHeaderViewsCount()) {
            return position / mNumColumns;
        } else {
            return AdapterView.INVALID_POSITION;
        }
    }

    /**
     * 获取FooterView的集合位置（FooterView占用的空白位置也算作FooterView）
     * 返回 AdapterView.INVALID_POSITION 表明其对应的不是FooterView
     *
     * @param position 位置
     * @return FooterView的集合位置
     */
    public int positionToFooterViewIndex(int position) {
        int startPosition = getHeaderViewsCount() + getHeaderItemsCount()
                + getItemCount() + getFooterItemsCount() + unusedPositionCount;
        if (getFooterViewsCount() > 0 && position >= startPosition) {
            return (position - startPosition) / mNumColumns;
        } else {
            return AdapterView.INVALID_POSITION;
        }
    }

    /**
     * 获取包含HeaderView占用的位置，实际为空白View充填
     *
     * @return 包含HeaderView占用的位置
     */
    public int getHeaderViewsCount() {
        return mHeaderViews.size() * mNumColumns;
    }

    /**
     * 获取包含FooterView占用的位置以及最后一行不足一行的几个剩余位置，实际为空白View充填
     *
     * @return 包含FooterView占用的位置以及最后一行不足一行的几个剩余位置
     */
    public int getFooterViewsCount() {
        int startPosition = getHeaderViewsCount() + getHeaderItemsCount()
                + getItemCount() + getFooterItemsCount();
        int position = startPosition;
        unusedPositionCount = 0;
        for (int i = 0; i < mFooterViews.size(); position++) {
            if (position % mNumColumns == 0) {
                i++;
            }
            if (i == 0) {
                unusedPositionCount++;
            }
        }
        return position - startPosition;
    }

    /**
     * 获取无效Position
     *
     * @return 无效Position
     */
    @SuppressWarnings("unused")
    public int getUnusedPositionCount() {
        return unusedPositionCount;
    }

    /**
     * 获取 position 对应的类型
     *
     * @param position 位置
     * @return 对应的类型
     */
    public PositionType getPositionType(int position) {
        if (position < getHeaderViewsCount() + getHeaderItemsCount()) {
            if (position % mNumColumns == 0)
                return PositionType.HEADER_VIEW;
            else
                return PositionType.HEADER_EMPTY;
        } else if (position >= getHeaderViewsCount() + getHeaderItemsCount()
                + getItemCount() + getFooterItemsCount()) {
            if (position % mNumColumns == 0)
                return PositionType.FOOTER_VIEW;
            else
                return PositionType.FOOTER_EMPTY;
        } else {
            if (position < getHeaderViewsCount() + getHeaderItemsCount())
                return PositionType.HEADER_ITEM;
            else if (position >= getHeaderViewsCount() + getHeaderItemsCount()
                    + getItemCount())
                return PositionType.FOOTER_ITEM;
            else
                return PositionType.NORMAL;
        }
    }

    public int getNumColumns() {
        return mNumColumns;
    }

    /**
     * 设置列数
     * 列数大于0才允许设置
     *
     * @param numColumns 列数
     */
    public void setNumColumns(int numColumns) {
        if (mNumColumns != numColumns && numColumns > 0) {
            mNumColumns = numColumns;
            notifyDataSetChanged();
        }
    }

    private View getHeader(int position) {
        View header = null;
        for (int i = 0; i <= mHeaderViews.size(); i++) {
            if (position < (i + 1) * mNumColumns) {
                header = mHeaderViews.get(i);
                break;
            }
        }
        return header;
    }

    private View getFooter(int position) {
        View footer = null;
        position = position - getHeaderViewsCount() - getHeaderItemsCount()
                - getItemCount() - getFooterItemsCount() - unusedPositionCount;
        for (int i = 0; i <= mFooterViews.size(); i++) {
            if (position < (i + 1) * mNumColumns) {
                footer = mFooterViews.get(i);
                break;
            }
        }
        return footer;
    }

    private View getHideView(View simple) {
        int height = simple.getMeasuredHeight();
        if (height == 0) {
            ViewGroup.LayoutParams p = simple.getLayoutParams();
            if (p == null || !(p instanceof AbsListView.LayoutParams)) {
                p = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 0);
            }
            simple.setLayoutParams(p);
            simple.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            height = simple.getMeasuredHeight();
        }
        LinearLayout lyt = new LinearLayout(simple.getContext());
        View view = new View(simple.getContext());
        lyt.addView(view);
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.height = height;
        view.setLayoutParams(lp);
        return lyt;
    }

    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    @SuppressWarnings("unused")
    public void notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated();
    }

    public enum PositionType {
        HEADER_EMPTY, HEADER_VIEW, HEADER_ITEM, NORMAL, FOOTER_ITEM, FOOTER_VIEW, FOOTER_EMPTY
    }
}
