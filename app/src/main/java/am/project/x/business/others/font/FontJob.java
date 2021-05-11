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
package am.project.x.business.others.font;

import androidx.annotation.NonNull;

import com.am.job.Job;

import am.util.font.TypefaceCollection;
import am.util.font.TypefaceConfig;

/**
 * Job
 */
class FontJob extends Job<FontJob.Callback> {

    private static final int ID_CONFIG = 0;
    private static final int ID_TYPEFACE = 1;

    private FontJob(Callback callback, int id, Object... params) {
        super(callback, id, params);
    }

    static void loadConfig(Callback callback) {
        new FontJob(callback, ID_CONFIG).execute();
    }

    static void loadTypefaceCollection(Callback callback, TypefaceConfig config,
                                       String nameOrAlias) {
        new FontJob(callback, ID_TYPEFACE, config, nameOrAlias).execute();
    }

    @Override
    protected void doInBackground(@NonNull Result result) {
        switch (getId()) {
            case ID_CONFIG:
                handleActionConfig(result);
                break;
            case ID_TYPEFACE:
                handleActionTypeface(result);
                break;
        }
    }

    private void handleActionConfig(@NonNull Result result) {
        final TypefaceConfig config = TypefaceConfig.getInstance();
        if (config.isAvailable()) {
            result.set(true, config);
        }
    }

    private void handleActionTypeface(@NonNull Result result) {
        final TypefaceConfig config = getParams().get(0);
        final String nameOrAlias = getParams().get(1);
        final TypefaceCollection collection = config.getTypefaceCollection(nameOrAlias);
        if (collection != null) {
            result.set(true, collection);
        }
    }

    @Override
    protected void onResult(@NonNull Callback callback, @NonNull Result result) {
        super.onResult(callback, result);
        switch (getId()) {
            case ID_CONFIG:
                notifyActionConfig(callback, result);
                break;
            case ID_TYPEFACE:
                notifyActionTypeface(callback, result);
                break;
        }
    }

    private void notifyActionConfig(@NonNull Callback callback, @NonNull Result result) {
        if (result.isSuccess())
            callback.onLoadConfigSuccess(result.get(0));
        else
            callback.onLoadConfigFailure();
    }

    private void notifyActionTypeface(@NonNull Callback callback, @NonNull Result result) {
        if (result.isSuccess())
            callback.onLoadTypefaceCollectionSuccess(result.get(0));
        else
            callback.onLoadTypefaceCollectionFailure();
    }

    public interface Callback {
        void onLoadConfigFailure();

        void onLoadConfigSuccess(TypefaceConfig config);

        void onLoadTypefaceCollectionFailure();

        void onLoadTypefaceCollectionSuccess(TypefaceCollection collection);
    }
}
