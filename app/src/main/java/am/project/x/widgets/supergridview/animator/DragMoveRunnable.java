package am.project.x.widgets.supergridview.animator;

import android.view.animation.Interpolator;
import android.widget.ImageView;

import am.project.x.widgets.supergridview.DragView;
import am.project.x.utils.AnimatorViewCompat;


/**
 * 移动及吸附
 * 
 * @author xiangzhicheng
 * 
 */
public class DragMoveRunnable extends DragBaseRunnable {

	public static final long MOVEDURATION = 100;
	private float mStartScale;
	private float mAbsorbedScale;
	private float mStartScaleX;
	private float mStartScaleY;
	private boolean absorbed;
	private float mCenterX;
	private float mCenterY;
	private float mStartDragX;
	private float mStartDragY;
	private float mDragInherentX;
	private float mDragInherentY;
	private float dragOffX;
	private float dragOffY;
	private float mDeleteCenterX;
	private float mDeleteCenterY;
	private int mDeleteInherentX;
	private int mDeleteInherentY;
	private int mDeleteMove;
	private float deleteOffX;
	private float deleteOffY;
	private float mParentLeft;
	private float mParentTop;
	private float mParentWidth;
	private float mParentHeight;
	private final DragStartRunnable mStartDragRunnable;

	public DragMoveRunnable(DragView dragParent, ImageView dragView,
							ImageView deleteView, DragStartRunnable startDragRunnable,
							long duration, Interpolator interpolator, float startScale,
							float absorbedScale) {
		super(dragParent, dragView, deleteView, duration, interpolator);
		mStartDragRunnable = startDragRunnable;
		mDuration = MOVEDURATION;
		mStartScale = startScale;
		mAbsorbedScale = absorbedScale;
	}

	public void setup(float centerX, float centerY, int deleteInherentX,
			int deleteInherentY, int deleteMove, float parentLeft,
			float parentTop, float parentWidth, float parentHeight) {
		absorbed = false;
		mDuration = 100;
		mCenterX = centerX;
		mCenterY = centerY;
		mDeleteInherentX = deleteInherentX;
		mDeleteInherentY = deleteInherentY;
		mDeleteMove = deleteMove;
		mParentLeft = parentLeft;
		mParentTop = parentTop;
		mParentWidth = parentWidth;
		mParentHeight = parentHeight;

	}

	public void setupDelete(float deleteCenterX, float deleteCenterY) {
		mDeleteCenterX = deleteCenterX;
		mDeleteCenterY = deleteCenterY;
		mDragInherentX = mDeleteCenterX - mCenterX;
		mDragInherentY = mDeleteCenterY - mCenterY;
	}

	/**
	 * 吸附
	 * 
	 * @param x
	 * @param y
	 */
	public void absorb(float x, float y) {
		if (mStartDragRunnable.isRunning()) {
			mStartDragRunnable.stopDragView();
		} else {
			moveDelete(x, y);
		}
		if (absorbed) {
			// 已接管DragView的移动
			if (mRunning) {
				// 正在进行接管动画
				dragOffX = mDragInherentX + deleteOffX - mDeleteInherentX;
				dragOffY = mDragInherentY + (deleteOffY - mDeleteInherentY)
						* 0.5f;
			} else {
				AnimatorViewCompat.setTranslationX(mDragView, mDragInherentX
						+ deleteOffX - mDeleteInherentX);
                AnimatorViewCompat.setTranslationY(mDragView, mDragInherentY
						+ (deleteOffY - mDeleteInherentY) * 0.5f);
			}
		} else {
			// 开始接管DragView的移动
			absorbed = true;
			mStartDragX = AnimatorViewCompat.getTranslationX(mDragView);
			mStartDragY = AnimatorViewCompat.getTranslationY(mDragView);
			mStartScaleX = AnimatorViewCompat.getScaleX(mDragView);
			mStartScaleY = AnimatorViewCompat.getScaleY(mDragView);
			dragOffX = mDragInherentX + deleteOffX - mDeleteInherentX;
			dragOffY = mDragInherentY + (deleteOffY - mDeleteInherentY) * 0.5f;
			if (mRunning) {
				// 正在进行释放接管动画
				stop();
			}
			start();

		}
	}

	/**
	 * 移动
	 * 
	 * @param x
	 * @param y
	 */
	public void move(float x, float y) {
		if (mStartDragRunnable.isRunning() && !mRunning) {
			mStartDragRunnable.setOffSet(x, y);
		} else {
			if (mStartDragRunnable.isRunning()) {
				mStartDragRunnable.setOffSet(x, y);
				mStartDragRunnable.stopDragView();
			} else {
				moveDelete(x, y);
			}
			if (absorbed) {
				// DragView已被接管，释放接管并开始释放动画
				absorbed = false;
				mStartDragX = AnimatorViewCompat.getTranslationX(mDragView);
				mStartDragY = AnimatorViewCompat.getTranslationY(mDragView);
				mStartScaleX = AnimatorViewCompat.getScaleX(mDragView);
				mStartScaleY = AnimatorViewCompat.getScaleY(mDragView);
				dragOffX = x - mCenterX;
				dragOffY = y - mCenterY;
				if (mRunning) {
					// 正在进行接管动画
					stop();
				}
				start();
			} else {
				dragOffX = x - mCenterX;
				dragOffY = y - mCenterY;
				if (!mRunning) {
					// 释放动画不在进行
                    AnimatorViewCompat.setTranslationX(mDragView, dragOffX);
                    AnimatorViewCompat.setTranslationY(mDragView, dragOffY);
				}
			}
		}

	}

	/**
	 * 照常移动删除View
	 * 
	 * @param x
	 * @param y
	 */
	private void moveDelete(float x, float y) {
		// TODO 优化
		deleteOffX = (x - mParentLeft) * mDeleteMove * 2f / mParentWidth
				- mDeleteMove + mDeleteInherentX;
		deleteOffY = (y - mParentTop) * mDeleteMove * 2f / mParentHeight
				- mDeleteMove + mDeleteInherentY;
        AnimatorViewCompat.setTranslationX(mDeleteView, deleteOffX);
        AnimatorViewCompat.setTranslationY(mDeleteView, deleteOffY);
	}

	@Override
	public void stop() {
		super.stop();
	}

	@Override
	protected void animator(float p) {
        AnimatorViewCompat.setTranslationX(mDragView, mStartDragX
				+ (dragOffX - mStartDragX) * p);
        AnimatorViewCompat.setTranslationY(mDragView, mStartDragY
				+ (dragOffY - mStartDragY) * p);
		if (absorbed) {
            AnimatorViewCompat.setScaleX(mDragView, mStartScaleX
					- (mStartScaleX - mAbsorbedScale) * p);
            AnimatorViewCompat.setScaleY(mDragView, mStartScaleY
					- (mStartScaleY - mAbsorbedScale) * p);
		} else {
            AnimatorViewCompat.setScaleX(mDragView, mStartScaleX
					- (mStartScaleX - mStartScale) * p);
            AnimatorViewCompat.setScaleY(mDragView, mStartScaleY
					- (mStartScaleY - mStartScale) * p);
		}
	}

	@Override
	public void setDuration(long duration) {
		// 不可修改吸附时间
	}

	public void setStartScale(float scale) {
		mStartScale = scale;
	}

	public void setAbsorbedScale(float scale) {
		mAbsorbedScale = scale;
	}

}
