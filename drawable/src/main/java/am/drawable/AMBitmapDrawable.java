/*
 * Copyright (C) 2020 AlexMofer
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
package am.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * 安全的Bitmap
 * Created by Alex on 2020/2/13.
 */
public class AMBitmapDrawable extends DrawableWrapper {

    private Bitmap mBitmap;

    public AMBitmapDrawable(Resources res, Bitmap bitmap) {
        super(new BitmapDrawable(res, bitmap));
        mBitmap = bitmap;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mBitmap == null || mBitmap.isRecycled())
            return;
        try {
            super.draw(canvas);
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public BitmapDrawable getWrappedDrawable() {
        return (BitmapDrawable) super.getWrappedDrawable();
    }

    @Override
    public void setWrappedDrawable(Drawable drawable) {
        throw new RuntimeException("Not support!");
    }

    /**
     * 设置Bitmap
     */
    public void setBitmap(Resources res, Bitmap bitmap) {
        if (mBitmap == bitmap) {
            invalidateSelf();
            return;
        }
        mBitmap = bitmap;
        super.setWrappedDrawable(new BitmapDrawable(res, bitmap));
    }
}