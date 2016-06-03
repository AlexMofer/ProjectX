package am.project.x.widgets.supergridview.animator;

import android.graphics.RectF;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import am.project.x.widgets.supergridview.DragView;
import am.project.x.widgets.supergridview.support.ViewCompat;

public class DragFlingRunnable extends DragBaseRunnable {

	private long mNormalDuration;
	private final DragStartRunnable mStartDragRunnable;
	private float startScale;
	private Interpolator decelerateInterpolator = new DecelerateInterpolator();
	private Interpolator accelerateInterpolator = new AccelerateInterpolator();
	private Interpolator absorbInterpolator;
	private float startX;
	private float startY;
	private float flingX;
	private float flingY;
	private float startflingX;
	private float startflingY;
	private float startDeleteX;
	private float startDeleteY;
	private float mCenterX;
	private float mCenterY;
	private int mDeleteInherentX;
	private int mDeleteInherentY;
	private float mDeleteMove;
	private float deleteOffX;
	private float deleteOffY;
	private float mParentLeft;
	private float mParentTop;
	private float mParentWidth;
	private float mParentHeight;
	private float mDeleteCenterX;
	private float mDeleteCenterY;
	private int mDeleteHeight;
	private int mDeleteWidth;
	private float mAbsorbedScale;
	private float mStartScaleX;
	private float mStartScaleY;
	private CompareType mComparetype;
	private final RectF rectAbsorb = new RectF();
	private boolean isFlingBack;
	private boolean isFlingDelete;
	private boolean isGoingToAbsorb;

	public enum CompareType {
		XMIN, XMAX, YMIN, YMAX;
	}

	public DragFlingRunnable(DragView dragParent, ImageView dragView,
							 ImageView deleteView, DragStartRunnable startDragRunnable,
							 long duration, Interpolator interpolator, float absorbedScale) {
		super(dragParent, dragView, deleteView, duration, interpolator);
		mNormalDuration = duration;
		absorbInterpolator = interpolator;
		mStartDragRunnable = startDragRunnable;
		mAbsorbedScale = absorbedScale;
	}

	public void setup(float centerX, float centerY, int deleteInherentX,
			int deleteInherentY, int deleteMove,CompareType compareType, float parentLeft,
			float parentTop, float parentWidth, float parentHeight) {
		mCenterX = centerX;
		mCenterY = centerY;
		mDeleteInherentX = deleteInherentX;
		mDeleteInherentY = deleteInherentY;
		mComparetype = compareType;
		mDeleteMove = deleteMove;
		mParentLeft = parentLeft;
		mParentTop = parentTop;
		mParentWidth = parentWidth;
		mParentHeight = parentHeight;
	}

	public void setupDelete(int deleteWidth, int deleteHeight,
			float deleteCenterX, float deleteCenterY) {
		mDeleteWidth = deleteWidth;
		mDeleteHeight = deleteHeight;
		mDeleteCenterX = deleteCenterX;
		mDeleteCenterY = deleteCenterY;
		final int radio = mDeleteWidth > mDeleteHeight ? mDeleteWidth
				: mDeleteHeight;
		rectAbsorb.set(mDeleteCenterX - radio, mDeleteCenterY - radio,
				mDeleteCenterX + radio, mDeleteCenterY + radio);
	}

	public void start(float x, float y, float velocityX, float velocityY) {
		mDuration = mNormalDuration;
		startX = x;
		startY = y;
		flingX = velocityX * mDuration;
		flingY = velocityY * mDuration;
		// TODO 飞行距离应做控制，飞行距离太短应由距离决定时间，计算飞出是否会进入删除区域。
		isFlingDelete = false;
		startflingX = ViewCompat.getTranslationX(mDragView);
		startflingY = ViewCompat.getTranslationY(mDragView);
		isFlingBack = false;
		mInterpolator = decelerateInterpolator;
		if (canFlyToDelete()) {
			final float endX = startX + flingX;
			final float endY = startY + flingY;
			final double line = pointToLine(startX, startY, endX, endY,
					mDeleteCenterX, mDeleteCenterY);
			if (line < mDeleteWidth || line < mDeleteHeight) {
				isFlingDelete = true;
				isGoingToAbsorb = false;
				mDuration += DragMoveRunnable.MOVEDURATION;
				flingX = velocityX * mDuration;
				flingY = velocityY * mDuration;
			}
		}
		start();
	}

