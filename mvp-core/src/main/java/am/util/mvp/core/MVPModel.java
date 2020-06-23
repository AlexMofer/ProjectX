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
 * Model
 * Created by Alex on 2017/3/13.
 */
@SuppressWarnings("rawtypes")
public abstract class MVPModel<P extends MVPPresenter> {

    private WeakReference<P> mPresenter;

    /**
     * 获取Presenter
     *
     * @return Presenter
     */
    protected P getPresenter() {
        return mPresenter == null ? null : mPresenter.get();
    }

    /**
     * 设置Presenter
     *
     * @param presenter Presenter
     */
    void setPresenter(P presenter) {
        mPresenter = new WeakReference<>(presenter);
    }
}
