package am.project.x.activities.develop.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import am.project.x.R;
import am.project.x.widgets.display.DisplayRecyclerView;
import am.util.mvp.AMAppCompatActivity;

public class TestActivity extends AMAppCompatActivity {

    private final DisplayAdapter mAdapter = new DisplayAdapter();
    private DisplayRecyclerView mVContent;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_test;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.test_toolbar);

        mVContent = findViewById(R.id.display_rv_content);


        mVContent.setScale(1.5f);
//        mVContent.setDisplayLocation(1, -20, 0);
        mVContent.setDisplayLocation(1, 0, 0.0001f, false, 600 + 20 * 40, 900 + 20 * 40, 600 + 2 * 40, 900 + 2 * 40);

        mVContent.setAdapter(mAdapter);
    }
}
