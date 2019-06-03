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

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * 请求工厂
 * Created by Alex on 2018/3/14.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class CallFactory<S> {
    private static final long DEFAULT_TIMEOUT = 60000L;
    private final OkHttpClient.Builder mClientBuilder;
    private final Retrofit.Builder mRetrofitBuilder;
    private S mService;

    public CallFactory() {
        mClientBuilder = new OkHttpClient.Builder();
        onInitializeOkHttpClientBuilder(mClientBuilder);
        mRetrofitBuilder = new Retrofit.Builder();
        onInitializeRetrofitBuilder(mRetrofitBuilder);
    }

    public CallFactory(Class<S> service) {
        this();
        createService(service);
    }

    /**
     * 初始化OkHttpClient构造器
     *
     * @param builder OkHttpClient构造器
     */
    protected void onInitializeOkHttpClientBuilder(OkHttpClient.Builder builder) {
        final long timeout = getTimeout();
        builder.writeTimeout(timeout, TimeUnit.SECONDS);
        builder.readTimeout(timeout, TimeUnit.SECONDS);
        builder.connectTimeout(timeout, TimeUnit.SECONDS);
        if (isLogging()) {
            final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            onInitializeHttpLoggingInterceptor(interceptor);
            builder.addInterceptor(interceptor);
        }
    }

    /**
     * 获取通用超时时常
     *
     * @return 通用超时时常
     */
    public long getTimeout() {
        return DEFAULT_TIMEOUT;
    }

    /**
     * 是否输出Http相关日志
     *
     * @return 是否输出（默认不输出）
     */
    public boolean isLogging() {
        return false;
    }

    /**
     * 初始化HttpLoggingInterceptor日志拦截器
     *
     * @param interceptor HttpLoggingInterceptor 日志拦截器
     */
    protected void onInitializeHttpLoggingInterceptor(HttpLoggingInterceptor interceptor) {
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    /**
     * 初始化RetrofitBuilder
     *
     * @param builder RetrofitBuilder
     */
    protected void onInitializeRetrofitBuilder(Retrofit.Builder builder) {
    }

    /**
     * 创建服务接口
     *
     * @param service 服务接口
     */
    public void createService(Class<S> service) {
        mService = mRetrofitBuilder.client(mClientBuilder.build()).build().create(service);
    }

    /**
     * 获取客户端构建器
     * 对构建器做修改后需重新调用{@link #createService(Class)}
     *
     * @return OkHttpClient.Builder
     */
    public final OkHttpClient.Builder getClientBuilder() {
        return mClientBuilder;
    }

    /**
     * 获取Retrofit构建器
     * 对构建器做修改后需重新调用{@link #createService(Class)}
     *
     * @return Retrofit.Builder
     */
    public final Retrofit.Builder getRetrofitBuilder() {
        return mRetrofitBuilder;
    }

    public final S getService() {
        return mService;
    }
}
