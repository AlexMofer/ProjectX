package io.github.alexmofer.projectx.business.others.retrofithelper;

import com.am.retrofit.helper.CallFactory;

import io.github.alexmofer.projectx.BuildConfig;
import retrofit2.Call;

/**
 * 请求生成器
 * Created by Alex on 2019/1/29.
 */

public class TestCallFactory extends CallFactory<TestService> {
    private static TestCallFactory mInstance;

    private TestCallFactory() {
        super("https://www.sojson.com/open/", TestService.class);
    }

    private static TestCallFactory getInstance() {
        if (mInstance == null) {
            mInstance = new TestCallFactory();
        }
        return mInstance;
    }

    static Call<TestBean> getWeather(@SuppressWarnings("SameParameterValue") String city) {
        return getInstance().getServer().getWeather(city);
    }

    @Override
    public long getTimeout() {
        return 15000L;
    }

    @Override
    public boolean isLogging() {
        return BuildConfig.DEBUG;
    }
}
