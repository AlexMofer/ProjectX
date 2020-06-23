/*
 * Copyright (C) 2020 AlexMofer
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
package am.util.retrofit.download;

import am.util.retrofit.CallFactory;
import retrofit2.Retrofit;

/**
 * 下载请求生成器
 * Created by Alex on 2017/9/13.
 */

public class DownloadCallFactory extends CallFactory<DownloadService> {
    private static DownloadCallFactory mInstance;

    protected DownloadCallFactory() {
        //no instance
        super(DownloadService.class);
    }

    public static DownloadCallFactory getInstance() {
        if (mInstance == null) {
            mInstance = new DownloadCallFactory();
        }
        return mInstance;
    }

    @Override
    protected void onInitializeRetrofitBuilder(Retrofit.Builder builder) {
        super.onInitializeRetrofitBuilder(builder);
        builder.baseUrl("https://www.android.com");
    }
}
