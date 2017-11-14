/*
 * Copyright (C) 2015 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package am.util.viewpager.adapter;

import android.content.res.Configuration;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ConfigurationHelper;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ViewsPagerAdapter
 */
@SuppressWarnings("all")
public class ViewsPagerAdapter extends PagerAdapter {
    private List<View> mListViews;

    public ViewsPagerAdapter() {
    }

    public ViewsPagerAdapter(View... views) {
        if (views == null || views.length <= 0)
            return;
        setViews(Arrays.asList(views));
    }

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
    public final int getItemPosition(Object object) {
        if (mListViews == null || mListViews.size() <= 0)
            return POSITION_NONE;
        return mListViews.contains(object) ? POSITION_UNCHANGED : POSITION_NONE;
    }

    @Override
    @Deprecated
    public void destroyItem(View container, int position, Object object) {
        destroyItem((ViewPager) container, position, object);
    }

    @Override
    @Deprecated
    public Object instantiateItem(View container, int position) {
        return instantiateItem((ViewPager) container, position);
    }

    public void setViews(List<View> views) {
        mListViews = views;
        notifyDataSetChanged();
    }

    public void addView(View view) {
        if (mListViews == null)
            mListViews = new ArrayList<>();
        mListViews.add(view);
        notifyDataSetChanged();
    }

    public void addView(int position, View view) {
        if (mListViews == null)
            mListViews = new ArrayList<>();
        mListViews.add(position, view);
        notifyDataSetChanged();
    }

    public void removeView(View view) {
        if (mListViews == null || mListViews.size() <= 0)
            return;
        if (mListViews.remove(view)) {
            notifyDataSetChanged();
        }
    }

    public void removeView(int position) {
        if (mListViews == null || mListViews.size() <= 0)
            return;
        if (position >= 0 && position < mListViews.size()) {
            mListViews.remove(position);
            notifyDataSetChanged();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (mListViews == null || mListViews.isEmpty())
            return;
        for (View view : mListViews) {
            if (view == null)
                continue;
            ConfigurationHelper.onConfigurationChanged(view, newConfig);
        }
    }
}
