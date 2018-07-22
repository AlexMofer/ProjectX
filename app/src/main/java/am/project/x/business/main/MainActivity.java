package am.project.x.business.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import am.project.x.R;
import am.project.x.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {

    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
}
