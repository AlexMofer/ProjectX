package am.project.x.activities.widgets.gradienttabstrip.fragments;

import android.os.Bundle;

/**
 * WechatFragment
 * Created by Alex on 2016/5/19.
 */
public class WechatFragment extends GradientTabStripFragment {

    public static WechatFragment newInstance(String content) {
        WechatFragment fragment = new WechatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_NAME, content);
        fragment.setArguments(bundle);
        return fragment;
    }
}
