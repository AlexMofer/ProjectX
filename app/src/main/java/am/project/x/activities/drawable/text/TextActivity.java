package am.project.x.activities.drawable.text;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import am.drawable.TextDrawable;
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
        ImageView ivImage = (ImageView) findViewById(R.id.text_iv_image);
        ((Switch) findViewById(R.id.text_sh_scale)).setOnCheckedChangeListener(this);
        final int textSize = getResources().getDimensionPixelOffset(R.dimen.textSize_drawable);
        final int color = ContextCompat.getColor(this, R.color.colorPrimary);
        final String text = getString(R.string.text_drawable);
        drawable = new TextDrawable(getApplicationContext(), textSize, color, text);
        drawable.setAutoScale(false);
        ivImage.setImageDrawable(drawable);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        drawable.setAutoScale(isChecked);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TextActivity.class));
    }
}
