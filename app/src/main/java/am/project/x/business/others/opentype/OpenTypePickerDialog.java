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
package am.project.x.business.others.opentype;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;

import am.project.x.R;

/**
 * 字体选择对话框
 */
class OpenTypePickerDialog extends AlertDialog {

    private final ViewGroup mContent;

    OpenTypePickerDialog(Context context) {
        super(context);
        setTitle(R.string.ot_title_picker);
        final View content = View.inflate(context, R.layout.dlg_opentype_picker, null);
        setView(content);
        mContent = (ViewGroup) content;
    }

    void setItems() {
        mContent.removeAllViews();
    }
}
