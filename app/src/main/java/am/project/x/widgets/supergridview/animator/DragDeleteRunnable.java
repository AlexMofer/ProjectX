package am.project.x.widgets.supergridview.animator;

import android.view.animation.Interpolator;
import android.widget.ImageView;

import am.project.x.widgets.animators.AnimatorCallback;
import am.project.x.widgets.supergridview.DragView;
import am.project.x.utils.AnimatorViewCompat;


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
		mDragScaleX = AnimatorViewCompat.getScaleX(mDragView);
		mDragScaleY = AnimatorViewCompat.getScaleY(mDragView);
		mDeleteTranslationX = AnimatorViewCompat.getTranslationX(mDeleteView);
		mDeleteTranslationY = AnimatorViewCompat.getTranslationY(mDeleteView);
		mDragTranslationX = AnimatorViewCompat.getTranslationX(mDragView);
		mDragTranslationY = AnimatorViewCompat.getTranslationY(mDragView);
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
		AnimatorViewCompat.setScaleX(mDragView, mDragScaleX * op);
		AnimatorViewCompat.setScaleY(mDragView, mDragScaleY * op);
		AnimatorViewCompat.setTranslationX(mDragView, mDragTranslationX +
				(mDragOffX - mDragTranslationX) * p);
		AnimatorViewCompat.setTranslationY(mDragView, mDragTranslationY +
				(mDragOffY - mDragTranslationY) * p);
		AnimatorViewCompat.setTranslationX(mDeleteView, mDeleteTranslationX * op);
		AnimatorViewCompat.setTranslationY(mDeleteView, mDeleteTranslationY * op);
	}

}