	/**
	 * 可以飞行到删除区域
	 * 
	 * @return
	 */
	private boolean canFlyToDelete() {
		final float endX = startX + flingX;
		final float endY = startY + flingY;
		switch (mComparetype) {
		case XMAX:
			if (endX > mDeleteCenterX - mDeleteWidth)
				return true;
			break;
		case XMIN:
			if (endX < mDeleteCenterX + mDeleteWidth)
				return true;
			break;
		case YMAX:
			if (endY > mDeleteCenterY - mDeleteHeight)
				return true;
			break;
		case YMIN:
			if (endY < mDeleteCenterY + mDeleteHeight)
				return true;
			break;
		}
		return false;
	}

	/**
	 * 飞入吸附区域了
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private void isGoingToAbsorb(final float x, final float y) {
		if (rectAbsorb.contains(x, y))
			isGoingToAbsorb = true;
	}

	/**
	 * 飞行进删除区域了
	 * 
	 * @return
	 */
	private void isFlyToDelete(final float x, final float y) {
		switch (mComparetype) {
		case XMAX:
			if (x > mDeleteCenterX - mDeleteWidth)
				isGoingToAbsorb = true;
			break;
		case XMIN:
			if (x < mDeleteCenterX + mDeleteWidth)
				isGoingToAbsorb = true;
			break;
		case YMAX:
			if (y > mDeleteCenterY - mDeleteHeight)
				isGoingToAbsorb = true;
			break;
		case YMIN:
			if (y < mDeleteCenterY + mDeleteHeight)
				isGoingToAbsorb = true;
			break;
		}
	}

	/**
	 * 点到直线的最短距离的判断 点（x0,y0） 到由两点组成的线段（x1,y1） ,( x2,y2 )
	 * 
	 * @param startX
	 * @param startY
	 * @param flingX
	 * @param flingY
	 * @param mDeleteCenterX
	 * @param mDeleteCenterY
	 * @return
	 */
	private double pointToLine(float startX, float startY, float flingX,
			float flingY, float mDeleteCenterX, float mDeleteCenterY) {
		double space = 0;
		final double flingLine, startCLines, endCLine;
		flingLine = lineSpace(startX, startY, flingX, flingY);// 线段的长度
		startCLines = lineSpace(startX, startY, mDeleteCenterX, mDeleteCenterY);// (x1,y1)到点的距离
		endCLine = lineSpace(flingX, flingY, mDeleteCenterX, mDeleteCenterY);// (x2,y2)到点的距离
		if (endCLine + startCLines == flingLine) {// 点在线段上
			space = 0;
			return space;
		}
		if (flingLine <= 0.000001) {// 不是线段，是一个点
			space = startCLines;
			return space;
		}
		if (endCLine * endCLine >= flingLine * flingLine + startCLines
				* startCLines) { // 组成直角三角形或钝角三角形，(x1,y1)为直角或钝角
			space = startCLines;
			return space;
		}
		if (startCLines * startCLines >= flingLine * flingLine + endCLine
				* endCLine) {// 组成直角三角形或钝角三角形，(x2,y2)为直角或钝角
			space = endCLine;
			return space;
		}
		// 组成锐角三角形，则求三角形的高
		final double p = (flingLine + startCLines + endCLine) / 2;// 半周长
		final double s = Math.sqrt(p * (p - flingLine) * (p - startCLines)
				* (p - endCLine));// 海伦公式求面积
		space = 2 * s / flingLine;// 返回点到线的距离（利用三角形面积公式求高）
		return space;

	}

	// 计算两点之间的距离

