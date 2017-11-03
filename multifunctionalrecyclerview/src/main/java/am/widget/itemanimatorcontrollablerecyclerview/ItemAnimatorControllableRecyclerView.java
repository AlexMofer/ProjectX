/*
 * Copyright (C) 2017 AlexMofer
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

package am.widget.itemanimatorcontrollablerecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;

import am.widget.multifunctionalrecyclerview.R;

/**
 * 子项动画可控的RecyclerView
 * Created by Alex on 2017/10/23.
 */

public class ItemAnimatorControllableRecyclerView extends RecyclerView {

    private long mAddDuration = 0;
    private long mChangeDuration = 0;
    private long mMoveDuration = 0;
    private long mRemoveDuration = 0;

    public ItemAnimatorControllableRecyclerView(Context context) {
        super(context);
        initView(context, null);
    }

    public ItemAnimatorControllableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ItemAnimatorControllableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        updateItemAnimatorDurations();
        TypedArray custom = context.obtainStyledAttributes(attrs,
                R.styleable.ItemAnimatorControllableRecyclerView);
        setSupportsAddAnimations(custom.getBoolean(
                R.styleable.ItemAnimatorControllableRecyclerView_iacAddAnimationsEnable, true));
        setSupportsChangeAnimations(custom.getBoolean(
                R.styleable.ItemAnimatorControllableRecyclerView_iacChangeAnimationsEnable, true));
        setSupportsMoveAnimations(custom.getBoolean(
                R.styleable.ItemAnimatorControllableRecyclerView_iacMoveAnimationsEnable, true));
        setSupportsRemoveAnimations(custom.getBoolean(
                R.styleable.ItemAnimatorControllableRecyclerView_iacRemoveAnimationsEnable, true));
        custom.recycle();
    }

    private void updateItemAnimatorDurations() {
        ItemAnimator animator = getItemAnimator();
        if (animator != null) {
            mAddDuration = animator.getAddDuration();
            mChangeDuration = animator.getChangeDuration();
            mMoveDuration = animator.getMoveDuration();
            mRemoveDuration = animator.getRemoveDuration();
        } else {
            mAddDuration = 0;
            mChangeDuration = 0;
            mMoveDuration = 0;
            mRemoveDuration = 0;
        }
    }

    @Override
    public void setItemAnimator(ItemAnimator animator) {
        super.setItemAnimator(animator);
        updateItemAnimatorDurations();
    }

    /**
     * 设置是否支持添加动画
     *
     * @param supportsAddAnimations 是否支持
     * @see Adapter#notifyItemInserted(int)
     * @see Adapter#notifyItemRangeInserted(int, int)
     */
    public void setSupportsAddAnimations(boolean supportsAddAnimations) {
        ItemAnimator animator = getItemAnimator();
        if (animator == null)
            return;
        if (supportsAddAnimations) {
            animator.setAddDuration(mAddDuration);
        } else {
            animator.setAddDuration(0);
        }
    }

    /**
     * 设置是否支持移除动画
     *
     * @param supportsChangeAnimations 是否支持
     * @see Adapter#notifyItemChanged(int)
     * @see Adapter#notifyItemRangeChanged(int, int)
     */
    public void setSupportsChangeAnimations(boolean supportsChangeAnimations) {
        ItemAnimator animator = getItemAnimator();
        if (animator == null)
            return;
        if (supportsChangeAnimations) {
            animator.setChangeDuration(mChangeDuration);
        } else {
            animator.setChangeDuration(0);
        }
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(supportsChangeAnimations);
        }
    }

    /**
     * 设置是否支持移动动画
     *
     * @param supportsMoveAnimations 是否支持
     * @see Adapter#notifyItemMoved(int, int)
     */
    public void setSupportsMoveAnimations(boolean supportsMoveAnimations) {
        ItemAnimator animator = getItemAnimator();
        if (animator == null)
            return;
        if (supportsMoveAnimations) {
            animator.setMoveDuration(mMoveDuration);
        } else {
            animator.setMoveDuration(0);
        }
    }

    /**
     * 设置是否支持移除动画
     *
     * @param supportsRemoveAnimations 是否支持
     * @see Adapter#notifyItemRemoved(int)
     * @see Adapter#notifyItemRangeRemoved(int, int)
     */
    public void setSupportsRemoveAnimations(boolean supportsRemoveAnimations) {
        ItemAnimator animator = getItemAnimator();
        if (animator == null)
            return;
        if (supportsRemoveAnimations) {
            animator.setRemoveDuration(mRemoveDuration);
        } else {
            animator.setRemoveDuration(0);
        }
    }
}
