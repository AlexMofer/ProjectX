package am.project.x.widgets.supergridview.animator;

import android.view.animation.Interpolator;
import android.widget.ImageView;

import am.project.x.widgets.supergridview.DragView;
import am.project.x.widgets.support.ViewHelper;


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
		mDragScaleX = ViewHelper.getScaleX(mDragView);
		mDragScaleY = ViewHelper.getScaleY(mDragView);
		mDragTranslationX = ViewHelper.getTranslationX(mDragView);
		mDragTranslationY = ViewHelper.getTranslationY(mDragView);
		mDeleteTranslationX = ViewHelper.getTranslationX(mDeleteView);
		mDeleteTranslationY = ViewHelper.getTranslationY(mDeleteView);
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
		ViewHelper.setScaleX(mDragView, scaleX);
		ViewHelper.setScaleY(mDragView, scaleY);
		ViewHelper.setTranslationX(mDragView, mDragTranslationX * op);
		ViewHelper.setTranslationY(mDragView, mDragTranslationY * op);
		ViewHelper.setTranslationX(mDeleteView, mDeleteTranslationX * op);
		ViewHelper.setTranslationY(mDeleteView, mDeleteTranslationY * op);
	}

}