	private double lineSpace(float x1, float y1, float x2, float y2) {

		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	public void setOffSet(float x, float y) {
		deleteOffX = (x - mParentLeft) * mDeleteMove * 2f / mParentWidth
				- mDeleteMove + mDeleteInherentX;
		deleteOffY = (y - mParentTop) * mDeleteMove * 2f / mParentHeight
				- mDeleteMove + mDeleteInherentY;
	}

	@Override
	public void end() {
		super.end();
		if (isFlingDelete) {
			isFlingDelete = false;
			mDragParent.notifyViewDelete();
		} else {
			if (!isFlingBack) {
				isFlingBack = true;
				mInterpolator = accelerateInterpolator;
				startDeleteX = ViewCompat.getTranslationX(mDeleteView);
				startDeleteY = ViewCompat.getTranslationY(mDeleteView);
				start();
			} else {
				isFlingBack = false;
				mDragParent.notifyViewFinish();
			}
		}
	}

	@Override
	protected void animator(float p) {
		if (isFlingDelete) {
			delete(p);
		} else {
			if (isFlingBack)
				remove(p);
			else
				fling(p);
		}
	}

	private void delete(float p) {
		final float translationX = startflingX + flingX * p;
		final float translationY = startflingY + flingY * p;
		final float x = mCenterX + translationX;
		final float y = mCenterY + translationY;
		if (!isGoingToAbsorb) {
			isGoingToAbsorb(x, y);
			isFlyToDelete(x, y);
			mStartScaleX = ViewCompat.getScaleX(mDragView);
			mStartScaleY = ViewCompat.getScaleY(mDragView);
		}
		if (isGoingToAbsorb) {
			final float ix = mDeleteCenterX - mCenterX;
			final float iy = mDeleteCenterY - mCenterY;
			if (mStartDragRunnable.isRunning()) {
				mStartDragRunnable.stopDragView();
				mStartDragRunnable.setOffSet(x, y);
			} else {
				setOffSet(x, y);
                ViewCompat.setTranslationX(mDeleteView, deleteOffX);
                ViewCompat.setTranslationY(mDeleteView, deleteOffY);
			}
			float op = 0;
			if (mDuration - mTime <= DragMoveRunnable.MOVEDURATION) {
				op = absorbInterpolator
						.getInterpolation((float) (DragMoveRunnable.MOVEDURATION - mDuration + mTime)
								/ (float) DragMoveRunnable.MOVEDURATION);
                ViewCompat.setScaleX(mDragView, mStartScaleX - (mStartScaleX - mAbsorbedScale) * op);
                ViewCompat.setScaleY(mDragView, mStartScaleY - (mStartScaleY - mAbsorbedScale) * op);
			}
            ViewCompat.setTranslationX(mDragView, translationX + deleteOffX - mDeleteInherentX - (translationX - ix) * op);
            ViewCompat.setTranslationY(mDragView, translationY + deleteOffY - mDeleteInherentY - (translationY - iy) * op);
		} else {
			if (mStartDragRunnable.isRunning()) {
				mStartDragRunnable.setOffSet(x, y);
			} else {
                ViewCompat.setTranslationX(mDragView, translationX);
                ViewCompat.setTranslationY(mDragView, translationY);
				setOffSet(x, y);
                ViewCompat.setTranslationX(mDeleteView, deleteOffX);
                ViewCompat.setTranslationY(mDeleteView, deleteOffY);
			}
		}
	}

	private void fling(float p) {
		final float translationX = startflingX + flingX * p;
		final float translationY = startflingY + flingY * p;
		final float x = mCenterX + translationX;
		final float y = mCenterY + translationY;
		if (mStartDragRunnable.isRunning()) {
			mStartDragRunnable.setOffSet(x, y);
		} else {
            ViewCompat.setTranslationX(mDragView, translationX);
            ViewCompat.setTranslationY(mDragView, translationY);
			setOffSet(x, y);
            ViewCompat.setTranslationX(mDeleteView, deleteOffX);
            ViewCompat.setTranslationY(mDeleteView, deleteOffY);
		}

	}

	private void remove(float p) {
        ViewCompat.setTranslationX(mDragView, (startflingX + flingX) * (1 - p));
        ViewCompat.setTranslationY(mDragView, (startflingY + flingY) * (1 - p));
        ViewCompat.setTranslationX(mDeleteView, startDeleteX * (1 - p));
        ViewCompat.setTranslationY(mDeleteView, startDeleteY * (1 - p));
		float scale = 0;
		if (startScale == 1) {
			return;
		} else if (startScale > 1) {
			scale = startScale - (startScale - 1) * p;
		} else {
			scale = startScale + (1 - startScale) * p;
		}
        ViewCompat.setScaleX(mDragView, scale);
        ViewCompat.setScaleY(mDragView, scale);
	}

	public void setStartScale(float scale) {
		startScale = scale;
	}

	public void setAbsorbedScale(float scale) {
		mAbsorbedScale = scale;
	}
	
	@Override
	public void setDuration(long duration) {
		mNormalDuration = duration;
	}
	
	@Override
	public void setInterpolator(Interpolator interpolator) {
		absorbInterpolator = interpolator;
	}

}
