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
import android.view.View;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

/**
 * 基础警报对话框
 * Created by Alex on 2020/6/12.
 */
@SuppressWarnings("unused")
public class BaseAlertDialogFragment extends BaseDialogFragment {

    private boolean mBlockView = false;

    public BaseAlertDialogFragment() {
    }

    @ContentView
    public BaseAlertDialogFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Nullable
    @Override
    public AlertDialog getDialog() {
        return (AlertDialog) super.getDialog();
    }

    @NonNull
    @Override
    public AlertDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), getTheme());
        onCreateAlertDialog(savedInstanceState, builder);
        return builder.create();
    }

    /**
     * 创建警告对话框
     *
     * @param savedInstanceState 保存的实例状态
     * @param builder            构造器
     */
    protected void onCreateAlertDialog(@Nullable Bundle savedInstanceState,
                                       AlertDialog.Builder builder) {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        final View view = getView();
        if (view != null) {
            if (view.getParent() != null) {
                throw new IllegalStateException(
                        "DialogFragment can not be attached to a container view");
            }
            final AlertDialog dialog = getDialog();
            if (dialog != null)
                dialog.setView(view);
        }
        // 屏蔽获取View，避免其将View设置为Dialog的ContentView，否则整个AlertDialog布局将失效
        mBlockView = true;
        super.onActivityCreated(savedInstanceState);
        mBlockView = false;
    }

    @Nullable
    @Override
    public View getView() {
        return mBlockView ? null : super.getView();
    }
}
