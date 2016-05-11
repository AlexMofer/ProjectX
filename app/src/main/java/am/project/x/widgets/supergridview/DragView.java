package am.project.x.widgets.supergridview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import am.project.x.widgets.supergridview.animator.DragDeleteRunnable;
import am.project.x.widgets.supergridview.animator.DragFlingRunnable;
import am.project.x.widgets.supergridview.animator.DragMoveRunnable;
import am.project.x.widgets.supergridview.animator.DragRemoveRunnable;
import am.project.x.widgets.supergridview.animator.DragStartRunnable;
import am.project.x.widgets.supergridview.support.Tools;


public class DragView extends ViewGroup {

	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mWindowLayoutParams;
	private int width = 0;
	private int height = 0;
	private ImageView mDragView;
	private Bitmap mDragBitmap;
	private int itemWidth = 0;
	private int itemHeight = 0;
	private int itemTop = 0;
	private int itemLeft = 0;
	private float startScale = 1.2f;
	private float absorbedScale = 0.8f;
	private OnViewDragListener dragListener;
	private long mDuration = 300;
	private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
	private DragController mDragController = new DragController();// 拖拽控制器
	private ImageView mDeleteView;
	private boolean mDeleteable;
	private int deleteHeight = 100;
	private int deleteWidth = 100;
	private int deleteMove = 20;
	private int deleteMargin = 20;
	private int mDeleteLocation = 0;
	private float deleteCenterX = 0;
	private float deleteCenterY = 0;
	private final RectF deleteRectF;
	private final DragStartRunnable startDragRunnable;
	private final DragMoveRunnable moveDragRunnable;
	private final DragRemoveRunnable removeDragRunnable;
	private final DragDeleteRunnable deleteDragRunnable;
	private final DragFlingRunnable flingDragRunnable;
	private boolean mDragDeleteFinished, mGridDeleteFinished;

