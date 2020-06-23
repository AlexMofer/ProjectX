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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ContentView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * MVP对话框
 * Created by Alex on 2020/3/6.
 */
public class BaseDialogFragment extends AppCompatDialogFragment {

    @LayoutRes
    private int mContentLayoutId;

    public BaseDialogFragment() {
    }

    @ContentView
    public BaseDialogFragment(@LayoutRes int contentLayoutId) {
        this();
        mContentLayoutId = contentLayoutId;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return mContentLayoutId != 0 ?
                inflater.inflate(mContentLayoutId, container, false) : null;
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
        if (view == null)
            return requireDialog().findViewById(id);
        return view.findViewById(id);
    }
}