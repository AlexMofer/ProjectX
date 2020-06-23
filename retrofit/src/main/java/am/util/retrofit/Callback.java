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
package am.util.retrofit;

/**
 * 回调
 * Created by Alex on 2020/6/23.
 */
public interface Callback<T> {

    int ERROR_CODE_THROWABLE = -1;
    int ERROR_CODE_EMPTY = -2;

    /**
     * 响应
     *
     * @param result 结果（在回调包装器允许空结果的情况下，可能为空）
     */
    void onResponse(T result);

    /**
     * 错误
     *
     * @param code    错误码
     * @param message 错误信息（可能为空）
     */
    void onFailure(int code, String message);
}
