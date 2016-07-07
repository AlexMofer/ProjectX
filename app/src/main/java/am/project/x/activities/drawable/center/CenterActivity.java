package am.project.x.activities.drawable.center;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import am.drawable.CenterDrawable;
import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class CenterActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_center;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.center_toolbar);
        ImageView ivRectangle = (ImageView) findViewById(R.id.center_iv_rectangle);
        ivRectangle.setImageDrawable(new CenterDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_drawable_center),
                ContextCompat.getColor(this, R.color.colorPrimary)));
        ImageView ivRectangleE = (ImageView) findViewById(R.id.center_iv_rectangle_e);
        ivRectangleE.setImageDrawable(new CenterDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_drawable_center),
                ContextCompat.getColor(this, R.color.colorPrimary), true));
        ImageView ivRRectangle = (ImageView) findViewById(R.id.center_iv_rounded_rectangle);
        CenterDrawable cRR = new CenterDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_drawable_center),
                ContextCompat.getColor(this, R.color.colorPrimary),
                CenterDrawable.SHAPE_ROUNDED_RECTANGLE, false);
        cRR.setCornerRadius(getResources().getDisplayMetrics().density * 10);
        ivRRectangle.setImageDrawable(cRR);
        ImageView ivRRectangleE = (ImageView) findViewById(R.id.center_iv_rounded_rectangle_e);
        CenterDrawable cRRE = new CenterDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_drawable_center),
                ContextCompat.getColor(this, R.color.colorPrimary),
                CenterDrawable.SHAPE_ROUNDED_RECTANGLE, true);
        cRRE.setCornerRadius(getResources().getDisplayMetrics().density * 10);
        ivRRectangleE.setImageDrawable(cRRE);
        ImageView ivOval = (ImageView) findViewById(R.id.center_iv_oval);
        ivOval.setImageDrawable(new CenterDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_drawable_center),
                ContextCompat.getColor(this, R.color.colorPrimary),
                CenterDrawable.SHAPE_OVAL, false));
        ImageView ivOvalE = (ImageView) findViewById(R.id.center_iv_oval_e);
        ivOvalE.setImageDrawable(new CenterDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_drawable_center),
                ContextCompat.getColor(this, R.color.colorPrimary),
                CenterDrawable.SHAPE_OVAL, true));
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, CenterActivity.class));
    }
}
