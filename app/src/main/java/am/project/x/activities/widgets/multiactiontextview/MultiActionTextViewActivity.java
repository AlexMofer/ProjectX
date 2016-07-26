package am.project.x.activities.widgets.multiactiontextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.multiactiontextview.MultiActionTextView;

public class MultiActionTextViewActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_multiactiontextview;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.mat_toolbar);
        MultiActionTextView textView = (MultiActionTextView) findViewById(R.id.mat_tv_content);
    }
}
