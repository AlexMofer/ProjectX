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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.RecyclerView;

/**
 * LifecycleViewHolder
 * Created by Alex on 2024/1/5.
 */
public class LifecycleViewHolder extends RecyclerView.ViewHolder implements LifecycleOwner {

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    public LifecycleViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.addOnAttachStateChangeListener(new InnerOnAttachStateChangeListener());
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mRegistry;
    }

    protected void onCreate() {
        mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    protected void onStart() {
        mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    protected void onResume() {
        mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    protected void onPause() {
        mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    }

    protected void onStop() {
        mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    protected void onDestroy() {
        mRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }

    private class InnerOnAttachStateChangeListener implements View.OnAttachStateChangeListener {

        @Override
        public void onViewAttachedToWindow(@NonNull View v) {
            onResume();
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull View v) {
            onPause();
        }
    }
}
