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
package io.github.alexmofer.projectx.business.widgets.multiactiontextview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.am.appcompat.app.AppCompatActivity;
import com.am.widget.multiactiontextview.MultiActionClickableSpan;
import com.am.widget.multiactiontextview.MultiActionTextView;

import io.github.alexmofer.projectx.R;

/**
 * 文本点击
 */
public class MultiActionTextViewActivity extends AppCompatActivity implements View.OnClickListener,
        MultiActionClickableSpan.OnTextClickedListener {

    public MultiActionTextViewActivity() {
        super(R.layout.activity_multiactiontextview);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, MultiActionTextViewActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.mat_toolbar);
        final MultiActionTextView text = findViewById(R.id.mat_tv_content);
        final int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        final int colorAccent = ContextCompat.getColor(this, R.color.colorAccent);
        final int colorRipple = ContextCompat.getColor(this, R.color.colorRipple);
        MultiActionClickableSpan action1 = new MultiActionClickableSpan(
                0, 7, colorPrimary, true, false, this);
        MultiActionClickableSpan action2 = new MultiActionClickableSpan(
                10, 15, colorAccent, false, true, this);
        MultiActionClickableSpan action3 = new MultiActionClickableSpan(
                134, 140, colorRipple, false, true, this);
        MultiActionClickableSpan action4 = new MultiActionClickableSpan(
                181, 189, colorRipple, false, true, this);
        MultiActionClickableSpan action5 = new MultiActionClickableSpan(
                214, 230, colorRipple, false, true, this);
        MultiActionClickableSpan action6 = new MultiActionClickableSpan(
                346, 356, colorRipple, false, true, this);
        MultiActionClickableSpan action7 = new MultiActionClickableSpan(
                382, 392, colorRipple, false, true, this);
        text.setText(R.string.mat_content,
                action1, action2, action3, action4, action5, action6, action7);
        text.setOnClickListener(this);
    }

    // Listener
    @Override
    public void onClick(View view) {
        Toast.makeText(this, R.string.mat_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTextClicked(View view, MultiActionClickableSpan span) {
        String text = ((TextView) view).getText().toString();
        Toast.makeText(this, text.substring(span.getStart(), span.getEnd()),
                Toast.LENGTH_SHORT).show();
    }
}
