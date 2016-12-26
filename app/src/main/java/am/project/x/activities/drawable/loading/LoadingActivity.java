package am.project.x.activities.drawable.loading;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import am.drawable.CirclingDrawable;
import am.drawable.DoubleCircleDrawable;
import am.drawable.MaterialProgressDrawable;
import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class LoadingActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_loading;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.loading_toolbar);
        setDoubleCircleDrawable();
        setCirclingDrawable();
        setMaterialProgressDrawable();
    }

    private void setDoubleCircleDrawable() {
        ImageView loading = (ImageView) findViewById(R.id.loading_iv_01);
        DoubleCircleDrawable drawable = new DoubleCircleDrawable(getResources().getDisplayMetrics().density);
        loading.setImageDrawable(drawable);
        drawable.start();
    }

    private void setCirclingDrawable() {
        ImageView loading = (ImageView) findViewById(R.id.loading_iv_02);
        final int stroke = (int) (4 * getResources().getDisplayMetrics().density);
        CirclingDrawable drawable = new CirclingDrawable(stroke,
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getDrawable(this, R.drawable.ic_drawable_default));
        loading.setImageDrawable(drawable);
        drawable.start();
    }

    private void setMaterialProgressDrawable() {
        ImageView loading = (ImageView) findViewById(R.id.loading_iv_03);
        MaterialProgressDrawable drawable = new MaterialProgressDrawable(
                getResources().getDisplayMetrics().density, MaterialProgressDrawable.DEFAULT,
                0x00000000, 255, 0xff33b5e5, 0xff99cc00, 0xffff4444, 0xffffbb33);
        loading.setImageDrawable(drawable);
        drawable.start();
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, LoadingActivity.class));
    }
}
