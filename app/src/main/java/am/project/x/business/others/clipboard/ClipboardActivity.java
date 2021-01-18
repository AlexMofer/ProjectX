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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Random;

import am.appcompat.app.BaseActivity;
import am.project.x.R;
import am.util.clipboard.SuperClipboard;

/**
 * 剪切板
 * Created by Alex on 2019/3/13.
 */
public class ClipboardActivity extends BaseActivity {

    private TextView mVResult;
    private final ClipboardBean mData = ClipboardBean.test();

    public ClipboardActivity() {
        super(R.layout.activity_clipboard);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, ClipboardActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.clipboard_toolbar);
        mVResult = findViewById(R.id.clipboard_tv_result);
        findViewById(R.id.clipboard_btn_copy).setOnClickListener(v -> copy());
        findViewById(R.id.clipboard_btn_paste).setOnClickListener(v -> paste());
        this.<TextView>findViewById(R.id.clipboard_tv_target).setText(mData.toString());
    }

    private void copy() {
        if (SuperClipboard.copy(this,
                SuperClipboard.MIME_ITEM + "/vnd.projectx.data", mData)) {
            Toast.makeText(this, R.string.clipboard_info,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void paste() {
        final Serializable result = SuperClipboard.getClipData(this);
        if (result instanceof ClipboardBean) {
            mVResult.setText(result.toString());
        }
    }

    private static class ClipboardBean implements Serializable {
        private final byte mByte;
        private final short mShort;
        private final int mInt;
        private final long mLong;
        private final float mFloat;
        private final double mDouble;
        private final boolean mBoolean;
        private final char mChar;
        private final String mString;

        private ClipboardBean(byte mByte, short mShort, int mInt, long mLong, float mFloat,
                              double mDouble, boolean mBoolean, char mChar, String mString) {
            this.mByte = mByte;
            this.mShort = mShort;
            this.mInt = mInt;
            this.mLong = mLong;
            this.mFloat = mFloat;
            this.mDouble = mDouble;
            this.mBoolean = mBoolean;
            this.mChar = mChar;
            this.mString = mString;
        }

        static ClipboardBean test() {
            final Random random = new Random();
            final int v = random.nextInt(250);
            return new ClipboardBean((byte) (127 - v), (short) (32767 - v), Integer.MAX_VALUE / v,
                    Long.MAX_VALUE / v, Float.MAX_VALUE / v,
                    Double.MAX_VALUE / v, true, Character.MAX_VALUE,
                    "Test:" + v);
        }

        @NotNull
        @Override
        public String toString() {
            return "ClipboardBean{" +
                    "mByte=" + mByte +
                    ", mShort=" + mShort +
                    ", mInt=" + mInt +
                    ", mLong=" + mLong +
                    ", mFloat=" + mFloat +
                    ", mDouble=" + mDouble +
                    ", mBoolean=" + mBoolean +
                    ", mChar=" + mChar +
                    ", mString='" + mString + '\'' +
                    '}';
        }
    }
}
