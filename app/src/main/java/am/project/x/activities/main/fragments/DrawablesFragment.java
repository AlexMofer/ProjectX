package am.project.x.activities.main.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import am.project.x.R;

/**
 * 图形图像
 */
public class DrawablesFragment extends Fragment {

    public static DrawablesFragment newInstance() {
        return new DrawablesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_drawables, container, false);
    }

}
