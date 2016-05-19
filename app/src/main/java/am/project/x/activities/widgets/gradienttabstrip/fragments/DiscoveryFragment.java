package am.project.x.activities.widgets.gradienttabstrip.fragments;

import android.os.Bundle;

/**
 * DiscoveryFragment
 * Created by Alex on 2016/5/19.
 */
public class DiscoveryFragment extends GradientTabStripFragment {

    public static DiscoveryFragment newInstance(String content) {
        DiscoveryFragment fragment = new DiscoveryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_NAME, content);
        fragment.setArguments(bundle);
        return fragment;
    }
}