package am.project.x.activities;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class TitleViewPagerAdapter extends PagerAdapter {

	private List<View> mListViews;
	private List<String> Titles;

	public TitleViewPagerAdapter(List<View> mListViews, List<String> Titles) {
		this.mListViews = mListViews;// 构造方法，参数是我们的页卡，这样比较方便。
		this.Titles = Titles;// 存放Title的数组
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mListViews.get(position));// 删除页卡
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
		container.addView(mListViews.get(position), 0);// 添加页卡
		return mListViews.get(position);
	}

	@Override
	public int getCount() {
		return mListViews == null ? 0 : mListViews.size();// 返回页卡的数量
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;// 官方提示这样写
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return Titles.get(position);// 获取PageTitle
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
