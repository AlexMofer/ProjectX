package am.project.x.activities.widgets.stateframelayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import am.drawable.MaterialProgressDrawable;
import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.MaterialProgressCircleImageView;
import am.widget.stateframelayout.StateFrameLayout;

public class StateFrameLayoutActivity extends BaseActivity
        implements RadioGroup.OnCheckedChangeListener, StateFrameLayout.OnAllStateClickListener {

    private StateFrameLayout lytState;
    private MaterialProgressDrawable mLoadingDrawable;
    private MaterialProgressCircleImageView mLoadingView;
    private Drawable mErrorDrawable;
    private View mErrorView;
    private Drawable mEmptyDrawable;
    private View mEmptyView;

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_stateframelayout;
    }

    @Override
    @SuppressWarnings("all")
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.sfl_toolbar);
        lytState = (StateFrameLayout) findViewById(R.id.sfl_lyt_state);
        RadioGroup rgpState = (RadioGroup) findViewById(R.id.sfl_rgp_state);
        RadioGroup rgpMode = (RadioGroup) findViewById(R.id.sfl_rgp_mode);
        mLoadingDrawable = new MaterialProgressDrawable(
                getResources().getDisplayMetrics().density, MaterialProgressDrawable.LARGE,
                0x00000000, 255, 0xff33b5e5, 0xff99cc00, 0xffff4444, 0xffffbb33);
        mLoadingView = new MaterialProgressCircleImageView(getApplicationContext());
        mErrorDrawable = ContextCompat.getDrawable(this, R.drawable.ic_stateframelayout_error);
        TextView tvError = new TextView(getApplicationContext());
        tvError.setText(R.string.stateframelayout_change_state_error);
        tvError.setTextColor(0xffff4081);
        tvError.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 64);
        tvError.setClickable(true);
        tvError.setBackgroundResource(R.drawable.bg_common_press);
        mErrorView = tvError;
        mEmptyDrawable = ContextCompat.getDrawable(this, R.drawable.ic_stateframelayout_empty);
        TextView tvEmpty = new TextView(getApplicationContext());
        tvEmpty.setText(R.string.stateframelayout_change_state_empty);
        tvEmpty.setTextColor(0xffffe640);
        tvEmpty.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 64);
        tvEmpty.setClickable(true);
        tvEmpty.setBackgroundResource(R.drawable.bg_common_press);
        mEmptyView = tvEmpty;
        lytState.setOnStateClickListener(this);
        rgpState.setOnCheckedChangeListener(this);
        rgpMode.setOnCheckedChangeListener(this);
        rgpState.check(R.id.sfl_rbt_normal);
        rgpMode.check(R.id.sfl_rbt_drawable);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.sfl_rbt_normal:
                lytState.normal();
                break;
            case R.id.sfl_rbt_loading:
                lytState.loading();
                mLoadingView.start();
                break;
            case R.id.sfl_rbt_error:
                lytState.error();
                break;
            case R.id.sfl_rbt_empty:
                lytState.empty();
                break;
            case R.id.sfl_rbt_drawable:
                mLoadingView.stop();
                lytState.setStateDrawables(mLoadingDrawable, mErrorDrawable, mEmptyDrawable);
                break;
            case R.id.sfl_rbt_view:
                lytState.setStateViews(mLoadingView, mErrorView, mEmptyView);
                mLoadingView.start();
                break;
        }
    }

    @Override
    public void onNormalClick(StateFrameLayout layout) {
        Toast.makeText(getApplicationContext(), R.string.stateframelayout_change_state_normal,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadingClick(StateFrameLayout layout) {
        Toast.makeText(getApplicationContext(), R.string.stateframelayout_change_state_loading,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyClick(StateFrameLayout layout) {
        Toast.makeText(getApplicationContext(), R.string.stateframelayout_change_state_empty,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorClick(StateFrameLayout layout) {
        Toast.makeText(getApplicationContext(), R.string.stateframelayout_change_state_error,
                Toast.LENGTH_SHORT).show();
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, StateFrameLayoutActivity.class));
    }
}
