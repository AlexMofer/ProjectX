package io.github.alexmofer.projectx.business.developing;

import android.content.Context;
import android.widget.Toast;

import io.github.alexmofer.android.support.app.ApplicationData;
import io.github.alexmofer.android.support.app.ApplicationHolder;

/**
 * 测试数据
 * Created by Alex on 2024/2/28.
 */
class TestApplicationData extends ApplicationData {

    private final String mTest = "匿名类";

    private TestApplicationData() {
        //no instance
    }

    public static TestApplicationData getInstance() {
        return ApplicationHolder.getApplicationData(
                TestApplicationData.class, TestApplicationData::new);
    }

    public void toast(Context context) {
        Toast.makeText(context, mTest, Toast.LENGTH_SHORT).show();
    }
}
