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

package io.github.alexmofer.projectx.common;

import android.app.Dialog;

import com.am.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import io.github.alexmofer.projectx.business.common.LoadingDialog;

/**
 * 基础Activity
 * Created by Alex on 2020/6/1.
 */
@SuppressWarnings("unused")
public abstract class CommonActivity extends AppCompatActivity {
    private final ArrayList<Dialog> mShowDialogs = new ArrayList<>();
    private Dialog mLoading;
    private boolean mShowLoading = false;
    private boolean mAttachedToWindow = false;

    public CommonActivity() {
    }

    public CommonActivity(int contentLayoutId) {
        super(contentLayoutId);
    }

    /**
     * 获取载入对话框
     *
     * @return 载入对话框
     */
    protected Dialog getLoadingDialog() {
        return new LoadingDialog(this);
    }

    /**
     * 显示默认载入
     */
    public void showLoading() {
        if (mLoading == null)
            mLoading = getLoadingDialog();
        if (mLoading == null)
            return;
        if (mAttachedToWindow)
            mLoading.show();
        else
            mShowLoading = true;

    }

    /**
     * 隐藏默认载入
     */
    public void dismissLoading() {
        if (mLoading == null)
            return;
        if (mAttachedToWindow)
            mLoading.dismiss();
        else
            mShowLoading = false;

    }

    /**
     * 显示对话框
     *
     * @param dialog Dialog
     */
    public void showDialog(Dialog dialog) {
        if (mAttachedToWindow) {
            dialog.show();
        } else {
            if (!mShowDialogs.contains(dialog))
                mShowDialogs.add(dialog);
        }
    }

    /**
     * 关闭对话框
     *
     * @param dialog Dialog
     */
    public void dismissDialog(Dialog dialog) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        mShowDialogs.remove(dialog);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        if (mLoading != null && mShowLoading)
            mLoading.show();
        for (Dialog dialog : mShowDialogs) {
            dialog.show();
        }
        mShowDialogs.clear();
    }

    @Override
    public void onDetachedFromWindow() {
        mAttachedToWindow = false;
        if (mLoading != null && mLoading.isShowing())
            mLoading.dismiss();
        super.onDetachedFromWindow();
    }

    /**
     * 是否附着到窗口层
     *
     * @return 是否
     */
    public boolean isAttachedToWindow() {
        return mAttachedToWindow;
    }
}
