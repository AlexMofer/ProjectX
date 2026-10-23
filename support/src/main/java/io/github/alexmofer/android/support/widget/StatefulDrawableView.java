/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Drawable 可响应状态的 View
 * Created by Alex on 2026/10/23.
 */
public class StatefulDrawableView extends View {
    private final ArrayList<Drawable> mDrawables = new ArrayList<>();

    public StatefulDrawableView(Context context) {
        super(context);
    }

    public StatefulDrawableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatefulDrawableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatefulDrawableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void unbindDrawable(@NonNull Drawable drawable) {
        if (!mDrawables.remove(drawable)) {
            return;
        }
        if (isAttachedToWindow()) {
            drawable.setVisible(false, false);
        }
        drawable.setCallback(null);
        unscheduleDrawable(drawable);
    }

    protected void bindDrawable(@NonNull Drawable drawable) {
        if (mDrawables.contains(drawable)) {
            return;
        }
        mDrawables.add(drawable);
        drawable.setLayoutDirection(getLayoutDirection());
        if (drawable.isStateful()) {
            drawable.setState(getDrawableState());
        }
        if (isAttachedToWindow()) {
            drawable.setVisible(getWindowVisibility() == VISIBLE && isShown(), false);
        }
        drawable.setCallback(this);
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || mDrawables.contains(who);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final int[] state = getDrawableState();
        boolean changed = false;
        for (Drawable drawable : mDrawables) {
            if (drawable.isStateful()) {
                changed |= drawable.setState(state);
            }
        }

        if (changed) {
            invalidate();
        }
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        for (Drawable drawable : mDrawables) {
            drawable.setHotspot(x, y);
        }
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        for (Drawable drawable : mDrawables) {
            drawable.jumpToCurrentState();
        }
    }
}
