package am.project.x.activities.main.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import am.project.x.R;

/**
 * 开发中
 */
public class DevelopFragment extends Fragment {

    public static DevelopFragment newInstance() {
        return new DevelopFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_develop, container, false);
    }
}
