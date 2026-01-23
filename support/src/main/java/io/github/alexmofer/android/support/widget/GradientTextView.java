package io.github.alexmofer.android.support.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 颜色渐变 TextView
 * Created by Alex on 2026/1/23.
 */
public class GradientTextView extends AppCompatTextView {

    private int mStartColor;
    private float mStartX;
    private float mStartY;
    private int mEndColor;
    private float mEndX;
    private float mEndY;
    private boolean mGradientEnable = true;

    public GradientTextView(@NonNull Context context,
                            float startX, float startY, int startColor,
                            float endX, float endY, int endColor) {
        super(context);
        mStartX = startX;
        mStartY = startY;
        mStartColor = startColor;
        mEndX = endX;
        mEndY = endY;
        mEndColor = endColor;
    }

    public GradientTextView(@NonNull Context context) {
        this(context, 0, 0, Color.WHITE, 1, 1, Color.BLACK);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mGradientEnable) {
            setShader();
        } else {
            getPaint().setShader(null);
        }
    }

    private void setShader() {
        final TextPaint paint = getPaint();
        final int width = getWidth();
        final int height = getHeight();
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();
        final int contentWidth = width - paddingLeft - paddingRight;
        final int contentHeight = height - paddingTop - paddingBottom;
        final float x0 = paddingLeft + contentWidth * mStartX;
        final float y0 = paddingTop + contentHeight * mStartY;
        final float x1 = paddingLeft + contentWidth * mEndX;
        final float y1 = paddingBottom + contentHeight * mEndY;
        paint.setShader(new LinearGradient(x0, y0, x1, y1, mStartColor, mEndColor,
                Shader.TileMode.CLAMP));
    }

    /**
     * 设置是否开启渐变
     *
     * @param enable 是否开启渐变
     */
    public void setGradientEnable(boolean enable) {
        if (mGradientEnable == enable) {
            return;
        }
        mGradientEnable = enable;
        if (mGradientEnable) {
            final int w = getWidth();
            final int h = getHeight();
            if (w > 0 || h > 0) {
                setShader();
                invalidate();
            }
        } else {
            getPaint().setShader(null);
            invalidate();
        }
    }

    /**
     * 设置渐变
     *
     * @param startX     开始坐标点X轴
     * @param startY     开始坐标点Y轴
     * @param startColor 开始颜色
     * @param endX       结束坐标X轴
     * @param endY       结束坐标Y轴
     * @param endColor   结束颜色
     */
    public void setGradient(float startX, float startY, int startColor,
                            float endX, float endY, int endColor) {
        mStartX = startX;
        mStartY = startY;
        mStartColor = startColor;
        mEndX = endX;
        mEndY = endY;
        mEndColor = endColor;
        if (mGradientEnable) {
            final int w = getWidth();
            final int h = getHeight();
            if (w > 0 || h > 0) {
                setShader();
                invalidate();
            }
        }
    }
}