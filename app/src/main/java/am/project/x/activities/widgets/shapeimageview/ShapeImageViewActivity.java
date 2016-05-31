package am.project.x.activities.widgets.shapeimageview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class ShapeImageViewActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_shapeimageview;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.siv_toolbar);

    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ShapeImageViewActivity.class));
    }
}
