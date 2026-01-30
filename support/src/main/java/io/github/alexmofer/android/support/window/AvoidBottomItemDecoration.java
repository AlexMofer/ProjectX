package io.github.alexmofer.android.support.window;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 底部避让子项装饰
 * Created by Alex on 2026/1/30.
 */
public final class AvoidBottomItemDecoration extends RecyclerView.ItemDecoration {

    private final int mBottomExtra;
    private int mBottom = 0;

    public AvoidBottomItemDecoration(@NonNull RecyclerView container,
                                     @NonNull AvoidAreaCalculator calculator,
                                     @NonNull LifecycleOwner owner, int extra) {
        mBottomExtra = extra;
        calculator.calculateBottom(owner, b -> {
            if (mBottom != b) {
                mBottom = b;
                container.invalidateItemDecorations();
            }
        });
    }

    public AvoidBottomItemDecoration(@NonNull RecyclerView container,
                                     @NonNull AvoidAreaCalculator calculator,
                                     @NonNull LifecycleOwner owner) {
        this(container, calculator, owner, 0);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
            outRect.bottom = mBottom + mBottomExtra;
        }
    }
}