package am.project.x.activities.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import am.project.x.R;
import am.project.x.activities.develop.test.TestActivity;
import am.project.x.activities.develop.supergridview.SuperGridViewActivity;

/**
 * 开发中
 */
public class DevelopFragment extends Fragment implements View.OnClickListener{

    public static DevelopFragment newInstance() {
        return new DevelopFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_develop, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.develop_btn_test).setOnClickListener(this);
        view.findViewById(R.id.develop_btn_supergridview).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.develop_btn_test:
                TestActivity.startActivity(getContext());
                break;
            case R.id.develop_btn_supergridview:
                SuperGridViewActivity.startActivity(getContext());
                break;
        }
    }
}
