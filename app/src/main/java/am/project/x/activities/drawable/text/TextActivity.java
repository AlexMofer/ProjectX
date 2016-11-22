package am.project.x.activities.drawable.text;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import am.drawable.TextDrawable;
import am.project.support.compat.AMViewCompat;
import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class TextActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{

    private TextDrawable drawable;
    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_text;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.text_toolbar);
        View view = findViewById(R.id.text_v_image);
        ((Switch) findViewById(R.id.text_sh_scale)).setOnCheckedChangeListener(this);
        drawable = new TextDrawable(getApplicationContext(), R.dimen.textSize_drawable,
                R.color.colorPrimary, R.string.text_drawable);
        AMViewCompat.setBackground(view, drawable);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        drawable.setAutoScale(isChecked);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TextActivity.class));
    }
}
