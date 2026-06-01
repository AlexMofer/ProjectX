package io.github.alexmofer.android.support.widget.builders;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import io.github.alexmofer.android.support.function.FunctionPObjectBoolean;
import io.github.alexmofer.android.support.widget.AutoAdjustSpanCountGridLayoutManager;
import io.github.alexmofer.android.support.widget.AutoCenterHorizontalLinearLayoutManager;

/**
 * RecyclerView
 * Created by Alex on 2026/3/9.
 */
public class RecyclerViewBuilder extends ViewGroupBuilder {
    private final RecyclerView mView;

    public RecyclerViewBuilder(@NonNull RecyclerView view) {
        super(view);
        this.mView = view;
    }

    public RecyclerViewBuilder(@NonNull Context context) {
        this(new RecyclerView(context));
    }

    @NonNull
    @Override
    public RecyclerView build() {
        return mView;
    }

    public RecyclerViewBuilder setLayoutManager(@Nullable RecyclerView.LayoutManager layout) {
        mView.setLayoutManager(layout);
        return this;
    }

    public RecyclerViewBuilder setGridLayoutManager(int minCount, int itemMinWidth,
                                                    boolean notifyItemDecorations) {
        return setLayoutManager(new AutoAdjustSpanCountGridLayoutManager(
                mView.getContext(), minCount, itemMinWidth, notifyItemDecorations));
    }

    public RecyclerViewBuilder setVertical() {
        return setLayoutManager(new LinearLayoutManager(mView.getContext()));
    }

    public RecyclerViewBuilder setHorizontal() {
        return setLayoutManager(new LinearLayoutManager(mView.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
    }

    public RecyclerViewBuilder setAutoCenterHorizontal() {
        return setLayoutManager(new AutoCenterHorizontalLinearLayoutManager(mView.getContext()));
    }

    public RecyclerViewBuilder addItemDecoration(@NonNull RecyclerView.ItemDecoration decor) {
        mView.addItemDecoration(decor);
        return this;
    }

    public RecyclerViewBuilder setAdapter(@Nullable RecyclerView.Adapter<?> adapter) {
        mView.setAdapter(adapter);
        return this;
    }

    public RecyclerViewBuilder addOnScrollDirectionListener(@NonNull FunctionPObjectBoolean<RecyclerView> listener) {
        mView.addOnScrollListener(new OnScrollDirectionListener(listener));
        return this;
    }

    public RecyclerViewBuilder attachItemTouchHelper(@NonNull ItemTouchHelper helper) {
        helper.attachToRecyclerView(mView);
        return this;
    }

    private static class OnScrollDirectionListener extends RecyclerView.OnScrollListener {
        private final FunctionPObjectBoolean<RecyclerView> mListener;
        private Boolean mPositive;

        public OnScrollDirectionListener(@NonNull FunctionPObjectBoolean<RecyclerView> listener) {
            mListener = listener;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            if (dy == 0) {
                return;
            }
            final boolean positive = dy > 0;
            if (Objects.equals(mPositive, positive)) {
                return;
            }
            mPositive = positive;
            mListener.execute(recyclerView, positive);
        }
    }
}
