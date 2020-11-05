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
package am.util.mvp.ui;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import am.appcompat.app.BaseActivity;
import am.util.mvp.core.MVPView;
import am.util.mvp.core.MVPViewHolder;

/**
 * MVP Activity
 * Created by Alex on 2020/3/11.
 */
public abstract class MVPActivity extends BaseActivity implements MVPView {

    private final MVPViewHolder<MVPView> mViewHolder = new MVPViewHolder<>();
    private final LifecycleEventObserver mLifecycleEventObserver =
            this::onLifecycleOwnerStateChanged;

    public MVPActivity() {
        getLifecycle().addObserver(mLifecycleEventObserver);
    }

    @ContentView
    public MVPActivity(int contentLayoutId) {
        super(contentLayoutId);
        getLifecycle().addObserver(mLifecycleEventObserver);
    }

    private void onLifecycleOwnerStateChanged(@NonNull LifecycleOwner source,
                                              @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_CREATE:
                mViewHolder.setView(this);
                break;
            case ON_DESTROY:
                mViewHolder.setView(null);
                source.getLifecycle().removeObserver(mLifecycleEventObserver);
                break;
        }
    }

    /**
     * 获取ViewHolder
     *
     * @return ViewHolder
     */
    @NonNull
    protected MVPViewHolder<? extends MVPView> getViewHolder() {
        return mViewHolder;
    }

    /**
     * 设置View
     * 便于在View已创建后手动调用
     *
     * @param view View
     */
    protected void setView(MVPView view) {
        mViewHolder.setView(view);
    }
}