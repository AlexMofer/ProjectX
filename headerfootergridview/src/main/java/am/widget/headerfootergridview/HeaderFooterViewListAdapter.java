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
import android.widget.ListView;
import android.widget.WrapperListAdapter;

import java.util.ArrayList;

/**
 * ListAdapter used when a ListView has header views. This ListAdapter wraps
 * another one and also keeps track of the header views and their associated
 * data objects.
 * <p>
 * This is intended as a base class; you will probably not need to use this
 * class directly in your own code.
 */
public class HeaderFooterViewListAdapter implements WrapperListAdapter,
		Filterable {

	private ListAdapter mAdapter;
	private final ArrayList<ListView.FixedViewInfo> mHeaderItemInfos;
	private final ArrayList<ListView.FixedViewInfo> mFooterItemInfos;
	static final ArrayList<ListView.FixedViewInfo> EMPTY_INFO_LIST = new ArrayList<ListView.FixedViewInfo>();
	static final ArrayList<View> EMPTY_VIEW_LIST = new ArrayList<View>();
	private final DataSetObservable mDataSetObservable = new DataSetObservable();
	boolean mAreAllFixedViewsSelectable;
	private final boolean mIsFilterable;
	private final ArrayList<View> mHeaderViews;
	private final ArrayList<View> mFooterViews;
	private int mNumColumns;
	private int unusedPositionCount = 0;

	public enum PositionType {
		HEADEREMPTY, HEADERVIEW, HEADERITEM, NORMAL, FOOTERITEM, FOOTERVIEW, FOOTEREMPTY;
	}

	public HeaderFooterViewListAdapter(
			ArrayList<ListView.FixedViewInfo> headerItemInfos,
			ArrayList<ListView.FixedViewInfo> footerItemInfos,
			ListAdapter adapter, ArrayList<View> headerViews,
			ArrayList<View> footerViews, int numColumns) {
		mAdapter = adapter;
		mIsFilterable = adapter instanceof Filterable;
		if (headerItemInfos == null) {
			mHeaderItemInfos = EMPTY_INFO_LIST;
		} else {
			mHeaderItemInfos = headerItemInfos;
		}
		if (footerItemInfos == null) {
			mFooterItemInfos = EMPTY_INFO_LIST;
		} else {
			mFooterItemInfos = footerItemInfos;
		}
		mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderItemInfos)
				&& areAllListInfosSelectable(mFooterItemInfos);
		if (numColumns > 0)
			mNumColumns = numColumns;
		else
			mNumColumns = 1;
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
	}

	public int getHeaderItemCount() {
		return mHeaderItemInfos.size();
	}

	public int getFooterItemCount() {
		return mFooterItemInfos.size();
	}

	@Override
	public boolean isEmpty() {
		return mAdapter == null || mAdapter.isEmpty();
	}

	private boolean areAllListInfosSelectable(
			ArrayList<ListView.FixedViewInfo> infos) {
		if (infos != null) {
			for (ListView.FixedViewInfo info : infos) {
				if (!info.isSelectable) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean removeHeaderItem(View v) {
		for (int i = 0; i < mHeaderItemInfos.size(); i++) {
			ListView.FixedViewInfo info = mHeaderItemInfos.get(i);
			if (info.view == v) {
				mHeaderItemInfos.remove(i);

				mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderItemInfos)
						&& areAllListInfosSelectable(mFooterItemInfos);
				notifyDataSetChanged();
				return true;
			}
		}

		return false;
	}

	public boolean removeFooterItem(View v) {
		for (int i = 0; i < mFooterItemInfos.size(); i++) {
			ListView.FixedViewInfo info = mFooterItemInfos.get(i);
			if (info.view == v) {
				mFooterItemInfos.remove(i);

				mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderItemInfos)
						&& areAllListInfosSelectable(mFooterItemInfos);
				notifyDataSetChanged();
				return true;
			}
		}

		return false;
	}

	@Override
	public int getCount() {
		return getHeaderViewCount() + getHeaderItemCount() + getItemCount()
				+ getFooterItemCount() + getFooterViewCount();
	}

	@Override
	public boolean areAllItemsEnabled() {
		if (mAdapter != null) {
			return mAreAllFixedViewsSelectable && mAdapter.areAllItemsEnabled();
		} else {
			return true;
		}
	}

	@Override
	public boolean isEnabled(int position) {
		if (position < getHeaderViewCount()) {
			// HeaderView
			return true;
		} else if (position < getHeaderViewCount() + getHeaderItemCount()
				+ getItemCount() + getFooterItemCount()) {
			position = position - getHeaderViewCount();
			// HeaderItem (negative positions will throw an
			// IndexOutOfBoundsException)
			int numHeaders = getHeaderItemCount();
			if (position < numHeaders) {
				return mHeaderItemInfos.get(position).isSelectable;
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
			return mFooterItemInfos.get(adjPosition - adapterCount).isSelectable;
		} else {
			if (position < getHeaderViewCount() + getHeaderItemCount()
					+ getItemCount() + getFooterItemCount()
					+ unusedPositionCount)
				return false;
			// FooterView
			return true;
		}
	}

	@Override
	public Object getItem(int position) {
		if (position < getHeaderViewCount()) {
			// HeaderView
			return null;
		} else if (position < getHeaderViewCount() + getHeaderItemCount()
				+ getItemCount() + getFooterItemCount()) {
			position = position - getHeaderViewCount();
			// HeaderItem (negative positions will throw an
			// IndexOutOfBoundsException)
			int numHeaders = getHeaderItemCount();
			if (position < numHeaders) {
				return mHeaderItemInfos.get(position).data;
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
			return mFooterItemInfos.get(adjPosition - adapterCount).data;
		} else {
			// FooterView
			return null;
		}
	}

	@Override
	public long getItemId(int position) {

		int numHeaders = getHeaderItemCount() + getHeaderViewCount();
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
		if (mAdapter != null) {
			return mAdapter.hasStableIds();
		}
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position < getHeaderViewCount()) {
			// HeaderView
			View item = null;
			View header = getHeader(position);

			if (position % mNumColumns == 0)
				item = header;
			else
				item = getHideView(header);
			return item;

		} else if (position < getHeaderViewCount() + getHeaderItemCount()
				+ getItemCount() + getFooterItemCount()) {
			position = position - getHeaderViewCount();
			// HeaderItem (negative positions will throw an
			// IndexOutOfBoundsException)
			int numHeaders = getHeaderItemCount();
			if (position < numHeaders) {
				return mHeaderItemInfos.get(position).view;
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
			return mFooterItemInfos.get(adjPosition - adapterCount).view;
		} else {
			if (position < getHeaderViewCount() + getHeaderItemCount()
					+ getItemCount() + getFooterItemCount()
					+ unusedPositionCount) {
				View simple = null;
				if (mFooterItemInfos.size() > 0)
					simple = mFooterItemInfos.get(mFooterItemInfos.size() - 1).view;
				else if (mAdapter != null && mAdapter.getCount() > 0)
					simple = mAdapter.getView(mAdapter.getCount() - 1, null,
							null);
				else if (mHeaderItemInfos.size() > 0)
					simple = mHeaderItemInfos.get(mHeaderItemInfos.size() - 1).view;
				else if (mHeaderViews.size() > 0)
					simple = mHeaderViews.get(mHeaderViews.size() - 1);
				return getHideView(simple);
			}
			// FooterView
			View item = null;
			View footer = getFooter(position);
			if (position % mNumColumns == 0)
				item = footer;
			else
				item = getHideView(footer);
			return item;
		}
	}

	@Override
	public int getItemViewType(int position) {
		int numHeaders = getHeaderItemCount() + getHeaderViewCount();
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
	 * 获得实际子项数目
	 * 
	 * @return
	 */
	public int getItemCount() {
		if (mAdapter != null) {
			return mAdapter.getCount();
		} else {
			return 0;
		}
	}

	/**
	 * 获得子项的第一项position（包含HeaderItem与FooterItem，不包含HeaderView及其占用的空白View）
	 * <p>
	 * 原Adapter无Item并且也无HeaderItem与FooterItem时，返回 AdapterView.INVALID_POSITION
	 * 
	 * @return
	 */
	public int getFristItemPosition() {
		// adapter有值或者有HeaderItem
		if ((mAdapter != null && mAdapter.getCount() > 0)
				|| getHeaderItemCount() > 0 || getFooterItemCount() > 0)
			return getHeaderViewCount();
		else
			return AdapterView.INVALID_POSITION;
	}

	/**
	 * 获得子项的最后项position（包含HeaderItem与FooterItem，不包含FooterView及其占用的空白View）
	 * <p>
	 * 原Adapter无Item并且也无HeaderItem与FooterItem时，返回 AdapterView.INVALID_POSITION
	 * 
	 * @return
	 */
	public int getLastItemPosition() {
		// adapter有值或者有HeaderItem
		if ((mAdapter != null && mAdapter.getCount() > 0)
				|| getHeaderItemCount() > 0 || getFooterItemCount() > 0)
			return getHeaderViewCount() + getHeaderItemCount() + getItemCount()
					+ getFooterItemCount() - 1;
		else
			return AdapterView.INVALID_POSITION;
	}

	/**
	 * 获取末个HeaderItem位置
	 * <p>
	 * 无HeaderItem则返回 AdapterView.INVALID_POSITION
	 * 
	 * @return
	 */
	public int getLastHeaderItemPosition() {
		// Header
		if (mHeaderItemInfos.size() > 0) {
			return getHeaderViewCount() + getHeaderItemCount() - 1;
		} else {
			return AdapterView.INVALID_POSITION;
		}
	}

	/**
	 * 获得实际子项的第一项position
	 * <p>
	 * 原Adapter无Item时，返回 AdapterView.INVALID_POSITION
	 * 
	 * @return
	 */
	public int getFristWrappedAdapterItemPosition() {
		// adapter有值
		if (mAdapter != null && mAdapter.getCount() > 0) {
			return getHeaderViewCount() + getHeaderItemCount();
		} else {
			return AdapterView.INVALID_POSITION;
		}
	}

	/**
	 * 获取首个FooterItem位置
	 * <p>
	 * 无FooterItem则返回 AdapterView.INVALID_POSITION
	 * 
	 * @return
	 */
	public int getFristFooterItemPosition() {
		// Footer
		if (mFooterItemInfos.size() > 0) {
			return getHeaderViewCount() + getHeaderItemCount() + getItemCount();
		} else {
			return AdapterView.INVALID_POSITION;
		}
	}

	/**
	 * 由Item position获取头Item的集合位置
	 * <p>
	 * 返回 AdapterView.INVALID_POSITION 表明其对应的不是头Item
	 * 
	 * @param position
	 * @return
	 */
	public int positionToHeaderItemIndex(int position) {
		final int lastHeaderItem = getFristFooterItemPosition();
		if (getHeaderItemCount() > 0 && lastHeaderItem > position
				&& position <= getHeaderViewCount()) {
			return position - getHeaderViewCount();
		} else {
			return AdapterView.INVALID_POSITION;
		}
	}

	/**
	 * 由Item position获取尾Item的集合位置
	 * <p>
	 * 返回 AdapterView.INVALID_POSITION 表明其对应的不是尾Item
	 * 
	 * @param position
	 * @return
	 */
	public int positionToFooterItemIndex(int position) {
		final int fristFooterItem = getFristFooterItemPosition();
		if (getFooterItemCount() > 0 && fristFooterItem >= position
				&& position < fristFooterItem + getFooterItemCount()) {
			return position - fristFooterItem;
		} else {
			return AdapterView.INVALID_POSITION;
		}
	}

	/**
	 * 由Item position获取HeaderView的集合位置（HeaderView占用的空白位置也算作HeaderView）
	 * <p>
	 * 返回 AdapterView.INVALID_POSITION 表明其对应的不是HeaderView
	 * 
	 * @param position
	 * @return
	 */
	public int positionToHeaderViewIndex(int position) {

		if (getHeaderViewCount() > 0 && position < getHeaderViewCount()) {
			return position / mNumColumns;
		} else {
			return AdapterView.INVALID_POSITION;
		}
	}

	/**
	 * 由Item position获取FooterView的集合位置（FooterView占用的空白位置也算作FooterView）
	 * <p>
	 * 返回 AdapterView.INVALID_POSITION 表明其对应的不是FooterView
	 * 
	 * @param position
	 * @return
	 */
	public int positionToFooterViewIndex(int position) {
		int startPosition = getHeaderViewCount() + getHeaderItemCount()
				+ getItemCount() + getFooterItemCount() + unusedPositionCount;
		if (getFooterViewCount() > 0 && position >= startPosition) {
			return (position - startPosition) / mNumColumns;
		} else {
			return AdapterView.INVALID_POSITION;
		}
	}

	/**
	 * 包含HeaderView占用的位置，实际为空白View充填
	 * 
	 * @return
	 */
	public int getHeaderViewCount() {
		return mHeaderViews.size() * mNumColumns;
	}

	/**
	 * 包含FooterView占用的位置以及最后一行不足一行的几个剩余位置，实际为空白View充填
	 * 
	 * @return
	 */
	public int getFooterViewCount() {
		int startPosition = getHeaderViewCount() + getHeaderItemCount()
				+ getItemCount() + getFooterItemCount();
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
	 * @return
	 */
	public int getUnusedPositionCount() {
		return unusedPositionCount;
	}

	/**
	 * 获取 position 对应的类型
	 * 
	 * @param position
	 * @return
	 */
	public PositionType getPositionType(int position) {
		if (position < getHeaderViewCount() + getHeaderItemCount()) {
			if (position % mNumColumns == 0)
				return PositionType.HEADERVIEW;
			else
				return PositionType.HEADEREMPTY;
		} else if (position >= getHeaderViewCount() + getHeaderItemCount()
				+ getItemCount() + getFooterItemCount()) {
			if (position % mNumColumns == 0)
				return PositionType.FOOTERVIEW;
			else
				return PositionType.FOOTEREMPTY;
		} else {
			if (position < getHeaderViewCount() + getHeaderItemCount())
				return PositionType.HEADERITEM;
			else if (position >= getHeaderViewCount() + getHeaderItemCount()
					+ getItemCount())
				return PositionType.FOOTERITEM;
			else
				return PositionType.NORMAL;
		}
	}

	public int getNumColumns() {
		return mNumColumns;
	}

	/**
	 * 列数大于0才允许设置
	 * 
	 * @param numColumns
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
		position = position - getHeaderViewCount() - getHeaderItemCount()
				- getItemCount() - getFooterItemCount() - unusedPositionCount;
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
		LinearLayout llyt = new LinearLayout(simple.getContext());
		View view = new View(simple.getContext());
		llyt.addView(view);
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		lp.height = height;
		view.setLayoutParams(lp);
		return llyt;
	}

	public void notifyDataSetChanged() {
		mDataSetObservable.notifyChanged();
	}

	public void notifyDataSetInvalidated() {
		mDataSetObservable.notifyInvalidated();
	}
}
