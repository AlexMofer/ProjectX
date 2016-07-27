package am.project.x.activities.widgets.multiactiontextview;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.multiactiontextview.MultiActionClickableSpan;
import am.widget.multiactiontextview.MultiActionTextView;

public class MultiActionTextViewActivity extends BaseActivity implements View.OnClickListener,
        MultiActionClickableSpan.OnTextClickedListener {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_multiactiontextview;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.mat_toolbar);
        MultiActionTextView textView = (MultiActionTextView) findViewById(R.id.mat_tv_content);
        final int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        final int colorAccent = ContextCompat.getColor(this, R.color.colorAccent);
        final int colorRipple = ContextCompat.getColor(this, R.color.colorRipple);
        MultiActionClickableSpan action1 = new MultiActionClickableSpan(
                0, 7, colorPrimary, true, false, this);
        MultiActionClickableSpan action2 = new MultiActionClickableSpan(
                10, 15, colorAccent, false, true, this);
        MultiActionClickableSpan action3 = new MultiActionClickableSpan(
                134, 140, colorRipple, false, true, this);
        MultiActionClickableSpan action4 = new MultiActionClickableSpan(
                181, 189, colorRipple, false, true, this);
        MultiActionClickableSpan action5 = new MultiActionClickableSpan(
                214, 230, colorRipple, false, true, this);
        MultiActionClickableSpan action6 = new MultiActionClickableSpan(
                346, 356, colorRipple, false, true, this);
        MultiActionClickableSpan action7 = new MultiActionClickableSpan(
                382, 392, colorRipple, false, true, this);
        textView.setText(R.string.multiactiontextview_content,
                action1, action2, action3, action4, action5, action6, action7);
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, R.string.multiactiontextview_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTextClicked(View view, MultiActionClickableSpan span) {
        String text = ((TextView) view).getText().toString();
        Toast.makeText(this, text.substring(span.getStart(), span.getEnd()),
                Toast.LENGTH_SHORT).show();
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MultiActionTextViewActivity.class));
    }
}
