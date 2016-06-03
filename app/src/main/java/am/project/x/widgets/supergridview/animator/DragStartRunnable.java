package am.project.x.widgets.supergridview.animator;

import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import am.project.x.widgets.supergridview.DragView;
import am.project.x.widgets.supergridview.support.ViewCompat;


public class DragStartRunnable extends DragBaseRunnable {

	private float mStartScale;
	private float mCenterX;
	private float mCenterY;
	private float dragOffX;
	private float dragOffY;
	private int mDeleteInherentX;
	private int mDeleteInherentY;
	private float mDeleteMove;
	private float deleteOffX;
	private float deleteOffY;
	private float mParentLeft;
	private float mParentTop;
	private float mParentWidth;
	private float mParentHeight;
	private boolean isNotifyStart;
	private boolean stopDragView = false;

	public DragStartRunnable(DragView dragParent, ImageView dragView,
							 ImageView deleteView, long duration, Interpolator interpolator, float startScale) {
		super(dragParent, dragView, deleteView, duration, interpolator);
		mStartScale = startScale;
	}

	public void setup(float centerX, float centerY, int deleteInherentX,
			int deleteInherentY, int deleteMove, float parentLeft,
			float parentTop, float parentWidth, float parentHeight,
			boolean deleteable) {
		mCenterX = centerX;
		mCenterY = centerY;
		mDeleteInherentX = deleteInherentX;
		mDeleteInherentY = deleteInherentY;
		mDeleteMove = deleteMove;
		mParentLeft = parentLeft;
		mParentTop = parentTop;
		mParentWidth = parentWidth;
		mParentHeight = parentHeight;
		if (deleteable) {
			mDeleteView.setVisibility(View.VISIBLE);
		} else {
			mDeleteView.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 开始
	 */
	public void start(float x, float y) {
		setOffSet(x, y);
		isNotifyStart = false;
		stopDragView = false;
		start();
	}

	public void setOffSet(float x, float y) {
		dragOffX = x - mCenterX;
		dragOffY = y - mCenterY;
		// TODO 可以优化
		deleteOffX = (x - mParentLeft) * mDeleteMove * 2f / mParentWidth
				- mDeleteMove + mDeleteInherentX;
		deleteOffY = (y - mParentTop) * mDeleteMove * 2f / mParentHeight
				- mDeleteMove + mDeleteInherentY;
	}

	@Override
	public void stop() {
		super.stop();
		if (!isNotifyStart) {
			mDragParent.notifyViewStart();
		}
	}
	
	public void stopDragView() {
		if (!isNotifyStart) {
			mDragParent.notifyViewStart();
		}
		stopDragView = true;
	}

	@Override
	public void run() {
		if (mTime == DEFAULT_FRAME_DELAY * 2 && !isNotifyStart) {
			// 放在此处通知的好处是避免太早通知出现闪烁情况
			mDragParent.notifyViewStart();
			isNotifyStart = true;
		}
		super.run();
	}

	@Override
	protected void animator(float p) {
		if (!stopDragView) {
			scaleAndMoveDrag(p);
		}
		moveDelete(p);
	}

	private void scaleAndMoveDrag(float p) {
		float scale = 1;
		if (mStartScale == 1) {
			return;
		} else if (mStartScale > 1) {
			scale = 1 + (mStartScale - 1) * p;
		} else {
			scale = 1 - (1 - mStartScale) * p;
		}
		ViewCompat.setScaleX(mDragView, scale);
		ViewCompat.setScaleY(mDragView, scale);
		ViewCompat.setTranslationX(mDragView, dragOffX * p);
		ViewCompat.setTranslationY(mDragView, dragOffY * p);
	}

	private void moveDelete(float p) {
		ViewCompat.setTranslationX(mDeleteView, deleteOffX * p);
		ViewCompat.setTranslationY(mDeleteView, deleteOffY * p);
	}

	public void setStartScale(float startScale) {
		mStartScale = startScale;
	}

	/**
	 * DragView是否已被停止
	 * @return
	 */
	public boolean isDragViewStoped() {
		return stopDragView;
	}
}
