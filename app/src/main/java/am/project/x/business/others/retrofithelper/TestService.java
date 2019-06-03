
package am.project.x.business.others.retrofithelper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * 测试接口
 * Created by Alex on 2019/1/29.
 */
interface TestService {

    /**
     * 获取天气
     *
     * @param city 城市
     * @return 请求
     */
    @Headers("User-Agent: Retrofit-Sample-App")
    @GET("api/weather/json.shtml")
    Call<TestBean> getWeather(@Query("city") String city);
}
