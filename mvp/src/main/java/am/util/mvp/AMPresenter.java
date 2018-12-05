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

package am.util.mvp;

import android.os.Bundle;

/**
 * Presenter
 * Created by Alex on 2017/3/13.
 */
@SuppressWarnings("WeakerAccess")
public abstract class AMPresenter<V extends AMView, M extends AMModel> {
    private V mView;

    public AMPresenter(V view) {
        mView = view;
    }

    protected final V getView() {
        return mView;
    }

    protected abstract M getModel();

    protected void onCreated(Bundle savedInstanceState) {
        final M model = getModel();
        if (model != null)
            model.onCreated(savedInstanceState);
    }

    protected void onStarted() {
        final M model = getModel();
        if (model != null)
            model.onStarted();
    }

    protected void onResumed() {
        final M model = getModel();
        if (model != null)
            model.onResumed();
    }

    protected void onPaused() {
        final M model = getModel();
        if (model != null)
            model.onPaused();
    }

    protected void onStopped() {
        final M model = getModel();
        if (model != null)
            model.onStopped();
    }

    protected void onSaveInstanceState(Bundle outState) {
        final M model = getModel();
        if (model != null)
            model.onSaveInstanceState(outState);
    }

    protected void onDestroyed() {
        final M model = getModel();
        if (model != null)
            model.onDestroyed();
    }

    void detach() {
        mView = null;
        onDetachedFromView();
        final M model = getModel();
        if (model != null)
            model.detach();
    }

    protected void onDetachedFromView() {
    }

    protected boolean isDetachedFromView() {
        return mView == null;
    }
}