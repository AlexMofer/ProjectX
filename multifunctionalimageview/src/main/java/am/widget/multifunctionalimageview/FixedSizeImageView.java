/*
 * Copyright (C) 2018 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package am.widget.multifunctionalimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * 定比缩放ImageView
 * Created by Xiang Zhicheng on 2018/12/26.
 */
@SuppressWarnings("unused")
public class FixedSizeImageView extends ForegroundImageView {

    public static final int SCALE_TARGET_HEIGHT = 0;// 对高进行缩放
    public static final int SCALE_TARGET_WIDTH = 1;// 对宽进行缩放
    public static final int SCALE_TARGET_EXPAND = 2;// 扩大方式（宽不足拉伸宽，高不足拉伸高）
    public static final int SCALE_TARGET_INSIDE = 3;// 缩小方式（缩小到一条边刚好与原尺寸一样，另一条小于原尺寸）
    private int mWidthScale = 0;// 宽度缩放比
    private int mHeightScale = 0;// 高度缩放比
    private int mScaleTarget;// 缩放目标

    public FixedSizeImageView(Context context) {
        super(context);
        initView(context, null, 0, 0);
    }

    public FixedSizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0, 0);
    }

    public FixedSizeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, 0);
    }


    public FixedSizeImageView(Context context, AttributeSet attrs, int defStyleAttr,
                              int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray custom = context.obtainStyledAttributes(attrs,
                R.styleable.FixedSizeImageView, defStyleAttr, defStyleRes);
        mWidthScale = custom.getInteger(R.styleable.FixedSizeImageView_sivWidthScale, 0);
        mHeightScale = custom.getInteger(R.styleable.FixedSizeImageView_sivHeightScale, 0);
        mScaleTarget = custom.getInt(R.styleable.FixedSizeImageView_sivScaleTarget,
                SCALE_TARGET_INSIDE);
        custom.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidthScale <= 0 || mHeightScale <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int measureWidth = getMeasuredWidth();
        final int measureHeight = getMeasuredHeight();
        switch (mScaleTarget) {
            case SCALE_TARGET_HEIGHT:
                setMeasuredDimension(measureWidth,
                        measureWidth * mHeightScale / mWidthScale);
                break;
            case SCALE_TARGET_WIDTH:
                setMeasuredDimension(measureHeight * mWidthScale / mHeightScale,
                        measureHeight);
                break;
            case SCALE_TARGET_EXPAND:
                if (measureWidth * mHeightScale < measureHeight * mWidthScale)
                    // 宽不足
                    setMeasuredDimension(measureHeight * mWidthScale / mHeightScale,
                            measureHeight);
                else
                    // 高不足
                    setMeasuredDimension(measureWidth,
                            measureWidth * mHeightScale / mWidthScale);
                break;
            default:
            case SCALE_TARGET_INSIDE:
                if (measureWidth * mHeightScale > measureHeight * mWidthScale)
                    setMeasuredDimension(measureHeight * mWidthScale / mHeightScale,
                            measureHeight);
                else
                    setMeasuredDimension(measureWidth,
                            measureWidth * mHeightScale / mWidthScale);
                break;
        }
    }

    /**
     * 获取缩放目标
     *
     * @return 缩放目标
     */
    public int getScaleTarget() {
        return mScaleTarget;
    }

    /**
     * 设置缩放目标
     *
     * @param target 缩放目标
     */
    public void setScaleTarget(int target) {
        if (target != SCALE_TARGET_INSIDE && target != SCALE_TARGET_HEIGHT
                && target != SCALE_TARGET_WIDTH && target != SCALE_TARGET_EXPAND)
            return;
        if (mScaleTarget != target) {
            mScaleTarget = target;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置缩放比（任意值小于等于0则关闭该功能）
     *
     * @param widthScale  宽度缩放比
     * @param heightScale 高度缩放比
     */
    public void setFixedSize(int widthScale, int heightScale) {
        if (mWidthScale != widthScale || mHeightScale != heightScale) {
            mWidthScale = widthScale;
            mHeightScale = heightScale;
            requestLayout();
            invalidate();
        }
    }
}
