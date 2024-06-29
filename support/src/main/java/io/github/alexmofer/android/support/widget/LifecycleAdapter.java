/*
 * Copyright (C) 2024 AlexMofer
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

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * LifecycleAdapter
 * Created by Alex on 2024/1/5.
 */
public abstract class LifecycleAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private final ArrayList<LifecycleViewHolder> mCreated = new ArrayList<>();
    private final View.OnAttachStateChangeListener mListener = new InnerOnAttachStateChangeListener();

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
        if (holder instanceof LifecycleViewHolder) {
            final LifecycleViewHolder vh = (LifecycleViewHolder) holder;
            vh.onCreate();
            super.onBindViewHolder(holder, position, payloads);
            mCreated.add(vh);
            vh.onStart();
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onViewRecycled(@NonNull VH holder) {
        if (holder instanceof LifecycleViewHolder) {
            final LifecycleViewHolder vh = (LifecycleViewHolder) holder;
            vh.onStop();
            super.onViewRecycled(holder);
            mCreated.remove(vh);
            vh.onDestroy();
        } else {
            super.onViewRecycled(holder);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnAttachStateChangeListener(mListener);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recyclerView.removeOnAttachStateChangeListener(mListener);
        clearCreated();
    }

    private void clearCreated() {
        for (LifecycleViewHolder holder : mCreated) {
            holder.onStop();
            holder.onDestroy();
        }
        mCreated.clear();
    }

    private class InnerOnAttachStateChangeListener
            implements View.OnAttachStateChangeListener {

        @Override
        public void onViewAttachedToWindow(@NonNull View v) {

        }

        @Override
        public void onViewDetachedFromWindow(@NonNull View v) {
            clearCreated();
        }
    }
}
