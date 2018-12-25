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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 裁剪ImageView
 * Created by Alex on 2018/12/25.
 */
@SuppressWarnings("unused")
@SuppressLint("AppCompatCustomView")
public class ClipImageView extends ImageView {

    private final Path mClipPath = new Path();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mOutlinePath = new Path();
    private ClipOutlineProvider mProvider;

    public ClipImageView(Context context) {
        super(context);
        initView(context, null, 0, 0);
    }

    public ClipImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0, 0);
    }

    public ClipImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClipImageView(Context context, AttributeSet attrs, int defStyleAttr,
                         int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.ClipImageView,
                defStyleAttr, defStyleRes);
        final int type = custom.getInt(R.styleable.ClipImageView_civClipType, 0);
        final float radius = custom.getDimension(R.styleable.ClipImageView_civRoundRectRadius,
                0);
        custom.recycle();
        mClipPath.setFillType(Path.FillType.EVEN_ODD);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mOutlinePath.setFillType(Path.FillType.EVEN_ODD);
        switch (type) {
            default:
                break;
            case 1:
                mProvider = ClipOutlineProvider.CIRCLE;
                break;
            case 2:
                mProvider = ClipOutlineProvider.OVAL;
                break;
            case 3:
                mProvider = ClipOutlineProvider.FULL_ROUND_RECT;
                break;
            case 4:
                mProvider = new RoundRectClipOutlineProvider(radius);
                break;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateClipOutline(false);
    }

    private void invalidateClipOutline(boolean redraw) {
        if (mProvider == null)
            return;
        mClipPath.reset();
        mClipPath.addRect(-1, -1, getWidth() + 1, getHeight() + 1,
                Path.Direction.CW);
        mOutlinePath.reset();
        mProvider.getOutline(this, mOutlinePath);
        mClipPath.addPath(mOutlinePath);
        if (redraw)
            invalidate();
    }

    public void setClipOutlineProvider(ClipOutlineProvider provider) {
        mProvider = provider;
        invalidateClipOutline();
    }

    public void invalidateClipOutline() {
        invalidateClipOutline(true);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mProvider == null) {
            super.draw(canvas);
            return;
        }
        final int layer = Compat.saveLayer(canvas, 0, 0, getWidth(), getHeight(),
                null);
        super.draw(canvas);
        canvas.drawPath(mClipPath, mPaint);
        canvas.restoreToCount(layer);
    }
}
