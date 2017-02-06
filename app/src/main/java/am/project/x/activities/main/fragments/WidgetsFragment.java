package am.project.x.activities.main.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import am.project.x.R;
import am.project.x.activities.widgets.cameraview.CameraViewActivity;
import am.project.x.activities.widgets.circleprogressbar.CircleProgressBarActivity;
import am.project.x.activities.widgets.headerfootergridview.HeaderFooterGridViewActivity;
import am.project.x.activities.widgets.multiactiontextview.MultiActionTextViewActivity;
import am.project.x.activities.widgets.recyclepager.RecyclePagerActivity;
import am.project.x.activities.widgets.replacelayout.ReplaceLayoutActivity;
import am.project.x.activities.widgets.drawableratingbar.DrawableRatingBarActivity;
import am.project.x.activities.widgets.gradienttabstrip.GradientTabStripActivity;
import am.project.x.activities.widgets.indicatortabstrip.IndicatorTabStripActivity;
import am.project.x.activities.widgets.selectionview.SelectionViewActivity;
import am.project.x.activities.widgets.shapeimageview.ShapeImageViewActivity;
import am.project.x.activities.widgets.smoothinputlayout.SmoothInputLayoutActivity;
import am.project.x.activities.widgets.stateframelayout.StateFrameLayoutActivity;
import am.project.x.activities.widgets.tagtabstrip.TagTabStripActivity;
import am.project.x.activities.widgets.wraplayout.WrapLayoutActivity;
import am.project.x.activities.widgets.zxingscanview.ZxingScanViewActivity;

/**
 * 控件
 */
public class WidgetsFragment extends Fragment implements View.OnClickListener{

    public static WidgetsFragment newInstance() {
        return new WidgetsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_widgets, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.widget_btn_gradienttabstrip).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_tagtabstrip).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_indicatortabstrip).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_shapeimageview).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_stateframelayout).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_wraplayout).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_replacelayout).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_drawableratingbar).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_headerfootergridview).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_multiactiontextview).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_selectionview).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_recyclepager).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_circleprogressbar).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_zxingscanview).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_smoothinputlayout).setOnClickListener(this);
        view.findViewById(R.id.widget_btn_cameraview).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.widget_btn_gradienttabstrip:
                GradientTabStripActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_tagtabstrip:
                TagTabStripActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_indicatortabstrip:
                IndicatorTabStripActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_shapeimageview:
                ShapeImageViewActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_stateframelayout:
                StateFrameLayoutActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_wraplayout:
                WrapLayoutActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_replacelayout:
                ReplaceLayoutActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_drawableratingbar:
                DrawableRatingBarActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_headerfootergridview:
                HeaderFooterGridViewActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_multiactiontextview:
                MultiActionTextViewActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_selectionview:
                SelectionViewActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_recyclepager:
                RecyclePagerActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_circleprogressbar:
                CircleProgressBarActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_zxingscanview:
                ZxingScanViewActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_smoothinputlayout:
                SmoothInputLayoutActivity.startActivity(getContext());
                break;
            case R.id.widget_btn_cameraview:
                CameraViewActivity.start(getContext());
                break;
        }
    }
}
