package am.project.x.activities.old;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.widgets.drawables.DoubleCircleDrawable;
import am.widget.stateframelayout.StateFrameLayout;


/**
 * 状态帧布局
 * 
 * @author Mofer
 * 
 */
public class StateFrameActivity extends FragmentActivity {



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.old_activity_stateframe);
		ViewPager vp = (ViewPager) findViewById(R.id.sf_vp_pager);
		vp.setAdapter(new StateFrameAdapter(getSupportFragmentManager()));
	}



	class StateFrameAdapter extends FragmentPagerAdapter {

		private SimpleStateFrameFragment mSimple;
		private CustomStateFrameFragment mCustom;
		public StateFrameAdapter(FragmentManager fm) {
			super(fm);
			mSimple = new SimpleStateFrameFragment();
			mCustom = new CustomStateFrameFragment();
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				default:
				case 0:
					return mSimple;
				case 1:
					return mCustom;

			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	}

	public static class SimpleStateFrameFragment extends BasePagerFragment implements OnClickListener, StateFrameLayout.OnStateClickListener {
		private SimpleStateFrameFragment me = this;
		private StateFrameLayout mStateFrameLayout;
		@Override
		protected int setContentLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return R.layout.old_fragment_stateframe_simple;
		}

		@Override
		protected void initResource() {
			findViewById(R.id.btn_connect).setOnClickListener(me);
			findViewById(R.id.btn_failure).setOnClickListener(me);
			findViewById(R.id.btn_empty).setOnClickListener(me);
			findViewById(R.id.btn_success).setOnClickListener(me);
            ((TextView)findViewById(R.id.tv_stateframe)).setText("Drawable StateFrame");
			mStateFrameLayout = (StateFrameLayout) findViewById(R.id.sfl_state);
			mStateFrameLayout.setStateDrawables(new DoubleCircleDrawable(
					getResources().getDisplayMetrics().density), ContextCompat
					.getDrawable(getContext(), R.drawable.old_unconnect), ContextCompat
					.getDrawable(getContext(), R.drawable.old_connected));
			mStateFrameLayout.setOnStateClickListener(me);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_connect:
					mStateFrameLayout.setState(StateFrameLayout.STATE_LOADING);
					break;
				case R.id.btn_failure:
					mStateFrameLayout.setState(StateFrameLayout.STATE_ERROR);
					break;
				case R.id.btn_empty:
					mStateFrameLayout.setState(StateFrameLayout.STATE_EMPTY);
					break;
				case R.id.btn_success:
					mStateFrameLayout.setState(StateFrameLayout.STATE_NORMAL);
					break;
				default:
					break;
			}
		}

		@Override
		public void onError(StateFrameLayout layout) {
			mStateFrameLayout.setState(StateFrameLayout.STATE_LOADING);
		}
	}

	public static class CustomStateFrameFragment extends BasePagerFragment implements OnClickListener, StateFrameLayout.OnStateClickListener {
		private CustomStateFrameFragment me = this;
		private StateFrameLayout mStateFrameLayout;
		@Override
		protected int setContentLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return R.layout.old_fragment_stateframe_simple;
		}

		@Override
		protected void initResource() {
			findViewById(R.id.btn_connect).setOnClickListener(me);
			findViewById(R.id.btn_failure).setOnClickListener(me);
			findViewById(R.id.btn_empty).setOnClickListener(me);
			findViewById(R.id.btn_success).setOnClickListener(me);
            ((TextView)findViewById(R.id.tv_stateframe)).setText("CustomView StateFrame");
			mStateFrameLayout = (StateFrameLayout) findViewById(R.id.sfl_state);
			ImageView ivLoading = new ImageView(getContext());
            DoubleCircleDrawable circleDrawable = new DoubleCircleDrawable(
                    getResources().getDisplayMetrics().density);
			ivLoading.setImageDrawable(circleDrawable);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
			mStateFrameLayout.setLoadingView(ivLoading, lp);
            circleDrawable.start();

            TextView error = new TextView(getContext());
            error.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
            error.setText("出错了！！！戳一下再来");
            error.setGravity(Gravity.CENTER);
            mStateFrameLayout.setErrorView(error, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            TextView empty = new TextView(getContext());
            empty.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
            empty.setText("木有数据");
            empty.setGravity(Gravity.CENTER);
            empty.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"要干嘛",Toast.LENGTH_SHORT).show();
                }
            });
            mStateFrameLayout.setEmptyView(empty, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			mStateFrameLayout.setOnStateClickListener(me);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_connect:
					mStateFrameLayout.setState(StateFrameLayout.STATE_LOADING);
					break;
				case R.id.btn_failure:
					mStateFrameLayout.setState(StateFrameLayout.STATE_ERROR);
					break;
				case R.id.btn_empty:
					mStateFrameLayout.setState(StateFrameLayout.STATE_EMPTY);
					break;
				case R.id.btn_success:
					mStateFrameLayout.setState(StateFrameLayout.STATE_NORMAL);
					break;
				default:
					break;
			}
		}

		@Override
		public void onError(StateFrameLayout layout) {
			mStateFrameLayout.setState(StateFrameLayout.STATE_LOADING);
		}
	}

}
