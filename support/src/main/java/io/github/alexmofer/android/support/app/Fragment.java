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
package io.github.alexmofer.android.support.app;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.alexmofer.android.support.transition.FragmentTransitions;
import io.github.alexmofer.android.support.utils.FragmentUtils;

/**
 * 基础 Fragment
 * Created by Alex on 2026/4/25.
 */
public abstract class Fragment extends androidx.fragment.app.Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onConfigTransition(savedInstanceState);
    }

    protected long getTransitionDuration() {
        return requireContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    /**
     * 配置转场
     *
     * @param savedInstanceState 状态
     */
    protected void onConfigTransition(@Nullable Bundle savedInstanceState) {
        FragmentTransitions.setTransitions(this, getTransitionDuration());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onConfigBackPressed();
    }

    protected void onConfigBackPressed() {
        if (this instanceof BackPressable) {
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                    new InnerOnBackPressedCallback());
        }
    }

    public final void dispatchBackPressed() {
        if (onBackPressed()) {
            return;
        }
        removeSelf();
    }

    /**
     * 移除自身
     *
     * @return 移除成功时返回 true
     */
    @SuppressWarnings("UnusedReturnValue")
    protected boolean removeSelf() {
        return FragmentUtils.tryRemoveSelf(this);
    }

    /**
     * 返回操作
     *
     * @return 返回 true 表示消耗本次返回操作
     */
    protected boolean onBackPressed() {
        return false;
    }

    private class InnerOnBackPressedCallback extends OnBackPressedCallback {

        public InnerOnBackPressedCallback() {
            super(true);
        }

        @Override
        public void handleOnBackPressed() {
            dispatchBackPressed();
        }
    }
}
