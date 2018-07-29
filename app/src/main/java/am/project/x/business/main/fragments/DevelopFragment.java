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
package am.project.x.business.main.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import am.project.x.R;
import am.project.x.base.BaseFragment;

public class DevelopFragment extends BaseFragment {

    @Override
    protected int getContentViewLayout(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        return R.layout.fragment_main_develop;
    }

    @Override
    protected void initializeFragment(Activity activity, @Nullable Bundle savedInstanceState) {

    }

    public static DevelopFragment newInstance() {
        return new DevelopFragment();
    }
}
