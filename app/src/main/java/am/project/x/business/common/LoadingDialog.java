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
package am.project.x.business.common;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.widget.FrameLayout;

import am.project.x.R;


/**
 * 载入对话框
 */
public class LoadingDialog extends AppCompatDialog {

    public LoadingDialog(Context context) {
        super(context, R.style.TransparentDialog);
        final FrameLayout contentView = new FrameLayout(context);
        // TODO
//        final MaterialProgressImageView loading = new MaterialProgressImageView(context);
//        loading.setColorSchemeColors(
//                ContextCompat.getColor(context, android.R.color.holo_red_light),
//                ContextCompat.getColor(context, android.R.color.holo_blue_light),
//                ContextCompat.getColor(context, android.R.color.holo_green_light),
//                ContextCompat.getColor(context, android.R.color.holo_orange_light),
//                ContextCompat.getColor(context, android.R.color.holo_purple));
//        contentView.addView(loading, new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
//                Gravity.CENTER));
        setContentView(contentView);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
}
