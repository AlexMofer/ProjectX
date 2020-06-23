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
package am.appcompat.app;

import android.view.View;

import androidx.annotation.ContentView;
import androidx.fragment.app.Fragment;

/**
 * 基础Fragment
 * Created by Alex on 2020/2/28.
 */
public abstract class BaseFragment extends Fragment {

    public BaseFragment() {
    }

    @ContentView
    public BaseFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    /**
     * 通过ID查找View
     *
     * @param id  View 的资源ID
     * @param <V> View类型
     * @return 对应资源ID的View
     */
    public final <V extends View> V findViewById(int id) {
        final View view = getView();
        if (view == null) {
            // 在错误的时机调用
            throw new IllegalStateException("Fragment " + this
                    + " has not created its view yet.");
        }
        return view.findViewById(id);
    }
}