package com.am.widget.headerfootergridview;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ListView.FixedViewInfo;

import com.am.widget.headerfootergridview.HeaderFooterViewListAdapter.PositionType;
import com.am.widget.headerfootergridview.support.GridViewCompat;


/**
 * 
 * 头尾GridView 完美支持 AUTO_FIT 形式 可直接继承GridView
 * 
 * @author AlexMofer
 * 
 */
public class HeaderFooterGridView extends GridView {

	private final ArrayList<FixedViewInfo> mHeaderItemInfos = new ArrayList<FixedViewInfo>();
	private final ArrayList<FixedViewInfo> mFooterItemInfos = new ArrayList<FixedViewInfo>();
	private final ArrayList<View> mHeaderViews = new ArrayList<View>();
	private final ArrayList<View> mFooterViews = new ArrayList<View>();
	private HeaderFooterViewListAdapter mAdapter;
	private int mNumColumns;
	private int mHorizontalSpacing;
	private int mRequestedHorizontalSpacing;
	private int mVerticalSpacing;
	private int mColumnWidth;
	private int mRequestedColumnWidth;
	private int mRequestedNumColumns;
	private final AbsListView.LayoutParams abLayoutParams = new AbsListView.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT, 0);

	public HeaderFooterGridView(Context context) {
		super(context);
	}

	public HeaderFooterGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HeaderFooterGridView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

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
		switch (stretchMode) {
		case NO_STRETCH:
			// 列间距不变，列宽不变
			mHorizontalSpacing = mRequestedHorizontalSpacing;
			mColumnWidth = mRequestedColumnWidth;
			break;
		default:
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
			break;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 完成计算，为达到高精确度，进行一下高版本数据确认：
		if (Build.VERSION.SDK_INT >= 11) {
			mNumColumns = GridViewCompat.getNumColumns(this);
		}
		if (Build.VERSION.SDK_INT >= 16) {
			mHorizontalSpacing = GridViewCompat.getHorizontalSpacing(this);
			mColumnWidth = GridViewCompat.getColumnWidth(this);
		}
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
			int startHeaderIndex, endHeaderIndex, startFooterIndex = 0, endFooterIndex = 0;
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
					.getFristItemPosition()
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
					.getFristItemPosition()) {
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
			} else {
				// 头尾均不显示
			}
		}
		super.dispatchDraw(canvas);
	}

	/**
	 * 移动View
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @param listViews
	 */
	private void translateView(int startIndex, int endIndex,
			ArrayList<View> listViews) {
		for (int i = startIndex; i <= endIndex && i < listViews.size()
				&& i >= 0; i++) {
			View header = listViews.get(i);
			header.layout(getPaddingLeft(), header.getTop(), getWidth()
					- getPaddingRight(), header.getTop() + header.getHeight());
		}
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		mAdapter = new HeaderFooterViewListAdapter(mHeaderItemInfos,
				mFooterItemInfos, adapter, mHeaderViews, mFooterViews,
				mNumColumns);
		super.setAdapter(mAdapter);
	}

	@Override
	public ListAdapter getAdapter() {
		if (mAdapter != null)
			return mAdapter.getWrappedAdapter();
		else
			return null;
	}

	@Override
	public void setVerticalSpacing(int verticalSpacing) {
		mVerticalSpacing = verticalSpacing;
		super.setVerticalSpacing(verticalSpacing);
	}

	public int getVerticalSpacingCompat() {
		if (Build.VERSION.SDK_INT >= 16)
			return GridViewCompat.getVerticalSpacing(this);
		else
			return mVerticalSpacing;

	}

	@Override
	public void setHorizontalSpacing(int horizontalSpacing) {
		mRequestedHorizontalSpacing = horizontalSpacing;
		super.setHorizontalSpacing(horizontalSpacing);
	}

	public int getHorizontalSpacingCompat() {
		if (Build.VERSION.SDK_INT >= 16)
			return GridViewCompat.getHorizontalSpacing(this);
		else
			return mHorizontalSpacing;
	}

	public int getRequestedHorizontalSpacingCompat() {
		if (Build.VERSION.SDK_INT >= 16)
			return GridViewCompat.getRequestedHorizontalSpacing(this);
		else
			return mRequestedHorizontalSpacing;
	}

	@Override
	public void setColumnWidth(int columnWidth) {
		mRequestedColumnWidth = columnWidth;
		super.setColumnWidth(columnWidth);
	}

	public int getColumnWidthCompat() {
		if (Build.VERSION.SDK_INT >= 16)
			return GridViewCompat.getColumnWidth(this);
		else
			return mColumnWidth;
	}

	public int getRequestedColumnWidthCompat() {
		if (Build.VERSION.SDK_INT >= 16)
			return GridViewCompat.getRequestedColumnWidth(this);
		else
			return mRequestedColumnWidth;
	}

	@Override
	public void setNumColumns(int numColumns) {
		mRequestedNumColumns = numColumns;
		super.setNumColumns(numColumns);
	}

	public int getNumColumnsCompat() {
		if (Build.VERSION.SDK_INT >= 16)
			return GridViewCompat.getNumColumns(this);
		else
			return mNumColumns;
	}

	/**
	 * 添加头子项
	 * 
	 * @param v
	 * @param data
	 * @param isSelectable
	 */
	public void addHeaderItem(View v, Object data, boolean isSelectable) {
		final FixedViewInfo info = new ListView(getContext()).new FixedViewInfo();
		info.view = v;
		info.data = data;
		info.isSelectable = isSelectable;
		mHeaderItemInfos.add(0, info);
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
	}

	/**
	 * 添加尾子项
	 * 
	 * @param v
	 * @param data
	 * @param isSelectable
	 */
	public void addFooterItem(View v, Object data, boolean isSelectable) {
		final FixedViewInfo info = new ListView(getContext()).new FixedViewInfo();
		info.view = v;
		info.data = data;
		info.isSelectable = isSelectable;
		mFooterItemInfos.add(info);
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
	}

	/**
	 * 添加头子项
	 * 
	 * @param v
	 */
	public void addHeaderItem(View v) {
		addHeaderItem(v, null, false);
	}

	/**
	 * 移除头子项
	 * 
	 * @param v
	 * @return
	 */
	public boolean removeHeaderItem(View v) {
		if (mAdapter != null)
			return mAdapter.removeHeaderItem(v);
		return false;
	}

	/**
	 * 添加尾子项
	 * 
	 * @param v
	 */
	public void addFooterItem(View v) {
		addFooterItem(v, null, false);
	}

	/**
	 * 移除尾子项
	 * 
	 * @param v
	 * @return
	 */
	public boolean removeFooterItem(View v) {
		if (mAdapter != null)
			return mAdapter.removeFooterItem(v);
		return false;
	}

	/**
	 * 添加头View
	 * 
	 * @param header
	 */
	public void addHeaderView(View header) {
		mHeaderViews.add(0, header);
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
	}

	/**
	 * 移除头View
	 * 
	 * @param header
	 * @return
	 */
	public boolean removeHeaderView(View header) {
		boolean finished = mHeaderViews.remove(header);
		if (finished && mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		return finished;
	}

	/**
	 * 添加尾View
	 * 
	 * @param footer
	 */
	public void addFooterView(View footer) {
		mFooterViews.add(footer);
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
	}

	/**
	 * 移除尾View
	 * 
	 * @param footer
	 * @return
	 */
	public boolean removeFooterView(View footer) {
		boolean finished = mFooterViews.remove(footer);
		if (finished && mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		return finished;
	}

	/**
	 * 获得实际子项的第一项position（不包含HeaderView及其占用的空白View及HeaderItem）
	 * 
	 * @return
	 */
	public int getFristWrappedAdapterItemPosition() {
		if (mAdapter != null)
			return mAdapter.getFristWrappedAdapterItemPosition();
		else
			return AdapterView.INVALID_POSITION;
	}

	/**
	 * 获得实际子项的最后一项position（不包含HeaderView及其占用的空白View及HeaderItem）
	 * 
	 * @return
	 */
	public int getLastWrappedAdapterItemPosition() {
		if (mAdapter != null)
			return mAdapter.getFristWrappedAdapterItemPosition()
					+ mAdapter.getItemCount() - 1;
		else
			return AdapterView.INVALID_POSITION;
	}

	/**
	 * 获取 position 对应的类型
	 * <p>
	 * 不在范围之内或Adapter为空返回null
	 * 
	 * @param position
	 * @return
	 */
	public PositionType getPositionType(int position) {
		if (mAdapter != null && position >= 0 && position < getCount()) {
			return mAdapter.getPositionType(position);
		} else {
			return null;
		}
	}
}
