/*
 * Copyright (C) 2019 AlexMofer
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
package am.project.x.business.others.clipboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import androidx.annotation.Nullable;

/**
 * 剪切板
 * Created by Alex on 2019/3/13.
 */
public class ClipboardActivity extends BaseActivity implements View.OnClickListener {

    private final ClipboardBean mData = ClipboardBean.test();
    private TextView mVResult;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ClipboardActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_clipboard;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.clipboard_toolbar);
        mVResult = findViewById(R.id.clipboard_tv_result);
        findViewById(R.id.clipboard_btn_copy).setOnClickListener(this);
        findViewById(R.id.clipboard_btn_paste).setOnClickListener(this);
        this.<TextView>findViewById(R.id.clipboard_tv_target).setText(mData.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clipboard_btn_copy:
                if (MyClipboardHelper.getInstance().copy(this, mData))
                    Toast.makeText(this, R.string.clipboard_info,
                            Toast.LENGTH_SHORT).show();
                break;
            case R.id.clipboard_btn_paste:
                final ClipboardBean result = MyClipboardHelper.getInstance().paste(this);
                if (result != null) {
                    mVResult.setText(result.toString());
                }
                break;
        }
    }
}
