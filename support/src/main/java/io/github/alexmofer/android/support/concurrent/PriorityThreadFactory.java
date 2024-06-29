/*
 * Copyright (C) 2024 AlexMofer
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
package io.github.alexmofer.android.support.concurrent;

import androidx.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 优先级线程工厂
 * Created by Alex on 2024/3/1.
 */
public class PriorityThreadFactory implements ThreadFactory {

    private final AtomicInteger mCount = new AtomicInteger(1);
    private final int mPriority;
    private final Adapter mAdapter;

    public PriorityThreadFactory(int priority, @NonNull Adapter adapter) {
        mPriority = priority;
        mAdapter = adapter;
    }

    public PriorityThreadFactory(int priority, @NonNull String name) {
        this(priority, (r, index) -> name + " #" + index);
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, mAdapter.getName(r, mCount.getAndIncrement())) {

            @Override
            public void run() {
                android.os.Process.setThreadPriority(mPriority);
                super.run();
            }
        };
    }

    /**
     * 内容提供者
     */
    public interface Adapter {

        /**
         * 获取名称
         *
         * @param r     Runnable
         * @param index 创建的第几个
         * @return 名称
         */
        @NonNull
        String getName(Runnable r, int index);
    }
}
