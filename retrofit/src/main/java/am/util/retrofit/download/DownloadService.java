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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 下载
 * Created by Alex on 2018/12/3.
 */
interface DownloadService {

    /**
     * 下载文件
     *
     * @param fileUrl 文件链接
     * @return 文件
     */
    @GET
    Call<ResponseBody> download(@Url String fileUrl);

    /**
     * 流式下载文件
     *
     * @param fileUrl 文件链接
     * @return 文件
     */
    @Streaming
    @GET
    Call<ResponseBody> downloadStreaming(@Url String fileUrl);
}
