package am.project.x.activities.widgets.gradienttabstrip.fragments;

import android.os.Bundle;

/**
 * AccountFragment
 * Created by Alex on 2016/5/19.
 */
public class AccountFragment extends GradientTabStripFragment {

    public static AccountFragment newInstance(String content) {
        AccountFragment fragment = new AccountFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_NAME, content);
        fragment.setArguments(bundle);
        return fragment;
    }
}