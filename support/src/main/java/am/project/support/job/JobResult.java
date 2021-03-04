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
 * 任务结果
 * Created by Alex on 2021/3/1.
 */
public final class JobResult extends AnyValue {

    private static final ArrayList<JobResult> CACHED = new ArrayList<>();

    private boolean mSuccess;

    private JobResult() {
    }

    static JobResult get() {
        final JobResult result;
        synchronized (CACHED) {
            if (CACHED.isEmpty()) {
                result = new JobResult();
            } else {
                result = CACHED.remove(0);
            }
        }
        result.clear();
        return result;
    }

    static void put(JobResult result) {
        if (result == null) {
            return;
        }
        result.clear();
        synchronized (CACHED) {
            CACHED.add(result);
        }
    }

    /**
     * 设置
     *
     * @param success 是否成功
     * @param results 结果数据
     */
    public void set(boolean success, Object... results) {
        mSuccess = success;
        set(results);
    }

    /**
     * 判断是否成功
     *
     * @return 任务成功时返回true
     */
    public boolean isSuccess() {
        return mSuccess;
    }
}
