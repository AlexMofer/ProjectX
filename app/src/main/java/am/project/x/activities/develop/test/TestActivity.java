package am.project.x.activities.develop.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.expandlayout.ExpandLayout;

public class TestActivity extends BaseActivity {


    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_test;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.test_toolbar);
    }

    public void show(View view) {
        ExpandLayout expandLayout = (ExpandLayout) view.getParent();
        expandLayout.setExpand(!expandLayout.isExpand());
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }
}
