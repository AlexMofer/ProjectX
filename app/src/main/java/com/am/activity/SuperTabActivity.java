package com.am.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.am.widget.R;
import com.am.widget.tabstrips.GradientPagerTabStrip;
import com.am.widget.tabstrips.TabTagAdapter;

@SuppressLint("InflateParams")
public class SuperTabActivity extends Activity {

	private GradientPagerTabStrip PagerTab;
	private ViewPager mViewPager;
	private TitleViewPagerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		mViewPager = (ViewPager) findViewById(R.id.vp);
		float density = getResources().getDisplayMetrics().density;
		List<View> viewList = new ArrayList<View>();
		View v1 = LayoutInflater.from(this).inflate(R.layout.item_pager, null);
		((TextView) v1.findViewById(R.id.text)).setText("昨天");
		viewList.add(v1);
		View v2 = LayoutInflater.from(this).inflate(R.layout.item_pager, null);
		((TextView) v2.findViewById(R.id.text)).setText("今天");
		viewList.add(v2);
		View v3 = LayoutInflater.from(this).inflate(R.layout.item_pager, null);
		((TextView) v3.findViewById(R.id.text)).setText("明天");
		viewList.add(v3);
		List<String> listStrings = new ArrayList<String>();
		listStrings.add("昨天");
		listStrings.add("今天");
		listStrings.add("明天");
		mAdapter = new TitleViewPagerAdapter(viewList, listStrings);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(1);
		PagerTab = (GradientPagerTabStrip) findViewById(R.id.tab);
		PagerTab.setTabsInterval((int) (10 * density));
		PagerTab.setTabIndicator(0xffffbb33, (int) (2 * density),
				(int) (10 * density));
		PagerTab.setUnderline(0xff669900, (int) (2 * density));
		PagerTab.setTextGradient(0xff8bc34a, 0xff33691e);
		PagerTab.showTextScale(true);
		PagerTab.setMagnification(1.25f);
		PagerTab.showTabGradient(true);
		PagerTab.setTabGradient(0xff00ddff, 0xff0099cc);
		TabTagAdapter adapter = new TabTagAdapter(this) {

			@Override
			public String getTag(int position) {
				switch (position) {
				default:
				case 0:
					return "000";
				case 1:
					return "";
				case 2:
					return "New";
				}
			}

			@Override
			public boolean isEnable(int position) {
				if (position == 0) {
					return false;
				} else {
					return true;
				}
			}

			@Override
			public float getTextSize(int position) {
				if (position == 1) {
					return 12 * getResources().getDisplayMetrics().density;
				}
				return super.getTextSize(position);
			}

			@Override
			public Drawable getBackground(int position) {
				// if (position == 1) {
				// return getResources().getDrawable(R.drawable.ic_tag_ovel);
				// }
				return super.getBackground(position);
			}

			@Override
			public TagAlign getTagAlign(int position) {
				// if (position == 1) {
				// return TagAlign.LEFTBOTTOM;
				// }
				return super.getTagAlign(position);
			}

			@Override
			public float getMarginLeft(int position) {
				return 4 * getResources().getDisplayMetrics().density;
			}

			@Override
			public float getMarginTop(int position) {
				return 4 * getResources().getDisplayMetrics().density;
			}

			@Override
			public float getMarginRight(int position) {
				return 4 * getResources().getDisplayMetrics().density;
			}

			@Override
			public float getMarginBottom(int position) {
				return 4 * getResources().getDisplayMetrics().density;
			}

			@Override
			public float getPaddingLeft(int position) {
				return 2 * getResources().getDisplayMetrics().density;
			}

			@Override
			public float getPaddingRight(int position) {
				return 2 * getResources().getDisplayMetrics().density;
			}
		};
		PagerTab.setTagAdapter(adapter);
	}

}
