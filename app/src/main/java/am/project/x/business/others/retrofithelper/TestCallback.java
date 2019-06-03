package am.project.x.business.others.retrofithelper;


import am.util.retrofit.CallbackWrapper;
import am.util.retrofit.TinyCallback;

/**
 * 请求回调
 * Created by Alex on 2019/1/29.
 */
class TestCallback extends CallbackWrapper<TestBean> {

    TestCallback(TinyCallback<TestBean> callback) {
        super(callback);
    }
}