	public DragView(Context context) {
		super(context);
		mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		mWindowLayoutParams = new WindowManager.LayoutParams();
		mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; // 图片之外的其他地方透明
		mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_FULLSCREEN;
		mDeleteView = new ImageView(context);
		mDeleteView.setImageDrawable(Tools.getDefaultDrawable(deleteHeight,
				deleteWidth));
		addView(mDeleteView);
		mDragView = new ImageView(context);
		addView(mDragView);
		deleteRectF = new RectF();
		startDragRunnable = new DragStartRunnable(this, mDragView, mDeleteView,
				mDuration, mInterpolator, startScale);
		moveDragRunnable = new DragMoveRunnable(this, mDragView, mDeleteView,
				startDragRunnable, mDuration, mInterpolator, startScale,
				absorbedScale);
		removeDragRunnable = new DragRemoveRunnable(this, mDragView,
				mDeleteView, mDuration, mInterpolator);
		deleteDragRunnable = new DragDeleteRunnable(this, mDragView,
				mDeleteView, startDragRunnable, moveDragRunnable, mDuration,
				mInterpolator);
		flingDragRunnable = new DragFlingRunnable(this, mDragView, mDeleteView,
				startDragRunnable, mDuration, mInterpolator, absorbedScale);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(width, height);
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDetachedFromWindow() {
		mDragBitmap.recycle();
		super.onDetachedFromWindow();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mDragView.layout(l + itemLeft, t + itemTop, l + itemLeft + itemWidth, t
				+ itemTop + itemHeight);
		int deletel = 0;
		int deletet = 0;
		int delete = deleteHeight > deleteWidth ? deleteHeight : deleteWidth;
		switch (mDeleteLocation) {
		default:
		case 0:
			deletel = (int) (l + width * 0.5f - deleteWidth * 0.5f);
			deletet = t - deleteHeight;
			deleteCenterX = getLeft() + width * 0.5f;
			deleteCenterY = getTop() + deleteMargin + deleteMove + delete
					* 0.5f;
			break;
		case 1:
			deletel = l + width;
			deletet = (int) (t + height * 0.5f - deleteHeight * 0.5f);
			deleteCenterX = getRight() - deleteMargin - deleteMove - delete
					* 0.5f;
			deleteCenterY = getTop() + height * 0.5f;
			break;
		case 2:
			deletel = (int) (l + width * 0.5f - deleteWidth * 0.5f);
			deletet = t + height;
			deleteCenterX = getLeft() + width * 0.5f;
			deleteCenterY = getBottom() - deleteMargin - deleteMove - delete
					* 0.5f;
			break;
		case 3:
			deletel = l - deleteWidth;
			deletet = (int) (t + height * 0.5f - deleteHeight * 0.5f);
			deleteCenterX = getLeft() + deleteMargin + deleteMove + delete
					* 0.5f;
			deleteCenterY = getTop() + height * 0.5f;
			break;
		}
		deleteRectF.set(deleteCenterX - deleteWidth, deleteCenterY
				- deleteHeight, deleteCenterX + deleteWidth, deleteCenterY
				+ deleteHeight);
		moveDragRunnable.setupDelete(deleteCenterX, deleteCenterY);
		flingDragRunnable.setupDelete(deleteWidth, deleteHeight, deleteCenterX,
				deleteCenterY);
		mDeleteView.layout(deletel, deletet, deletel + deleteWidth, deletet
				+ deleteHeight);
		deleteWidth = mDeleteView.getWidth();
		deleteHeight = mDeleteView.getHeight();
	}

	public void setArea(int width, int height, int left, int top) {
		this.width = width;
		this.height = height;
		mWindowLayoutParams.x = left;
		mWindowLayoutParams.y = top + Tools.getStatusHeight(getContext());
	}

	public void setDrag(View child, boolean deleteable) {
		mDeleteable = deleteable;
		mWindowManager.addView(this, mWindowLayoutParams);
		itemTop = child.getTop();
		itemLeft = child.getLeft();
		mDragBitmap = mDragController.setDragView(child);
		mDragView.setImageBitmap(mDragBitmap);
		itemWidth = mDragBitmap.getWidth();
		itemHeight = mDragBitmap.getHeight();
		final float centerX = itemLeft + itemWidth * 0.5f;
		final float centerY = itemTop + itemHeight * 0.5f;
		int deleteInherentX, deleteInherentY;
		DragFlingRunnable.CompareType compareType;
		switch (mDeleteLocation) {
		default:
		case 0:
			deleteInherentX = 0;
			deleteInherentY = deleteHeight + deleteMove + deleteMargin;
			compareType = DragFlingRunnable.CompareType.YMIN;
			break;
		case 1:
			deleteInherentX = -(deleteWidth + deleteMove + deleteMargin);
			deleteInherentY = 0;
			compareType = DragFlingRunnable.CompareType.XMAX;
			break;
		case 2:
			deleteInherentX = 0;
			deleteInherentY = -(deleteHeight + deleteMove + deleteMargin);
			compareType = DragFlingRunnable.CompareType.YMAX;
			break;
		case 3:
			deleteInherentX = deleteWidth + deleteMove + deleteMargin;
			deleteInherentY = 0;
			compareType = DragFlingRunnable.CompareType.XMIN;
			break;
		}
		startDragRunnable.setup(centerX, centerY, deleteInherentX,
				deleteInherentY, deleteMove, mWindowLayoutParams.x,
				mWindowLayoutParams.y, width, height, mDeleteable);
		moveDragRunnable.setup(centerX, centerY, deleteInherentX,
				deleteInherentY, deleteMove, mWindowLayoutParams.x,
				mWindowLayoutParams.y, width, height);
		flingDragRunnable.setup(centerX, centerY, deleteInherentX,
				deleteInherentY, deleteMove, compareType,
				mWindowLayoutParams.x, mWindowLayoutParams.y, width, height);

	}

	public void startDrag(float x, float y) {
		startDragRunnable.start(x, y);
		if (checkDeleteItem(x, y)) {
			moveDragRunnable.absorb(x, y);
		}
	}

	/**
	 * 移动
	 * 
	 * @param x
	 * @param y
	 */
	public void moveDrag(float x, float y) {
		if (checkDeleteItem(x, y)) {
			moveDragRunnable.absorb(x, y);
		} else {
			moveDragRunnable.move(x, y);
		}
	}

	/**
	 * 卸载
	 */
	public void removeDrag(float x, float y) {
		if (checkDeleteItem(x, y)) {
			notifyViewDelete();// 更新Adapter以后进行View移动并让拖动View消失
		} else {
			startDragRunnable.stop();
			moveDragRunnable.stop();
			removeDragRunnable.start();
		}
	}

	/**
	 * 飞
	 * 
	 * @param x
	 * @param y
	 * @param velocityX
	 * @param velocityY
	 */
	public void flingDrag(final float x, final float y, final float velocityX,
			final float velocityY) {
		if (checkDeleteItem(x, y)) {
			notifyViewDelete();// 更新Adapter以后进行View移动并让拖动View消失
		} else {
			flingDragRunnable.start(x, y, velocityX * 0.001f,
					velocityY * 0.001f);
		}
	}

	/**
	 * 删除
	 */
	public void delete() {
		deleteDragRunnable.start();
	}

	/**
	 * 取消删除
	 */
	public void cancelDelete() {
		startDragRunnable.stop();
		moveDragRunnable.stop();
		removeDragRunnable.start();
	}

	/**
	 * 检查是否应删除
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean checkDeleteItem(float x, float y) {
		if (deleteRectF.contains(x, y)) {
			final double x1 = deleteCenterX, y1 = deleteCenterY, x2 = x, y2 = y;
			final double temp_A, temp_B;
			final double C;
			temp_A = x1 > x2 ? (x1 - x2) : (x2 - x1);
			temp_B = y1 > y2 ? (y1 - y2) : (y2 - y1);
			C = Math.sqrt(temp_A * temp_A + temp_B * temp_B);
			if (C > deleteHeight && C > deleteWidth)
				return false;
			return true;
		}
		return false;
	}

	public void notifyViewDelete() {
		dragListener.onDelete();
	}

	public void notifyViewStart() {
		dragListener.onStart();
	}

	public void notifyGridDeleteFinish() {
		if (mDragDeleteFinished) {
			notifyViewFinish();
		} else {
			mGridDeleteFinished = true;
		}
	}

	public void notifyDragDeleteFinish() {
		if (mGridDeleteFinished) {
			notifyViewFinish();
		} else {
			mDragDeleteFinished = true;
		}
	}

	/**
	 * 通知关闭拖动
	 */
	public void notifyViewFinish() {
		mDragDeleteFinished = false;
		mGridDeleteFinished = false;
		mWindowManager.removeView(this);
		dragListener.onRelease();
	}

	/**
	 * 设置删除图片资源
	 * 
	 * @param drawable
	 */
	public void setDeleteDrawable(Drawable drawable) {
		deleteHeight = drawable.getIntrinsicHeight();
		deleteWidth = drawable.getIntrinsicWidth();
		mDeleteView.setImageDrawable(drawable);
	}

	/**
	 * 设置删除图片资源
	 * 
	 * @param resid
	 */
	public void setDeleteResource(int resid) {
		deleteHeight = ContextCompat.getDrawable(getContext(), resid).getIntrinsicHeight();
		deleteWidth = ContextCompat.getDrawable(getContext(), resid).getIntrinsicWidth();
		mDeleteView.setImageResource(resid);

	}

	/**
	 * 设置删除图片位置
	 *
	 */
	public int getDeleteDrawableLocation() {
		return mDeleteLocation;
	}

	/**
	 * 设置删除图片位置
	 * 
	 * @param location
	 */
	public void setDeleteDrawableLocation(int location) {
		mDeleteLocation = location;
	}

	public void setDragController(DragController controller) {
		if (controller != null)
			mDragController = controller;
	}

	public DragController getDragController() {
		return mDragController;
	}

	public int getItemTop() {
		return itemTop;
	}

	public int getItemLeft() {
		return itemLeft;
	}

	public int getItemWidth() {
		return itemWidth;
	}

	public int getItemHeight() {
		return itemHeight;
	}

	public long getDuration() {
		return mDuration;
	}

	public float getStartScale() {
		return startScale;
	}

	public void setStartScale(float scale) {
		startScale = scale;
		startDragRunnable.setStartScale(scale);
		moveDragRunnable.setStartScale(scale);
		flingDragRunnable.setStartScale(scale);
	}

	public float getAbsorbedScale() {
		return absorbedScale;
	}

	public void setAbsorbedScale(float scale) {
		absorbedScale = scale;
		moveDragRunnable.setAbsorbedScale(scale);
		flingDragRunnable.setAbsorbedScale(scale);
	}

	public void setDuration(long duration) {
		mDuration = duration;
		startDragRunnable.setDuration(duration);
		moveDragRunnable.setDuration(duration);
		removeDragRunnable.setDuration(duration);
		flingDragRunnable.setDuration(duration);
	}

	public Interpolator getInterpolator() {
		return mInterpolator;
	}

	public void setInterpolator(Interpolator interpolator) {
		mInterpolator = interpolator;
		startDragRunnable.setInterpolator(interpolator);
		moveDragRunnable.setInterpolator(interpolator);
		removeDragRunnable.setInterpolator(interpolator);
		flingDragRunnable.setInterpolator(interpolator);
	}

	public boolean isDeleteable() {
		return mDeleteable;
	}

	public void setDeleteable(boolean deleteable) {
		this.mDeleteable = deleteable;
	}

	/**
	 * 完成监听
	 * 
	 * @param listener
	 */
	public void setOnViewDragListener(OnViewDragListener listener) {
		dragListener = listener;
	}

	public interface OnViewDragListener {
		public void onStart();

		public void onDelete();

		public void onMerge();

		public void onRelease();
	}

}
