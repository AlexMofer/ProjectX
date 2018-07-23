/*
 * Copyright (C) 2018 AlexMofer
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
package am.project.x.business.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import am.project.x.utils.ViewUtils;

/**
 * 主页
 */
public class MainActivity extends BaseActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String EXTRA_TOGGLE = "toggle";
    private static final String EXTRA_FRAGMENT = "current";
    private static final String TAG_LOCAL = "fragment_local";
    private static final String TAG_RESENT = "fragment_resent";
    private static final String TAG_FAVORITE = "fragment_favorite";
    private DrawerLayout mVDrawer;
    private ViewGroup mVContent;
    private String mCurrentFragment = TAG_LOCAL;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        ViewUtils.setLayoutFullscreen(getWindow().getDecorView(), false);

        final Toolbar toolbar = findViewById(R.id.main_toolbar);
        mVDrawer = findViewById(R.id.main_drawer);
        mVContent = findViewById(R.id.main_lyt_content);
        final NavigationView navigation = findViewById(R.id.main_navigation);

        setSupportActionBar(toolbar);
        final ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, mVDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setToolbarNavigationClickListener(this);
        mVDrawer.addDrawerListener(toggle);
        toggle.syncState();
        navigation.setNavigationItemSelectedListener(this);

//        if (savedInstanceState == null) {
//            MenuItem item = navigation.getMenu().findItem(R.id.main_nav_local);
//            if (item != null) {
//                item.setChecked(true);
//                setTitle(item.getTitle());
//            }
//            setFragment(TAG_LOCAL);
//        } else {
//            mCurrentFragment = savedInstanceState.getString(EXTRA_FRAGMENT, TAG_LOCAL);
//            MenuItem item;
//            switch (mCurrentFragment) {
//                default:
//                case TAG_LOCAL:
//                    item = navigation.getMenu().findItem(R.id.main_nav_local);
//                    break;
//                case TAG_RESENT:
//                    item = navigation.getMenu().findItem(R.id.main_nav_resent);
//                    break;
//                case TAG_FAVORITE:
//                    item = navigation.getMenu().findItem(R.id.main_nav_favorite);
//                    break;
//            }
//            if (item != null) {
//                item.setChecked(true);
//                setTitle(item.getTitle());
//            }
//            setFragment(mCurrentFragment);
//        }
    }

//    private void setFragment(String tag) {
//        mCurrentFragment = tag;
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        Fragment fragment = manager.findFragmentByTag(tag);
//        if (fragment != null && fragment.isVisible())
//            return;
//        if (fragment == null) {
//            switch (tag) {
//                default:
//                case TAG_LOCAL:
//                    fragment = ManagerFragment.newInstance();
//                    break;
//                case TAG_RESENT:
//                    fragment = RecentFragment.newInstance();
//                    break;
//                case TAG_FAVORITE:
//                    fragment = FavoriteFragment.newInstance();
//                    break;
//            }
//            transaction.add(mVContent.getId(), fragment, tag);
//        }
//        List<Fragment> fragments = manager.getFragments();
//        for (Fragment f : fragments) {
//            if (f == fragment) {
//                transaction.show(f);
//                f.setMenuVisibility(true);
//                f.setUserVisibleHint(true);
//            } else {
//                f.setMenuVisibility(false);
//                f.setUserVisibleHint(false);
//                transaction.hide(f);
//            }
//        }
//        transaction.commitNowAllowingStateLoss();
//    }

    // Listener
    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.main_nav_local:
//                setTitle(item.getTitle());
//                setFragment(TAG_LOCAL);
//                break;
//            case R.id.main_nav_resent:
//                setTitle(item.getTitle());
//                setFragment(TAG_RESENT);
//                break;
//            case R.id.main_nav_favorite:
//                setTitle(item.getTitle());
//                setFragment(TAG_FAVORITE);
//                break;
//            case R.id.main_nav_cloud:
//                InstallerActivity.start(this, null);
//                break;
//            case R.id.main_nav_ftp:
//                FTPActivity.start(this);
//                break;
//            case R.id.main_nav_settings:
//                SettingsActivity.start(this);
//                break;
//        }
        mVDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mVDrawer.isDrawerOpen(GravityCompat.START)) {
            mVDrawer.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
}
