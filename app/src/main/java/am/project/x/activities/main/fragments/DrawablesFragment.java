package am.project.x.activities.main.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import am.project.x.R;
import am.project.x.activities.drawable.loading.LoadingActivity;

/**
 * 图形图像
 */
public class DrawablesFragment extends Fragment implements View.OnClickListener {

    public static DrawablesFragment newInstance() {
        return new DrawablesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_drawables, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.drawable_btn_loading).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawable_btn_loading:
                LoadingActivity.startActivity(getContext());
                break;
        }
    }
}
