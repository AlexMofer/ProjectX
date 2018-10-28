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

import am.project.support.job.Job;
import am.util.font.TypefaceCollection;
import am.util.font.TypefaceConfig;

/**
 * Job
 */
class FontJob extends Job<FontJob.Callback> {

    private static final int ACTION_CONFIG = 0;
    private static final int ACTION_TYPEFACE = 1;

    private FontJob(Callback callback, int action, Object... params) {
        super(callback, action, params);
    }

    static void loadConfig(Callback callback) {
        new FontJob(callback, ACTION_CONFIG).execute();
    }

    static void loadTypefaceCollection(Callback callback, TypefaceConfig config,
                                       String nameOrAlias) {
        new FontJob(callback, ACTION_TYPEFACE, config, nameOrAlias).execute();
    }

    @Override
    protected void doInBackground() {
        switch (getAction()) {
            case ACTION_CONFIG:
                handleActionConfig();
                break;
            case ACTION_TYPEFACE:
                handleActionTypeface();
                break;
        }
    }

    private void handleActionConfig() {
        final TypefaceConfig config = TypefaceConfig.getInstance();
        if (config.isAvailable())
            setResult(true, config);
    }

    private void handleActionTypeface() {
        final TypefaceConfig config = getParam(0);
        final String nameOrAlias = getParam(1);
        final TypefaceCollection collection = config.getTypefaceCollection(nameOrAlias);
        if (collection != null)
            setResult(true, collection);
    }

    @Override
    protected void dispatchResult(Callback callback) {
        super.dispatchResult(callback);
        if (callback == null)
            return;
        switch (getAction()) {
            case ACTION_CONFIG:
                notifyActionConfig(callback);
                break;
            case ACTION_TYPEFACE:
                notifyActionTypeface(callback);
                break;
        }
    }

    private void notifyActionConfig(Callback callback) {
        if (isSuccess())
            callback.onLoadConfigSuccess(this.<TypefaceConfig>getResult(0));
        else
            callback.onLoadConfigFailure();
    }

    private void notifyActionTypeface(Callback callback) {
        if (isSuccess())
            callback.onLoadTypefaceCollectionSuccess(this.<TypefaceCollection>getResult(0));
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
