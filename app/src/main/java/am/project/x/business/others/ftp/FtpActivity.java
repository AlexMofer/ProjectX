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
package am.project.x.business.others.ftp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.am.appcompat.app.AppCompatActivity;

import java.util.List;

import am.project.x.R;
import am.project.x.business.others.ftp.advanced.AdvancedFragment;
import am.project.x.business.others.ftp.legacy.LegacyFragment;

/**
 * FTP 文件传输
 */
public class FtpActivity extends AppCompatActivity implements FtpFragmentCallback {

    private static final String TAG_ADVANCED = "advanced";
    private static final String TAG_LEGACY = "legacy";
    private ViewGroup mVContent;

    public FtpActivity() {
        super(R.layout.activity_ftp);
    }

    public static Intent getStarter(Context context) {
        return new Intent(context, FtpActivity.class);
    }

    public static void start(Context context) {
        context.startActivity(getStarter(context));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.ftp_toolbar);
        mVContent = findViewById(R.id.ftp_content);
        setFragment(false);
    }

    private void setFragment(boolean legacy) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = manager.findFragmentByTag(TAG_LEGACY);
            if (fragment != null && fragment.isVisible())
                return;
            if (fragment == null) {
                fragment = LegacyFragment.newInstance();
                transaction.add(mVContent.getId(), fragment, TAG_LEGACY);
            }
            transaction.commitNowAllowingStateLoss();
            return;
        }
        final String tag = legacy ? TAG_LEGACY : TAG_ADVANCED;
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment != null && fragment.isVisible())
            return;
        if (fragment == null) {
            switch (tag) {
                default:
                case TAG_ADVANCED:
                    fragment = AdvancedFragment.newInstance();
                    break;
                case TAG_LEGACY:
                    fragment = LegacyFragment.newInstance();
                    break;
            }
            transaction.add(mVContent.getId(), fragment, tag);
        }
        List<Fragment> fragments = manager.getFragments();
        for (Fragment f : fragments) {
            if (f == fragment) {
                f.setMenuVisibility(true);
                transaction.show(f);
            } else {
                f.setMenuVisibility(false);
                transaction.hide(f);
            }
        }
        transaction.commitNowAllowingStateLoss();
    }

    // Callback
    @Override
    public void onSwitch(boolean legacy) {
        setFragment(legacy);
    }
}
