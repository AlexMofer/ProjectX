package com.am.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.am.widget.R;

public class WidgetListActivity extends Activity implements OnClickListener {

    private WidgetListActivity me = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widgetlist);
        findViewById(R.id.wl_btn_newwidget).setOnClickListener(me);
        findViewById(R.id.wl_btn_wechat).setOnClickListener(me);
        findViewById(R.id.wl_btn_bilateralpane).setOnClickListener(me);
        findViewById(R.id.wl_btn_dotpager).setOnClickListener(me);
        findViewById(R.id.wl_btn_supertab).setOnClickListener(me);
        findViewById(R.id.wl_btn_swiperefresh).setOnClickListener(me);
        findViewById(R.id.wl_btn_draggridview).setOnClickListener(me);
        findViewById(R.id.wl_btn_shapeimage).setOnClickListener(me);
        findViewById(R.id.wl_btn_citylist).setOnClickListener(me);
        findViewById(R.id.wl_btn_sharpcornerbox).setOnClickListener(me);
        findViewById(R.id.wl_btn_sconnectimage).setOnClickListener(me);
        findViewById(R.id.wl_btn_stateframe).setOnClickListener(me);
        findViewById(R.id.wl_btn_replace).setOnClickListener(me);
        findViewById(R.id.wl_btn_drawableratingbar).setOnClickListener(me);
        findViewById(R.id.wl_btn_wraplayout).setOnClickListener(me);
        findViewById(R.id.wl_btn_printer).setOnClickListener(me);
        findViewById(R.id.wl_btn_securitycode).setOnClickListener(me);
        findViewById(R.id.wl_btn_loading).setOnClickListener(me);
        findViewById(R.id.wl_btn_security).setOnClickListener(me);
        findViewById(R.id.wl_btn_cipher).setOnClickListener(me);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.wl_btn_newwidget:
                intent = new Intent(me, NewWidgetActivity.class);
                break;
            case R.id.wl_btn_wechat:
                intent = new Intent(me, WechatActivity.class);
                break;
            case R.id.wl_btn_bilateralpane:
                intent = new Intent(me, BilateralPaneActivity.class);
                break;
            case R.id.wl_btn_dotpager:
                intent = new Intent(me, DotpagerActivity.class);
                break;
            case R.id.wl_btn_supertab:
                intent = new Intent(me, SuperTabActivity.class);
                break;
            case R.id.wl_btn_swiperefresh:
                intent = new Intent(me, SwipeRefreshActivity.class);
                break;
            case R.id.wl_btn_draggridview:
                intent = new Intent(me, DragGridViewActivity.class);
                break;
            case R.id.wl_btn_shapeimage:
                intent = new Intent(me, ShapeImageViewActivity.class);
                break;
            case R.id.wl_btn_citylist:
                intent = new Intent(me, CityListActivity.class);
                break;
            case R.id.wl_btn_sharpcornerbox:
                intent = new Intent(me, SharpCornerBoxActivity.class);
                break;
            case R.id.wl_btn_sconnectimage:
                intent = new Intent(me, ConnectImageActivity.class);
                break;
            case R.id.wl_btn_stateframe:
                intent = new Intent(me, StateFrameActivity.class);
                break;
            case R.id.wl_btn_replace:
                intent = new Intent(me, ReplaceActivity.class);
                break;
            case R.id.wl_btn_drawableratingbar:
                intent = new Intent(me, DrawableRatingBarActivity.class);
                break;
            case R.id.wl_btn_wraplayout:
                intent = new Intent(me, WrapActivity.class);
                break;
            case R.id.wl_btn_printer:
                intent = new Intent(me, PrinterActivity.class);
                break;
            case R.id.wl_btn_securitycode:
                intent = new Intent(me, SecurityCodeActivity.class);
                break;
            case R.id.wl_btn_loading:
                intent = new Intent(me, LoadingActivity.class);
                break;
            case R.id.wl_btn_security:
                intent = new Intent(me, SecurityActivity.class);
                break;
            case R.id.wl_btn_cipher:
                intent = new Intent(me, CipherActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
