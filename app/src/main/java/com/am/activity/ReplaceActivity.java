package com.am.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.am.widget.R;
import com.am.widget.replacelayout.ReplaceLayout;
import com.am.widget.replacelayout.ReplaceLayout.ReplaceAdapter;
import com.am.widget.tabstrips.GradientTabStrip;

@SuppressWarnings("all")
public class ReplaceActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace);
        ViewPager vp = (ViewPager) findViewById(R.id.vp_replace);
        final ReplaceLayout rts = (ReplaceLayout) findViewById(R.id.rl_replace);
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), getApplicationContext(), rts);
        rts.setAdapter(adapter);
        vp.setAdapter(adapter);

        GradientTabStrip tab = (GradientTabStrip) findViewById(R.id.tab_wechat);
        tab.bindViewPager(vp);
        tab.setItemBackground(R.drawable.bg_tab);
        GradientTabStrip.SimpleGradientTabAdapter sgtadapter =
                new GradientTabStrip.SimpleGradientTabAdapter() {

            @Override
            public Drawable getSelectedDrawable(int position, Context context) {
                switch (position) {
                    default:
                    case 0:
                        return ContextCompat.getDrawable(context, R.drawable.ain);
                    case 1:
                        return ContextCompat.getDrawable(context, R.drawable.ail);
                    case 2:
                        return ContextCompat.getDrawable(context, R.drawable.aip);
                    case 3:
                        return ContextCompat.getDrawable(context, R.drawable.air);
                }
            }

            @Override
            public Drawable getNormalDrawable(int position, Context context) {
                switch (position) {
                    default:
                    case 0:
                        return ContextCompat.getDrawable(context, R.drawable.aio);
                    case 1:
                        return ContextCompat.getDrawable(context, R.drawable.aim);
                    case 2:
                        return ContextCompat.getDrawable(context, R.drawable.aiq);
                    case 3:
                        return ContextCompat.getDrawable(context, R.drawable.ais);
                }
            }

            @Override
            public boolean isTagEnable(int position) {
                if (position != 3) {
                    return true;
                }
                return false;
            }

            @Override
            public String getTag(int position) {
                if (position == 0) {
                    return "999";
                } else if (position == 1) {
                    return "2";
                } else {
                    return null;
                }
            }
        };
        tab.setAdapter(sgtadapter);
        tab.addOnChangeListener(rts);

        // 不要使用 ViewPager 的 setCurrentItem 来跳转
    }


    public static class BaseFragment extends Fragment {

        protected TextView tvContent;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_blank, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            tvContent = (TextView) getView().findViewById(R.id.fragment_tv_info);
        }
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter implements ReplaceAdapter {

        private BaseFragment mF1, mF2, mF3, mF4;
        private View mV1, mV2, mV3, mV4;
        private LayoutParams mLayoutParams;

        public MyFragmentPagerAdapter(FragmentManager fm, Context context, ViewGroup parent) {
            super(fm);
            mF1 = new BaseFragment() {
                @Override
                public void onActivityCreated(Bundle savedInstanceState) {
                    super.onActivityCreated(savedInstanceState);
                    tvContent.setText("微信");
                }
            };
            mF2 = new BaseFragment() {
                @Override
                public void onActivityCreated(Bundle savedInstanceState) {
                    super.onActivityCreated(savedInstanceState);
                    tvContent.setText("通讯录");
                }
            };
            mF3 = new BaseFragment() {
                @Override
                public void onActivityCreated(Bundle savedInstanceState) {
                    super.onActivityCreated(savedInstanceState);
                    tvContent.setText("发现");
                }
            };
            mF4 = new BaseFragment() {
                @Override
                public void onActivityCreated(Bundle savedInstanceState) {
                    super.onActivityCreated(savedInstanceState);
                    tvContent.setText("我");
                }
            };
            mV1 = LayoutInflater.from(context).inflate(R.layout.item_title_replace, parent, false);
            TextView tv1 = (TextView) mV1.findViewById(R.id.item_tv_title);
            tv1.setText("Title1");
            tv1.setBackgroundColor(0xff00ff00);
            tv1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Title1", Toast.LENGTH_SHORT).show();
                }
            });
            mV2 = LayoutInflater.from(context).inflate(R.layout.item_title_replace, parent, false);
            TextView tv2 = (TextView) mV2.findViewById(R.id.item_tv_title);
            tv2.setText("Title2");
            tv2.setBackgroundColor(0xffff0000);
            tv2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Title2", Toast.LENGTH_SHORT).show();
                }
            });
            mV3 = LayoutInflater.from(context).inflate(R.layout.item_title_replace, parent, false);
            TextView tv3 = (TextView) mV3.findViewById(R.id.item_tv_title);
            tv3.setText("Title3");
            tv3.setBackgroundColor(0xff0000ff);
            tv3.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Title3", Toast.LENGTH_SHORT).show();
                }
            });
            mV4 = LayoutInflater.from(context).inflate(R.layout.item_title_replace, parent, false);
            TextView tv4 = (TextView) mV4.findViewById(R.id.item_tv_title);
            tv4.setText("Title4");
            tv4.setBackgroundColor(0xffff00ff);
            tv4.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Title4", Toast.LENGTH_SHORT).show();
                }
            });
            mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        @Override
        public BaseFragment getItem(int position) {
            switch (position) {
                default:
                case 0:
                    return mF1;
                case 1:
                    return mF2;
                case 2:
                    return mF3;
                case 3:
                    return mF4;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                default:
                case 0:
                    return "微信";
                case 1:
                    return "通讯录";
                case 2:
                    return "发现";
                case 3:
                    return "我";
            }
        }

        @Override
        public LayoutParams getLayoutParams(int position) {
            return mLayoutParams;
        }

        @Override
        public View getReplaceView(int position) {
            switch (position) {
                default:
                case 0:
                    return mV1;
                case 1:
                    return mV2;
                case 2:
                    return mV3;
                case 3:
                    return mV4;
            }
        }

        @TargetApi(11)
        @Override
        public void onAnimation(ViewGroup replace, int correct, int next, float offset) {
            View correctView = getReplaceView(correct);
            View nextView = getReplaceView(next);
            correctView.setAlpha(offset);
            nextView.setAlpha(1F - offset);
        }

        @TargetApi(11)
        @Override
        public void onSelected(ViewGroup replace, int position) {
            View child = getReplaceView(position);
            child.setAlpha(1);
        }
    }
}
