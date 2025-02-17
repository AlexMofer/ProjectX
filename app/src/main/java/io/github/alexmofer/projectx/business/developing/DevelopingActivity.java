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
package io.github.alexmofer.projectx.business.developing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CompletableFuture;

import io.github.alexmofer.android.support.app.ApplicationData;
import io.github.alexmofer.android.support.app.ApplicationHolder;
import io.github.alexmofer.android.support.concurrent.UIThreadExecutor;
import io.github.alexmofer.projectx.databinding.ActivityDevelopingBinding;

/**
 * 正在开发
 */
public class DevelopingActivity extends AppCompatActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, DevelopingActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityDevelopingBinding.inflate(getLayoutInflater()).getRoot());
        TestApplicationData.getInstance().toast(this);
        InnerApplicationData.getInstance().toast(this);

        System.out.println("lalalalalal-------------------------------------CompletableFuture:" + Thread.currentThread().getName());
        CompletableFuture.supplyAsync(() -> {
                    System.out.println("lalalalalal-------------------------------------supplyAsync:" + Thread.currentThread().getName());
                    return "你好你好";
                })
                .thenAcceptAsync(result -> {
                    System.out.println("lalalalalal-------------------------------------thenAcceptAsync:" + Thread.currentThread().getName());

                }, UIThreadExecutor.get());
    }

    private static class InnerApplicationData extends ApplicationData {

        private final String mTest = "内部类";

        private InnerApplicationData() {
            //no instance
        }

        public static InnerApplicationData getInstance() {
            return ApplicationHolder.getApplicationData(
                    InnerApplicationData.class, InnerApplicationData::new);
        }

        public void toast(Context context) {
            Toast.makeText(context, mTest, Toast.LENGTH_SHORT).show();
        }
    }
}
