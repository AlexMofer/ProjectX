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

package am.util.retrofit;

import retrofit2.Call;

/**
 * 精简的回调
 * Created by Alex on 2018/3/14.
 */
public interface SimplifiedCallback<T> {

    /**
     * 响应
     *
     * @param call   请求
     * @param result 结果（在回调包装器允许空结果的情况下，可能为空）
     */
    void onResponse(Call<T> call, T result);

    /**
     * 错误
     *
     * @param call    请求
     * @param code    错误码
     * @param message 错误信息（可能为空）
     * @param result  结果（可能为空）
     */
    void onFailure(Call<T> call, int code, String message, T result);
}
