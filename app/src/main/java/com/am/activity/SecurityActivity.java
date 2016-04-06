package com.am.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.am.utils.SecurityUtils;
import com.am.widget.R;

import diff.strazzere.anti.AntiEmulator;

/**
 * 安全检测 Activity
 * Created by Alex on 2016/4/6.
 */
public class SecurityActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        TextView tvSecurity = (TextView) findViewById(R.id.security);
        String log = "";
        log += "isDebuggable = " + SecurityUtils.isDebuggable(this) + "\n";
        log += "isQEmuEnvDetected = " + AntiEmulator.isQEmuEnvDetected(this) + "\n";
        log += "isDebugged = " + AntiEmulator.isDebugged() + "\n";
        log += "isMonkeyDetected = " + AntiEmulator.isMonkeyDetected() + "\n";
        log += "isTaintTrackingDetected = " + AntiEmulator.isTaintTrackingDetected(this) + "\n";
        log += "Signature = " + SecurityUtils.getSignature(this);
        tvSecurity.setText(log);
    }
}
