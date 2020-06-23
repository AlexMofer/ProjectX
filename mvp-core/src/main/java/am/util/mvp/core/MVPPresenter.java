/*
 * Copyright (C) 2015 AlexMofer
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

package am.util.mvp.core;

import java.lang.ref.WeakReference;

/**
 * Presenter
 * Created by Alex on 2017/3/13.
 */
@SuppressWarnings("rawtypes")
public abstract class MVPPresenter<V extends MVPView, M extends MVPModel> {

    private WeakReference<MVPViewHolder<V>> mViewHolder;
    private M mModel;

    /**
     * 设置View持有者
     *
     * @param holder View持有者
     * @return Presenter
     */
    @SuppressWarnings("unchecked")
    public <P extends MVPPresenter> P setViewHolder(MVPViewHolder<? extends MVPView> holder) {
        if (mViewHolder != null) {
            mViewHolder.clear();
            mViewHolder = null;
        }
        if (holder == null)
            return (P) this;
        try {
            mViewHolder = new WeakReference<>((MVPViewHolder<V>) holder);
        } catch (ClassCastException e) {
            // ignore
        }
        return (P) this;
    }

    /**
     * 获取View
     *
     * @return View
     */
    protected V getView() {
        final MVPViewHolder<V> holder = mViewHolder == null ? null : mViewHolder.get();
        return holder == null ? null : holder.getView();
    }

    /**
     * 获取Model
     *
     * @return Model
     */
    protected M getModel() {
        return mModel;
    }

    /**
     * 设置Model
     *
     * @param model Model
     */
    protected void setModel(M model) {
        mModel = model;
        //noinspection unchecked
        mModel.setPresenter(this);
    }
}