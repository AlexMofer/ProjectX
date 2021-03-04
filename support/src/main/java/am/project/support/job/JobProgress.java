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
package am.project.support.job;

import java.util.ArrayList;

/**
 * 任务进度
 * Created by Alex on 2021/3/1.
 */
public final class JobProgress extends AnyValue {

    private static final ArrayList<JobProgress> CACHED = new ArrayList<>();

    private JobProgress() {
    }

    static JobProgress get() {
        final JobProgress progress;
        synchronized (CACHED) {
            if (CACHED.isEmpty()) {
                progress = new JobProgress();
            } else {
                progress = CACHED.remove(0);
            }
        }
        progress.clear();
        return progress;
    }

    static void put(JobProgress progress) {
        if (progress == null) {
            return;
        }
        progress.clear();
        synchronized (CACHED) {
            CACHED.add(progress);
        }
    }
}
