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
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 请求工厂
 * Created by Alex on 2018/3/14.
 */
public class CallFactory<S> {
    private static final long DEFAULT_TIMEOUT = 60000L;
    private final OkHttpClient.Builder mClientBuilder = new OkHttpClient.Builder();
    private final Retrofit.Builder mRetrofitBuilder = new Retrofit.Builder();
    private String mBaseUrl;
    private S mServer;

    public CallFactory() {
        onInitializeOkHttpClientBuilder(mClientBuilder);
        onInitializeRetrofitBuilder(mRetrofitBuilder);
        mRetrofitBuilder.addConverterFactory(onCreateConverterFactory(mRetrofitBuilder));
    }

    public CallFactory(String baseUrl) {
        setBaseUrl(baseUrl);
        mRetrofitBuilder.addConverterFactory(onCreateConverterFactory(mRetrofitBuilder));
    }

    public CallFactory(Class<S> server) {
        this();
        createServer(server);
    }

    public CallFactory(String baseUrl, Class<S> server) {
        this(baseUrl);
        createServer(server);
    }

    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
        onInitializeOkHttpClientBuilder(mClientBuilder);
        onInitializeRetrofitBuilder(mRetrofitBuilder);
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
        if (mBaseUrl != null) {
            builder.baseUrl(mBaseUrl);
        }
    }

    /**
     * 创建转换工厂
     *
     * @param builder RetrofitBuilder
     * @return 转换工厂
     */
    protected Converter.Factory onCreateConverterFactory(Retrofit.Builder builder) {
        return GsonConverterFactory.create();
    }

    /**
     * 创建服务接口
     *
     * @param server 服务接口
     */
    public void createServer(Class<S> server) {
        mServer = mRetrofitBuilder.client(mClientBuilder.build()).build().create(server);
    }

    /**
     * 获取客户端构建器
     * 对构建器做修改后需重新调用{@link #createServer(Class)}
     *
     * @return OkHttpClient.Builder
     */
    public final OkHttpClient.Builder getClientBuilder() {
        return mClientBuilder;
    }

    /**
     * 获取Retrofit构建器
     * 对构建器做修改后需重新调用{@link #createServer(Class)}
     *
     * @return Retrofit.Builder
     */
    public final Retrofit.Builder getRetrofitBuilder() {
        return mRetrofitBuilder;
    }

    /**
     * 获取服务接口
     *
     * @return 服务接口
     */
    public final S getServer() {
        return mServer;
    }
}
