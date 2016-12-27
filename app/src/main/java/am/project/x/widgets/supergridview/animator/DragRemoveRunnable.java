package am.project.x.widgets.supergridview.animator;

import android.view.animation.Interpolator;
import android.widget.ImageView;

import am.project.x.widgets.supergridview.DragView;
import am.project.x.widgets.supergridview.support.AnimatorViewCompat;


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
		mDragScaleX = AnimatorViewCompat.getScaleX(mDragView);
		mDragScaleY = AnimatorViewCompat.getScaleY(mDragView);
		mDragTranslationX = AnimatorViewCompat.getTranslationX(mDragView);
		mDragTranslationY = AnimatorViewCompat.getTranslationY(mDragView);
		mDeleteTranslationX = AnimatorViewCompat.getTranslationX(mDeleteView);
		mDeleteTranslationY = AnimatorViewCompat.getTranslationY(mDeleteView);
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
		AnimatorViewCompat.setScaleX(mDragView, scaleX);
		AnimatorViewCompat.setScaleY(mDragView, scaleY);
		AnimatorViewCompat.setTranslationX(mDragView, mDragTranslationX * op);
		AnimatorViewCompat.setTranslationY(mDragView, mDragTranslationY * op);
		AnimatorViewCompat.setTranslationX(mDeleteView, mDeleteTranslationX * op);
		AnimatorViewCompat.setTranslationY(mDeleteView, mDeleteTranslationY * op);
	}

}
