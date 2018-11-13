package am.widget.floatingactionmode.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import am.widget.floatingactionmode.R;

/**
 * Created by Xiang Zhicheng on 2018/11/13.
 */
final class AnimationLayout extends ViewGroup {

    private final View mBackground;

    public AnimationLayout(Context context) {
        super(context);

        @SuppressLint("CustomViewStyleable") final TypedArray custom = context.obtainStyledAttributes(R.styleable.FloatingActionMode);
        final int color = custom.getColor(
                R.styleable.FloatingActionMode_floatingActionModeBackgroundColor, 0);
        final float radius = custom.getDimension(
                R.styleable.FloatingActionMode_floatingActionModeBackgroundRadius, 0);
        final float elevation = custom.getDimension(
                R.styleable.FloatingActionMode_floatingActionModeElevation, 0);


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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBackground.measure(MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mBackground.layout(500, 500, 500 + mBackground.getMeasuredWidth(),
                500 + mBackground.getMeasuredHeight());
    }

}
