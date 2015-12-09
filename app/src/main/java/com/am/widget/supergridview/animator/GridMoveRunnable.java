package com.am.widget.supergridview.animator;

import android.view.View;
import android.view.animation.Interpolator;

import com.am.widget.animators.BaseAnimator;
import com.am.widget.headerfootergridview.HeaderFooterGridView;
import com.am.widget.headerfootergridview.HeaderFooterViewListAdapter;
import com.am.widget.supergridview.DragView;
import com.am.widget.support.ViewHelper;

/**
 * 目前不支持添加了FooterView，会出现动画错乱
 * <p>TODO 解决FooterView支持
 * @author xiangzhicheng
 * 
 */
public class GridMoveRunnable extends BaseAnimator {

	private long mNormalDuration;
	protected final HeaderFooterGridView mGridView;
	protected final DragView mDragView;
	private int startPosition;
	private int itemDelay = 30;

	private int downHeight = 0;// 上浮下沉高度，子项往下沉落 与 左下角上浮到右上角
	private int upHeight = 0;// 上浮下沉高度，子项往下沉落 与 左下角上浮到右上角
	private int rightWidth = 0;// 右偏移值，从右边恢复到原位置，固定值，计算一次（子项宽度+列间距）
	private int leftWidth = 0;// 左偏移值，左下角上浮到右上角，固定值，计算一次（父View宽度-左右Padding-子项宽度）

	private boolean moveDown = false;
	private int maxItem = 0;
	private int firstVisiblePosition = 0;
	private boolean reCheckPosition = false;
	private int firstChildTop;

	public GridMoveRunnable(HeaderFooterGridView gridView, DragView dragView,
			long duration, Interpolator interpolator) {
		super(gridView, duration, interpolator);
		if (mNormalDuration == 0)
			mNormalDuration = duration;
		mGridView = gridView;
		mDragView = dragView;
	}

	/**
	 * 初始化
	 */
	public void setup(int startPosition) {
		if (startPosition == mGridView.getCount() - 1) {
			// 拖动的是最后一项，无需动画
			mDragView.notifyGridDeleteFinish();
			return;
		}
		this.startPosition = startPosition;
		setupWithoutFooterView();
	}

	private void setupWithoutFooterView() {
		moveDown = false;// 默认情况不存在下沉
		firstVisiblePosition = mGridView.getFirstVisiblePosition();// 数据删除后需要重新检查
		final int lastVisiblePosition = mGridView.getLastVisiblePosition();
		if (mGridView.getNumColumnsCompat() > 1) {
			rightWidth = mGridView.getColumnWidthCompat()
					+ mGridView.getHorizontalSpacingCompat();
			leftWidth = (mGridView.getColumnWidthCompat() + mGridView
					.getHorizontalSpacingCompat())
					* (mGridView.getNumColumnsCompat() - 1);
		} else {
			rightWidth = 0;
			leftWidth = 0;
		}
		View vs = mGridView.getChildAt(startPosition - firstVisiblePosition);
		if (vs != null)
			upHeight = vs.getHeight() + mGridView.getVerticalSpacingCompat();
		else
			upHeight = 0;
		// 检查是否需要下沉
		if (firstVisiblePosition == 0
				&& mGridView.getChildAt(0).getTop() >= mGridView
						.getPaddingTop()) {
			// 第一项已完全显示，无需下沉
		} else if (lastVisiblePosition != mGridView.getCount() - 1
				|| lastVisiblePosition % mGridView.getNumColumnsCompat() != 0) {
			// 最后一项没有显示，或最后一项显示但不在最后一行第一个，无需下沉
		} else {
			// 上浮，下沉 计算 （需二次验证）
			moveDown = true;
			reCheckPosition = true;
			View lastChild = mGridView
					.getChildAt(mGridView.getChildCount() - 1);
			downHeight = lastChild.getMeasuredHeight()
					+ mGridView.getVerticalSpacingCompat()
					- (lastChild.getBottom() - (mGridView
							.getMeasuredHeight() - mGridView
							.getPaddingBottom()));
			View firstChild = mGridView.getChildAt(0);
			firstChildTop = firstChild.getTop();
			if (firstVisiblePosition == 0) {
				// 首项出现，无需二次验证
				reCheckPosition = false;
				int maxDownHeight = firstChild.getMeasuredHeight() - firstChild.getBottom() + mGridView.getPaddingTop();
				downHeight = downHeight > maxDownHeight ? maxDownHeight : downHeight;
			}
		}
	}

