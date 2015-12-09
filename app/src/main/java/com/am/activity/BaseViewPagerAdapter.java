package com.am.activity;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class BaseViewPagerAdapter extends PagerAdapter {
    private List<View> mListViews;

    public BaseViewPagerAdapter(List<View> mListViews) {
        this.mListViews = mListViews;// 构造方法，参数是我们的页卡，这样比较方便。
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
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView(mListViews.get(position));
    }

    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView(mListViews.get(position), 0);
        return mListViews.get(position);
    }
}
