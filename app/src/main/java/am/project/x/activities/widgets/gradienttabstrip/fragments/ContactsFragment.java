package am.project.x.activities.widgets.gradienttabstrip.fragments;

import android.os.Bundle;

/**
 * ContactsFragment
 * Created by Alex on 2016/5/19.
 */
public class ContactsFragment extends GradientTabStripFragment {

    public static ContactsFragment newInstance(String content) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_NAME, content);
        fragment.setArguments(bundle);
        return fragment;
    }
}