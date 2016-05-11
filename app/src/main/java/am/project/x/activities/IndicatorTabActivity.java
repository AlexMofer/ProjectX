package am.project.x.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.BaseTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import am.project.x.R;
import am.project.x.widgets.tabstrips.IndicatorTabStrip;

@SuppressWarnings("all")
public class IndicatorTabActivity extends Activity {

	private IndicatorTabStrip PagerTab;
	private ViewPager mViewPager;
	private TitleViewPagerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_indicator);
		super.onCreate(savedInstanceState);
		mViewPager = (ViewPager) findViewById(R.id.vp);
		float density = getResources().getDisplayMetrics().density;
		List<View> viewList = new ArrayList<>();
		View v1 = LayoutInflater.from(this).inflate(R.layout.item_pager, null);
		((TextView) v1.findViewById(R.id.text)).setText("昨天");
		viewList.add(v1);
		View v2 = LayoutInflater.from(this).inflate(R.layout.item_pager, null);
		((TextView) v2.findViewById(R.id.text)).setText("今天");
		viewList.add(v2);
		View v3 = LayoutInflater.from(this).inflate(R.layout.item_pager, null);
		((TextView) v3.findViewById(R.id.text)).setText("明天");
		viewList.add(v3);
		List<String> listStrings = new ArrayList<>();
		listStrings.add("昨天");
		listStrings.add("今天");
		listStrings.add("明天");
		mAdapter = new TitleViewPagerAdapter(viewList, listStrings);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(1);
		PagerTab = (IndicatorTabStrip) findViewById(R.id.tab);
        PagerTab.setItemInterval((int) (10 * density));
        PagerTab.showGradientItemBackground(true);
        PagerTab.setGradientItemBackground(0xff00ddff, 0xff0099cc);
        PagerTab.showUnderline(true);
        PagerTab.setUnderline(0xff669900, (int) (2 * density));
        PagerTab.showIndicator(true);
        PagerTab.setIndicator(0xffffbb33, (int) (48 * density), (int) (2 * density));
        PagerTab.showItemTextGradient(true);
        PagerTab.setItemTextGradient(0xff8bc34a, 0xff33691e);
        PagerTab.showItemTextScale(true);
        PagerTab.setItemTextScaleMagnification(1.25f);

        BaseTabStrip.ItemTabAdapter adapter = new BaseTabStrip.ItemTabAdapter() {

            @Override
            public boolean isTagEnable(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

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
		};
        PagerTab.setAdapter(adapter);
	}

}
