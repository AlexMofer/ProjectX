package am.project.x.widgets.supergridview.animator;

import android.view.animation.Interpolator;
import android.widget.ImageView;

import am.project.x.widgets.supergridview.DragView;
import am.project.x.widgets.supergridview.support.ViewCompat;


public class DragRemoveRunnable extends DragBaseRunnable {

	private float mDragScaleX;
	private float mDragScaleY;
	private float mDragTranslationX;
	private float mDragTranslationY;
	private float mDeleteTranslationX;
	private float mDeleteTranslationY;
	public DragRemoveRunnable(DragView dragParent, ImageView dragView,
							  ImageView deleteView, long duration, Interpolator interpolator) {
		super(dragParent, dragView, deleteView, duration, interpolator);
	}
	
	public void start() {
		mDragScaleX = ViewCompat.getScaleX(mDragView);
		mDragScaleY = ViewCompat.getScaleY(mDragView);
		mDragTranslationX = ViewCompat.getTranslationX(mDragView);
		mDragTranslationY = ViewCompat.getTranslationY(mDragView);
		mDeleteTranslationX = ViewCompat.getTranslationX(mDeleteView);
		mDeleteTranslationY = ViewCompat.getTranslationY(mDeleteView);
		super.start();
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
	}
	
	@Override
	public void end() {
		super.end();
		mDragParent.notifyViewFinish();
	}
	
	@Override
	protected void animator(float p) {
		final float op = 1 - p;
		float scaleX = 1, scaleY = 1;
		if (mDragScaleX > 1) {
			scaleX = 1 + (mDragScaleX - 1) * op;
		} else {
			scaleX = 1 - (1 - mDragScaleX) * op;
		}
		if (mDragScaleY > 1) {
			scaleY = 1 + (mDragScaleY - 1) * op;
		} else {
			scaleY = 1 - (1 - mDragScaleY) * op;
		}
		ViewCompat.setScaleX(mDragView, scaleX);
		ViewCompat.setScaleY(mDragView, scaleY);
		ViewCompat.setTranslationX(mDragView, mDragTranslationX * op);
		ViewCompat.setTranslationY(mDragView, mDragTranslationY * op);
		ViewCompat.setTranslationX(mDeleteView, mDeleteTranslationX * op);
		ViewCompat.setTranslationY(mDeleteView, mDeleteTranslationY * op);
	}

}
