package am.widget.shapeimageview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PressShapeImageView extends ShapeImageView {

	private Drawable mPressDrawable;
	private int mId;
	private boolean control = true;
	public PressShapeImageView(Context context) {
		super(context);
	}

	public PressShapeImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PressShapeImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mPressDrawable != null) {
			mPressDrawable.setBounds(getPaddingStart(this), getPaddingTop(),
					getWidth() - getPaddingEnd(this), getHeight() - getPaddingBottom());
			mPressDrawable.draw(canvas);
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mPressDrawable != null
				&& event.getAction() == MotionEvent.ACTION_DOWN) {
			setHotspot(mPressDrawable, event.getX(),
					event.getY());
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void drawableStateChanged() {
		if (mPressDrawable != null && mPressDrawable.isStateful()) {
			if (control) {
				mPressDrawable.setState(getDrawableState());
			} else {
				mPressDrawable.setState(EMPTY_STATE_SET);
			}
		}
		super.drawableStateChanged();

	}

	@Override
	protected boolean verifyDrawable(Drawable who) {
		boolean isPress = false;
		if (mPressDrawable != null && who == mPressDrawable) {
			isPress = true;
		}
		return isPress || super.verifyDrawable(who);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mPressDrawable != null) {
			mPressDrawable.setCallback(this);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if (mPressDrawable != null) {
			mPressDrawable.setCallback(null);
		}
		super.onDetachedFromWindow();
	}

	/**
	 * 设置按压 Drawable
	 * 
	 * @param id
	 */
	public void setPressDrawable(int id) {
		if (mId != id) {
			mId = id;
			setPressDrawable(getDrawable(getContext(), mId));
		}
	}

	/**
	 * 设置按压 Drawable
	 * 
	 * @param drawable
	 */
	public void setPressDrawable(Drawable drawable) {
		if (mPressDrawable != drawable) {
			if (mPressDrawable != null) {
				mPressDrawable.setCallback(null);
			}
			mPressDrawable = drawable;
			mPressDrawable.setCallback(this);
			invalidate();
		}
	}

	public boolean isControl() {
		return control;
	}

	public void setControl(boolean control) {
		this.control = control;
	}

    @SuppressWarnings("all")
	private static Drawable getDrawable(Context context, int id) {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            return getDrawableAPI21(context, id);
        }
        return context.getResources().getDrawable(id);
	}

    @TargetApi(21)
    private static Drawable getDrawableAPI21(Context context, int id) {
        return context.getDrawable(id);
    }

    private static void setHotspot(Drawable drawable, float x, float y) {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            setHotspotAPI21(drawable, x, y);
        }
    }

    @TargetApi(21)
    private static void setHotspotAPI21(Drawable drawable, float x, float y) {
        drawable.setHotspot(x, y);
    }

    private static int getPaddingStart(View view) {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 17) {
            return getPaddingStartAPI17(view);
        }
        return view.getPaddingLeft();
    }

    @TargetApi(17)
    private static int getPaddingStartAPI17(View view) {
        return view.getPaddingStart();
    }

    private static int getPaddingEnd(View view) {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 17) {
            return getPaddingEndAPI17(view);
        }
        return view.getPaddingRight();
    }

    @TargetApi(17)
    private static int getPaddingEndAPI17(View view) {
        return view.getPaddingStart();
    }
}
