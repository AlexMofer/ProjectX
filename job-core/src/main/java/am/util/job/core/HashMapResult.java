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
package am.util.job.core;

import java.util.ArrayList;

/**
 * 使用HashMap实现的任务结果
 * Created by Alex on 2021/3/12.
 */
class HashMapResult extends HashMapDataArray implements BaseJob.Result {

    private static final ArrayList<HashMapResult> CACHED = new ArrayList<>();

    private boolean mSuccess;

    private HashMapResult() {
    }

    static HashMapResult get() {
        final HashMapResult result;
        synchronized (CACHED) {
            if (CACHED.isEmpty()) {
                result = new HashMapResult();
            } else {
                result = CACHED.remove(0);
            }
        }
        result.clear();
        return result;
    }

    static void put(HashMapResult result) {
        if (result == null) {
            return;
        }
        result.clear();
        synchronized (CACHED) {
            CACHED.add(result);
        }
    }

    @Override
    public void set(boolean success, Object... results) {
        mSuccess = success;
        setAll(results);
    }

    @Override
    public boolean isSuccess() {
        return mSuccess;
    }
}
