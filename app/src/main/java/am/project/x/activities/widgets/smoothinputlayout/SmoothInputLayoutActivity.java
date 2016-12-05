package am.project.x.activities.widgets.smoothinputlayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.smoothinputlayout.SmoothInputLayout;

public class SmoothInputLayoutActivity extends BaseActivity implements View.OnTouchListener{

    private SmoothInputLayout lytContent;
    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_smoothinputlayout;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.smoothinputlayout_toolbar);
        lytContent = (SmoothInputLayout) findViewById(R.id.sil_lyt_content);
        findViewById(R.id.sil_v_list).setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        lytContent.closeInput(true);
        return false;
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SmoothInputLayoutActivity.class));
    }
}
