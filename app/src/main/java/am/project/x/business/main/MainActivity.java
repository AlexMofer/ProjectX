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
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.am.appcompat.app.AppCompatActivity;
import com.google.android.material.navigation.NavigationView;

import am.project.x.BuildConfig;
import am.project.x.R;
import am.project.x.business.about.AboutActivity;
import am.project.x.business.developing.DevelopingActivity;
import am.project.x.business.main.fragments.DevelopFragment;
import am.project.x.business.main.fragments.DrawablesFragment;
import am.project.x.business.main.fragments.OthersFragment;
import am.project.x.business.main.fragments.WidgetsFragment;
import am.project.x.utils.ContextUtils;
import am.project.x.utils.ViewUtils;

/**
 * 主页
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String EXTRA_FRAGMENT = "current";
    private static final String TAG_WIDGETS = "fragment_widgets";
    private static final String TAG_DRAWABLES = "fragment_drawables";
    private static final String TAG_OTHERS = "fragment_others";
    private static final String TAG_DEVELOP = "fragment_develop";
    private static final String REPORT = "https://github.com/AlexMofer/ProjectX/issues";
    private static final String EMAIL = "moferalex@gmail.com";
    private DrawerLayout mVDrawer;
    private ViewGroup mVContent;
    private String mCurrent;

    public MainActivity() {
        super(R.layout.activity_main);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        final String tag;
        final boolean debug = BuildConfig.DEBUG;
        if (savedInstanceState == null) {
            tag = TAG_WIDGETS;
            final MenuItem item = navigation.getMenu().findItem(R.id.main_nav_widgets);
            item.setChecked(true);
            setTitle(item.getTitle());
            if (debug)
                DevelopingActivity.start(this);
        } else {
            tag = savedInstanceState.getString(EXTRA_FRAGMENT, TAG_WIDGETS);
            MenuItem item;
            switch (tag) {
                default:
                case TAG_WIDGETS:
                    item = navigation.getMenu().findItem(R.id.main_nav_widgets);
                    break;
                case TAG_DRAWABLES:
                    item = navigation.getMenu().findItem(R.id.main_nav_drawables);
                    break;
                case TAG_OTHERS:
                    item = navigation.getMenu().findItem(R.id.main_nav_others);
                    break;
                case TAG_DEVELOP:
                    item = navigation.getMenu().findItem(R.id.main_nav_develop);
                    break;
            }
            item.setChecked(true);
            setTitle(item.getTitle());
        }
        final MenuItem develop = navigation.getMenu().findItem(R.id.main_nav_develop);
        develop.setVisible(debug);
        setFragment(tag);
    }

    private void setFragment(String tag) {
        final String oldTag = mCurrent;
        mCurrent = tag;
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        if (!TextUtils.isEmpty(oldTag)) {
            final Fragment old = manager.findFragmentByTag(oldTag);
            if (old != null && old.isVisible()) {
                transaction.hide(old);
            }
        }
        Fragment target = manager.findFragmentByTag(tag);
        if (target != null && target.isVisible())
            return;
        if (target == null) {
            switch (tag) {
                default:
                case TAG_WIDGETS:
                    target = WidgetsFragment.newInstance();
                    break;
                case TAG_DRAWABLES:
                    target = DrawablesFragment.newInstance();
                    break;
                case TAG_OTHERS:
                    target = OthersFragment.newInstance();
                    break;
                case TAG_DEVELOP:
                    target = DevelopFragment.newInstance();
                    break;
            }
            transaction.add(mVContent.getId(), target, tag);
        }
        transaction.show(target);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mVDrawer.isDrawerOpen(GravityCompat.START)) {
            mVDrawer.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_FRAGMENT, mCurrent);
    }

    // Listener
    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_nav_widgets:
                setTitle(item.getTitle());
                setFragment(TAG_WIDGETS);
                break;
            case R.id.main_nav_drawables:
                setTitle(item.getTitle());
                setFragment(TAG_DRAWABLES);
                break;
            case R.id.main_nav_others:
                setTitle(item.getTitle());
                setFragment(TAG_OTHERS);
                break;
            case R.id.main_nav_develop:
                setTitle(item.getTitle());
                setFragment(TAG_DEVELOP);
                break;
            case R.id.main_nav_report:
                ContextUtils.openBrowser(this, REPORT);
                break;
            case R.id.main_nav_contact:
                ContextUtils.sendEmail(this, null, null, EMAIL);
                break;
            case R.id.main_nav_about:
                AboutActivity.start(this);
                break;
        }
        mVDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
