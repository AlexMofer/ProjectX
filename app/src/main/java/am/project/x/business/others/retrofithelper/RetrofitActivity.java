package am.project.x.business.others.retrofithelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import am.appcompat.app.BaseActivity;
import am.project.x.R;
import am.project.x.business.others.retrofithelper.gson.GsonHelper;
import am.util.retrofit.TinyCallback;
import am.util.retrofit.WeakCallback;

public class RetrofitActivity extends BaseActivity implements TinyCallback<TestBean> {

    private final TestCallback mCallback = new TestCallback(this);
    private EditText mVInput;
    private TextView mVOutput;

    public static void start(Context context) {
        context.startActivity(new Intent(context, RetrofitActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        setSupportActionBar(R.id.retrofit_toolbar);
        mVInput = findViewById(R.id.main_edt_input);
        mVOutput = findViewById(R.id.main_tv_output);
    }

    public void test(View view) {
        if (view.getId() == R.id.main_btn_test) {
            final InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(mVInput.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            String input = mVInput.getText().toString().trim();
            if (TextUtils.isEmpty(input))
                input = getString(R.string.retrofit_input_hint);
            TestCallFactory.getWeather(input).enqueue(new WeakCallback<>(mCallback));
        }
    }

    @Override
    public void onResponse(TestBean result) {
        if (result == null)
            mVOutput.setText(R.string.retrofit_output_null);
        else
            mVOutput.setText(GsonHelper.toJson(result));
    }

    @Override
    public void onFailure(int code, String message, TestBean result) {
        mVOutput.setText(getString(R.string.retrofit_output_error, code, message,
                (result == null ? "null" : GsonHelper.toJson(result))));
    }
}
