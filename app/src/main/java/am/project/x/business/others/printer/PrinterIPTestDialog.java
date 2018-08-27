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
package am.project.x.business.others.printer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;

import am.project.x.R;
import am.project.x.utils.AlertDialogUtils;

/**
 * 固定IP测试对话框
 */
class PrinterIPTestDialog extends AppCompatDialog {

    PrinterIPTestDialog(@NonNull Context context) {
        super(context, AlertDialogUtils.getAlertDialogTheme(context));
        setContentView(R.layout.dlg_printer_ip_new);
    }
}
