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

import am.util.job.core.BaseJob;

/**
 * 任务
 * Created by Alex on 2021/3/12.
 */
public abstract class Job<C> extends BaseJob<C> {

    public Job(C callback, boolean weakCallback, int id, Object... params) {
        super(new SparseParams(), HandlerTraverse.getMainTraverse(),
                callback, weakCallback, id, params);
    }

    public Job(C callback, int id, Object... params) {
        this(callback, true, id, params);
    }

    public Job(C callback) {
        this(callback, 0, true);
    }

    @Override
    protected Result generateResult() {
        return SparseResult.get();
    }

    @Override
    protected void recycleResult(Result result) {
        if (result instanceof SparseResult) {
            SparseResult.put((SparseResult) result);
        }
    }

    @Override
    protected Progress generateProgress() {
        return SparseProgress.get();
    }

    @Override
    protected void recycleProgress(Progress progress) {
        if (progress instanceof SparseProgress) {
            SparseProgress.put((SparseProgress) progress);
        }
    }
}
