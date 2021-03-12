/*
 * Copyright (C) 2021 AlexMofer
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
package am.util.job;

import java.util.ArrayList;

import am.util.job.core.BaseJob;

/**
 * 使用SparseArray实现的任务进度
 * Created by Alex on 2021/3/12.
 */
class SparseProgress extends SparseDataArray implements BaseJob.Progress {

    private static final ArrayList<SparseProgress> CACHED = new ArrayList<>();

    private SparseProgress() {
    }

    static SparseProgress get() {
        final SparseProgress progress;
        synchronized (CACHED) {
            if (CACHED.isEmpty()) {
                progress = new SparseProgress();
            } else {
                progress = CACHED.remove(0);
            }
        }
        progress.clear();
        return progress;
    }

    static void put(SparseProgress progress) {
        if (progress == null) {
            return;
        }
        progress.clear();
        synchronized (CACHED) {
            CACHED.add(progress);
        }
    }
}
