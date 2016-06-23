package am.project.x.activities.main.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import am.project.x.R;
import am.project.x.activities.util.printer.PrinterActivity;
import am.project.x.activities.util.security.CipherActivity;
import am.project.x.activities.widgets.gradienttabstrip.GradientTabStripActivity;

/**
 * 其他
 */
public class OthersFragment extends Fragment implements View.OnClickListener {

    public static OthersFragment newInstance() {
        return new OthersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_others, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.other_btn_printer).setOnClickListener(this);
        view.findViewById(R.id.other_btn_security).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.other_btn_printer:
                PrinterActivity.startActivity(getContext());
                break;
            case R.id.other_btn_security:
                CipherActivity.startActivity(getContext());
                break;
        }
    }
}
