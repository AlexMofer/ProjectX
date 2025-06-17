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
package io.github.alexmofer.projectx.business.others.crypto;

import com.am.mvp.core.MVPModel;

/**
 * Model
 */
class CryptoModel extends MVPModel<CryptoPresenter> implements CryptoViewModel, CryptoJob.Callback {

    // ViewModel
    @Override
    public void getMessage(String input) {
        CryptoJob.getMessage(this, input);
    }

    @Override
    public void getDES(String input) {
        CryptoJob.getDES(this, input);
    }

    @Override
    public void getAES(String input) {
        CryptoJob.getAES(this, input);
    }

    @Override
    public void getRSA(String input) {
        CryptoJob.getRSA(this, input);
    }

    // Callback
    @Override
    public void onResult(String output) {
        final CryptoPresenter presenter = getPresenter();
        if (presenter == null)
            return;
        presenter.onResult(output);
    }
}
