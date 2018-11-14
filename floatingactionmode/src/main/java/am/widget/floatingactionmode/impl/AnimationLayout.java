package am.widget.floatingactionmode.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import am.widget.floatingactionmode.R;

/**
 * TODO 低版本Elevation效果
 * Created by Xiang Zhicheng on 2018/11/13.
 */
final class AnimationLayout extends ViewGroup {

    private final View mBackground;
    private final float mCornerRadius;
    private final Rect mBound = new Rect();

    public AnimationLayout(Context context) {
        super(context);
        final Resources resources = context.getResources();
        int color = resources.getColor(R.color.floatingActionModeBackgroundColor);
        float radius = resources.getDimension(R.dimen.floatingActionModeBackgroundCornerRadius);
        float elevation = resources.getDimension(R.dimen.floatingActionModeElevation);

        @SuppressLint("CustomViewStyleable") final TypedArray custom =
                context.obtainStyledAttributes(R.styleable.FloatingActionMode);
        color = custom.getColor(
                R.styleable.FloatingActionMode_floatingActionModeBackgroundColor, color);
        radius = custom.getDimension(
                R.styleable.FloatingActionMode_floatingActionModeBackgroundCornerRadius, radius);
        elevation = custom.getDimension(
                R.styleable.FloatingActionMode_floatingActionModeElevation, elevation);
        custom.recycle();

        final GradientDrawable background = new GradientDrawable();
        background.setColor(color);
        background.setCornerRadius(radius);

        mBackground = new View(context);
        mBackground.setBackgroundDrawable(background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBackground.setElevation(elevation);
        }
        addView(mBackground);

        mCornerRadius = radius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBackground.measure(MeasureSpec.makeMeasureSpec(mBound.width(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mBound.height(), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mBackground.layout(mBound.left, mBound.top, mBound.right, mBound.bottom);
    }

    float getCornerRadius() {
        return mCornerRadius;
    }

    void setBound(Rect bound) {
        mBound.set(bound);
        requestLayout();
    }
}
