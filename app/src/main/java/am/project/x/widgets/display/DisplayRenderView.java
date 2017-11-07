package am.project.x.widgets.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 显示渲染视图
 * Created by Xiang Zhicheng on 2017/11/3.
 */

public class DisplayRenderView extends View {

    private final TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private float mRequestedWidth = 0;
    private float mRequestedHeight = 0;
    private float mScale = 1.0f;
    private String mText;

    public DisplayRenderView(Context context) {
        super(context);
    }

    public DisplayRenderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DisplayRenderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int suggestedMinimumWidth = getSuggestedMinimumWidth();
        final int suggestedMinimumHeight = getSuggestedMinimumHeight();
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();
        final int width = (int) Math.max(mRequestedWidth + paddingLeft + paddingRight,
                suggestedMinimumWidth);
        final int height = (int) Math.max(mRequestedHeight + paddingTop + paddingBottom,
                suggestedMinimumHeight);
        if (mScale == 1.0f) {
            setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                    resolveSize(height, heightMeasureSpec));
            return;
        }
        setMeasuredDimension(Math.round(resolveSize(width, widthMeasureSpec) * mScale),
                Math.round(resolveSize(height, heightMeasureSpec) * mScale));
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.density = getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mText == null || mText.length() <= 0)
            return;
        mPaint.setTextSize(360);
        canvas.save();
        canvas.translate(getWidth() * 0.5f, getHeight() * 0.5f);
        canvas.drawText(mText, 0, 0, mPaint);
        canvas.restore();
    }

    public void setSize(int width, int height) {
        mRequestedWidth = width;
        mRequestedHeight = height;
        requestLayout();
        invalidate();
    }

    public void setScale(float scale) {
        mScale = scale;
        requestLayout();
        invalidate();
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }
}
