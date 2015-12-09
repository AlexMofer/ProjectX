package com.am.widget.supergridview.animator;

import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.am.widget.animators.AnimatorCallback;
import com.am.widget.supergridview.DragView;
import com.am.widget.support.ViewHelper;

/**
 * DragView吸入，一同与DeleteView消失
 * 
 * @author AlexMofer
 * 
 */
public class DragDeleteRunnable extends DragBaseRunnable {

	private float mDragScaleX;
	private float mDragScaleY;
	private float mDragTranslationX;
	private float mDragTranslationY;
	private float mDeleteTranslationX;
	private float mDeleteTranslationY;
	private float mDragOffX;
	private float mDragOffY;
	private final DragStartRunnable mStartDragRunnable;
	private final DragMoveRunnable mDragMoveRunnable;
	private final AnimatorCallback animatorCallback;

	public DragDeleteRunnable(DragView dragParent, ImageView dragView,
			ImageView deleteView, DragStartRunnable startDragRunnable,
			DragMoveRunnable dragMoveRunnable, long duration,
			Interpolator interpolator) {
		super(dragParent, dragView, deleteView, duration, interpolator);
		mStartDragRunnable = startDragRunnable;
		mDragMoveRunnable = dragMoveRunnable;
		animatorCallback = new AnimatorCallback() {
			
			@Override
			public void isStop() {
			}
			
			@Override
			public void isStart() {
			}
			
			@Override
			public void isEnd() {
				start();
			}
		};
	}

	public void start() {
		if (mStartDragRunnable.isRunning()) {
			mStartDragRunnable.addAnimatorCallback(animatorCallback);
			return;
		}
		if (mDragMoveRunnable.isRunning()) {
			mDragMoveRunnable.addAnimatorCallback(animatorCallback);
			return;
		}
		mDragScaleX = ViewHelper.getScaleX(mDragView);
		mDragScaleY = ViewHelper.getScaleY(mDragView);
		mDeleteTranslationX = ViewHelper.getTranslationX(mDeleteView);
		mDeleteTranslationY = ViewHelper.getTranslationY(mDeleteView);
		mDragTranslationX = ViewHelper.getTranslationX(mDragView);
		mDragTranslationY = ViewHelper.getTranslationY(mDragView);
		mDragOffX = mDragTranslationX - mDeleteTranslationX;
		mDragOffY = mDragTranslationY - mDeleteTranslationY;
		super.start();
	}

	@Override
	public void end() {
		super.end();
		mStartDragRunnable.removeAnimatorCallback();
		mDragMoveRunnable.removeAnimatorCallback();
		mDragParent.notifyDragDeleteFinish();
	}

	@Override
	protected void animator(float p) {
		final float op = 1 - p;
		ViewHelper.setScaleX(mDragView, mDragScaleX * op);
		ViewHelper.setScaleY(mDragView, mDragScaleY * op);
		ViewHelper.setTranslationX(mDragView, mDragTranslationX +
				(mDragOffX - mDragTranslationX) * p);
		ViewHelper.setTranslationY(mDragView, mDragTranslationY +
				(mDragOffY - mDragTranslationY) * p);
		ViewHelper.setTranslationX(mDeleteView, mDeleteTranslationX * op);
		ViewHelper.setTranslationY(mDeleteView, mDeleteTranslationY * op);
	}

}
