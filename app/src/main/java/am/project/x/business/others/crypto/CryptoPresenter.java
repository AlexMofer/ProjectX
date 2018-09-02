/*
 * Copyright (C) 2018 AlexMofer
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
package am.project.x.business.others.crypto;

import am.util.mvp.AMPresenter;

/**
 * Presenter
 */
class CryptoPresenter extends AMPresenter<CryptoView, CryptoModel> implements CryptoView,
        CryptoViewModel {

    private final CryptoModel mModel = new CryptoModel(this);

    CryptoPresenter(CryptoView view) {
        super(view);
    }

    @Override
    protected CryptoModel getModel() {
        return mModel;
    }

    // View
    @Override
    public void onResult(String output) {
        if (isDetachedFromView())
            return;
        getView().onResult(output);
    }

    // ViewModel
    @Override
    public void getMessage(String input) {
        getModel().getMessage(input);
    }

    @Override
    public void getDES(String input) {
        getModel().getDES(input);
    }

    @Override
    public void getAES(String input) {
        getModel().getAES(input);
    }

    @Override
    public void getRSA(String input) {
        getModel().getRSA(input);
    }
}