	@Override
	public void start() {
		mTime = 0;
		if (firstVisiblePosition != mGridView.getFirstVisiblePosition()) {
			firstVisiblePosition = mGridView.getFirstVisiblePosition();
			if (reCheckPosition && firstVisiblePosition == 0) {
				// 首项出现，需重新计算上浮下沉
				View firstChild = mGridView.getChildAt(0);
				downHeight = firstChild.getMeasuredHeight() + firstChild.getTop() + mGridView.getVerticalSpacingCompat() - firstChildTop;
			}
		}
		getMaxItemAndTime();
		super.start();
	}

	@Override
	public void end() {
		super.end();
		mDragView.notifyGridDeleteFinish();
	}

	private void getMaxItemAndTime() {
		maxItem = mGridView.getLastVisiblePosition() - startPosition;
		if (moveDown) {
			final int dItem = startPosition - firstVisiblePosition;
			final int uItem = mGridView.getLastVisiblePosition()
					- startPosition;
			maxItem = dItem > uItem ? dItem : uItem;
		}
		mDuration = mNormalDuration + itemDelay * maxItem;
	}

	@Override
	protected void animator(float p) {
		moveItemsWithoutFooterView(mTime);
	}

	public void moveItemsWithoutFooterView(long time) {
		if (firstVisiblePosition != mGridView.getFirstVisiblePosition()) {
			firstVisiblePosition = mGridView.getFirstVisiblePosition();
		}
		for (int i = 0; i <= maxItem; i++) {
			if (moveDown) {
				if (startPosition - 1 - i >= mGridView
						.getFirstVisiblePosition()) {
					translationItem(startPosition - 1 - i, 0, -downHeight
							* getInterpolation(i, time));
				}
				if (startPosition + i <= mGridView.getLastVisiblePosition()) {
					if ((startPosition + i + 1)
							% mGridView.getNumColumnsCompat() == 0) {

						translationItem(
								startPosition + i,
								-leftWidth * getInterpolation(i, time),
								(upHeight - downHeight)
										* getInterpolation(i, time));

					} else {
						translationItem(startPosition + i, rightWidth
								* getInterpolation(i, time), -downHeight
								* getInterpolation(i, time));
					}
				}

			} else {
				if ((startPosition + i + 1) % mGridView.getNumColumnsCompat() == 0) {

					translationItem(startPosition + i, -leftWidth
							* getInterpolation(i, time), upHeight
							* getInterpolation(i, time));

				} else {
					translationItem(startPosition + i, rightWidth
							* getInterpolation(i, time), 0);
				}
			}
		}
	}

	private float getInterpolation(int i, float time) {
		float input = (float) (time - i * itemDelay) / (float) mNormalDuration;
		if (input > 1)
			input = 1;
		else if (input < 0)
			input = 0;
		return 1 - mInterpolator.getInterpolation(input);
	}

	private void translationItem(int position, float x, float y) {
		View child = mGridView.getChildAt(position - firstVisiblePosition);
		if (child != null) {
			HeaderFooterViewListAdapter.PositionType pt = mGridView
					.getPositionType(position);
			if (pt == HeaderFooterViewListAdapter.PositionType.NORMAL
					|| pt == HeaderFooterViewListAdapter.PositionType.HEADERITEM) {
				ViewHelper.setTranslationX(child, x);
				ViewHelper.setTranslationY(child, y);
			} else if (pt == HeaderFooterViewListAdapter.PositionType.FOOTERITEM) {
				ViewHelper.setTranslationX(child, x);
				ViewHelper.setTranslationY(child, y);
			} else if (pt == HeaderFooterViewListAdapter.PositionType.HEADERVIEW) {
				ViewHelper.setTranslationY(child, y);
			} else if (pt == HeaderFooterViewListAdapter.PositionType.FOOTERVIEW) {
				// TODO 动画效果，垂直上移
			}
		}
	}

	@Override
	public void setDuration(long duration) {
		mNormalDuration = duration;
	}

}
