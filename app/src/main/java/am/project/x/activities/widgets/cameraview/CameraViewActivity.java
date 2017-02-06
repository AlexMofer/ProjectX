package am.project.x.activities.widgets.cameraview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class CameraViewActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_cameraview;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.camera_toolbar);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, CameraViewActivity.class);
        context.startActivity(starter);
    }
}
