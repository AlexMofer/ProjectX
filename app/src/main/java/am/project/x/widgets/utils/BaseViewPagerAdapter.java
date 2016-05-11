package am.project.x.widgets.utils;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

public class BaseViewPagerAdapter extends PagerAdapter {
	private List<View> mListViews;

	public BaseViewPagerAdapter(View... views) {
		mListViews = Arrays.asList(views); 
	}
	
	public BaseViewPagerAdapter(List<View> views) {
		mListViews = views;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mListViews.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(mListViews.get(position), 0);
		return mListViews.get(position);
	}

	@Override
	public int getCount() {
		return mListViews == null ? 0 : mListViews.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(mListViews.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(mListViews.get(position), 0);
		return mListViews.get(position);
	}
}
