package am.project.x.widgets.supergridview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AdapterView;

import java.util.ArrayList;

import am.project.x.widgets.headerfootergridview.HeaderFooterGridView;
import am.project.x.widgets.headerfootergridview.HeaderFooterViewListAdapter;
import am.project.x.widgets.supergridview.animator.GridMoveRunnable;

public class SuperGridView extends HeaderFooterGridView {

	private boolean dragable = true;
	private GestureDetectorCompat mDragGestureDetector;
	private AdapterView.OnItemLongClickListener longListener;
	private float fristX;
	private float fristY;
	private boolean startDrag;
	private boolean dragging;
	private int mDragPosition = INVALID_POSITION;
	private DragView mDragView;// 拖动View
	private DeleteAnimator deleteAnimator;// 删除动画
	private OnDragListener dragListener;
	private boolean animatGrid;
	private GridMoveRunnable gridMoveRunnable;
	private ArrayList<Integer> listDisablePosition = new ArrayList<Integer>();

	public SuperGridView(Context context) {
		super(context);
		init(context);
	}

	public SuperGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SuperGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		mDragGestureDetector = new GestureDetectorCompat(context,
				new SimpleOnGestureListener() {
					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						// 移动DragView
						if (startDrag)
							mDragView.moveDrag(e2.getX(), e2.getY());

						return true;
					}

					@Override
					public boolean onFling(final MotionEvent e1,
							final MotionEvent e2, final float velocityX,
							final float velocityY) {
						// 飞行DragView
						if (startDrag)
							mDragView.flingDrag(e2.getX(), e2.getY(),
									velocityX, velocityY);
						return true;
					}
				});

		mDragView = new DragView(context);
		mDragView.setOnViewDragListener(new DragView.OnViewDragListener() {

			@Override
			public void onStart() {
				getChildAt(mDragPosition - getFirstVisiblePosition())
						.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onRelease() {
				View child = getChildAt(mDragPosition
						- getFirstVisiblePosition());
				if (child != null)
					child.setVisibility(View.VISIBLE);
				dragging = false;
				animatGrid = false;
			}

			@Override
			public void onMerge() {
				// TODO 合并功能
			}

			@Override
			public void onDelete() {
				if (dragListener != null) {
					if (deleteAnimator == null)
						deleteAnimator = new DeleteAnimator();
					deleteAnimator.setup();
					dragListener.onDelete(mDragPosition,
							getFristWrappedAdapterItemPosition(),
							deleteAnimator);
				}
			}
		});

		// 提供长按震动
		super.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (dragable) {
					mDragPosition = pointToPosition((int) fristX, (int) fristY);
					if (checkPositionOfPointDragable(mDragPosition)) {
						// 子项可拖动，长按键听
						createDragItem(fristX, fristY);
						startDrag = true;
						dragging = true;
					}
				}
				if (longListener != null)
					longListener.onItemLongClick(parent, view, position, id);
				return true;
			}
		});
		gridMoveRunnable = new GridMoveRunnable(this, mDragView,
				mDragView.getDuration(), mDragView.getInterpolator());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mDragView.setArea(getWidth(), getHeight(), getLeft(), getTop());
	}

	@Override
	public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
		longListener = listener;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (!dragable)
			return super.dispatchTouchEvent(ev); // 不可拖动
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startDrag = false;
			fristX = ev.getX();
			fristY = ev.getY();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (!mDragGestureDetector.onTouchEvent(ev) && startDrag) {
				mDragView.removeDrag(ev.getX(), ev.getY());
			}
			startDrag = false;
		default:
			break;
		}
		if (startDrag) {
			return mDragGestureDetector.onTouchEvent(ev);
		} else {
			return super.dispatchTouchEvent(ev);
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (animatGrid && !gridMoveRunnable.isRunning()) {
			animatGrid = false;
			mDragView.delete();
			gridMoveRunnable.start();
		}
		super.dispatchDraw(canvas);
	}
	
	@Override
	protected void handleDataChanged() {
		// 数据变化提交之前先把拖动的子项显示，放置在此处有效解决删除最后一项出现部分子项隐藏的情况
		View child = getChildAt(mDragPosition - getFirstVisiblePosition());
		if (child != null)
			child.setVisibility(View.VISIBLE);
		super.handleDataChanged();
	}

	/**
	 * 创建拖动View
	 *
	 */
	private void createDragItem(float x, float y) {
		mDragView.setDrag(
				getChildAt(mDragPosition - getFirstVisiblePosition()), true);
		mDragView.startDrag(x, y);
	}

	/**
	 * 检查Point所在的Position是否可拖动 
	 * <p>TODO 增加条件
	 *
	 * @return
	 */
	private boolean checkPositionOfPointDragable(int position) {
		if (position != AdapterView.INVALID_POSITION
				&& !listDisablePosition.contains(position)) {
			if (getPositionType(position) == HeaderFooterViewListAdapter.PositionType.NORMAL)
				return true;
		}
		return false;
	}

	/**
	 * 拖动监听
	 * 
	 * @author xiangzhicheng
	 * 
	 */
	public interface OnDragListener {
		public void onDelete(int position, int fristWrappedAdapterItemPosition,
							 DeleteAnimator deleteAnimator);

		public void onMerge(int position, int toPoaition);
	}

	/**
	 * 设置删除图片资源
	 * 
	 * @param drawable
	 */
	public void setDeleteDrawable(Drawable drawable) {
		mDragView.setDeleteDrawable(drawable);
	}

	/**
	 * 设置删除图片资源
	 * 
	 * @param resid
	 */
	public void setDeleteResource(int resid) {
		mDragView.setDeleteResource(resid);
	}

	public boolean isDragging() {
		return dragging;
	}

	/**
	 * 设置删除图片位置 0 默认（上） 1 右 2 下 3 左
	 * 
	 * @param location
	 */
	public void setDeleteDrawableLocation(int location) {
		mDragView.setDeleteDrawableLocation(location);
	}

	/**
	 * 设置子项可否拖动
	 * <p>
	 * 仅记录GridView中的position并不定位到具体Item，因此Adapter更新以后需自行修改那些Position是否可拖拽
	 * 
	 * @param position
	 *            位置
	 * @param enable
	 *            可否拖动
	 */
	public void setPositionDragable(int position, boolean enable) {
		if (enable) {
			for (boolean goon = true; goon;) {
				goon = listDisablePosition.remove(Integer.valueOf(position));
			}
		} else {
			if (!listDisablePosition.contains(position))
				listDisablePosition.add(position);
		}
	}

	/**
	 * 设置所有子项可否拖动
	 * <p>
	 *
	 * @param enable
	 *            可否拖动
	 */
	public void setAllPositionDragable(boolean enable) {
		if (enable) {
			listDisablePosition.clear();
		} else {
			for (int i = 0; i < getCount(); i++)
				setPositionDragable(i, false);
		}
	}

	public boolean isDragable() {
		return dragable;
	}

	/**
	 * 开关GridView拖动
	 * <p>
	 * 不影响设置的子项是否可拖动
	 * 
	 * @param dragable
	 */
	public void setDragable(boolean dragable) {
		this.dragable = dragable;
	}

	public OnDragListener getOnDragListener() {
		return dragListener;
	}

	public void setOnDragListener(OnDragListener mergeListener) {
		this.dragListener = mergeListener;
	}

	public void setDragController(DragController controller) {
		mDragView.setDragController(controller);
	}

	public DragController getDragController() {
		return mDragView.getDragController();
	}

	@SuppressWarnings("deprecation")
	public void setDragViewBackground(Drawable background) {
		mDragView.setBackgroundDrawable(background);
	}

	public void setDragViewBackgroundColor(int color) {
		mDragView.setBackgroundColor(color);
	}

	public void setDragViewBackgroundResource(int resid) {
		mDragView.setBackgroundResource(resid);
	}

	public float getStartScale() {
		return mDragView.getStartScale();
	}

	public void setStartScale(float startScale) {
		mDragView.setStartScale(startScale);
	}

	public float getAbsorbedScale() {
		return mDragView.getAbsorbedScale();
	}

	public void setAbsorbedScale(float scale) {
		mDragView.setAbsorbedScale(scale);
	}

	public void setDuration(long duration) {
		mDragView.setDuration(duration);

	}
	
	public void setGridDuration(long duration) {
		gridMoveRunnable.setDuration(duration);

	}

	public void setInterpolator(Interpolator interpolator) {
		mDragView.setInterpolator(interpolator);
	}
	
	public void setGridInterpolator(Interpolator interpolator) {
		gridMoveRunnable.setInterpolator(interpolator);
	}

	/**
	 * 删除动画拆分
	 * 
	 * @author xiangzhicheng
	 * 
	 */
	public class DeleteAnimator {

		private DeleteAnimator() {
		}

		private void setup() {
			gridMoveRunnable.setup(mDragPosition);
		}

		public void delete() {
			animatGrid = true;
		}

		public void cancel() {
			animatGrid = false;
			mDragView.cancelDelete();
		}

		public void deleteWithoutAnimation() {
			animatGrid = false;
			mDragView.notifyGridDeleteFinish();
			mDragView.delete();
		}
	}
}
