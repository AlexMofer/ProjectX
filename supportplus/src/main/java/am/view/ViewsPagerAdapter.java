package am.view;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

/**
 * ViewsPagerAdapter
 */
public class ViewsPagerAdapter extends PagerAdapter {
    private List<View> mListViews;

    @SuppressWarnings("unused")
    public ViewsPagerAdapter() {
    }

    @SuppressWarnings("unused")
    public ViewsPagerAdapter(View... views) {
        if (views == null || views.length <= 0)
            return;
        setViews(Arrays.asList(views));
    }

    @SuppressWarnings("unused")
    public ViewsPagerAdapter(List<View> views) {
        setViews(views);
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
    @SuppressWarnings("all")
    @Deprecated
    public void destroyItem(View container, int position, Object object) {
        destroyItem((ViewPager) container, position, object);
    }

    @Override
    @SuppressWarnings("all")
    @Deprecated
    public Object instantiateItem(View container, int position) {
        return instantiateItem((ViewPager) container, position);
    }

    public void setViews(List<View> views) {
        mListViews = views;
        notifyDataSetChanged();
    }
}
