package am.project.x.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import am.project.x.R;
import am.project.x.widgets.securitycodebutton.SecurityCodeButton;


/**
 * 验证码
 * Created by Alex on 2015/10/16.
 */
public class SecurityCodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_activity_securitycode);
        final SecurityCodeButton securityCodeButton = (SecurityCodeButton) findViewById(R.id.as_stn);
        securityCodeButton.setText("获取验证码");
        securityCodeButton.setDuration(60000);
        securityCodeButton.setOnTimeListener(new SecurityCodeButton.TimeListener() {
            @Override
            public void onNormal(SecurityCodeButton view) {
                view.setText("获取验证码");
                view.setEnabled(true);
            }

            @Override
            public void onCount(SecurityCodeButton view, long duration) {
                view.setText("重新获取" + (duration / 1000) + "秒");
                view.setEnabled(false);
            }
        });

        securityCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                securityCodeButton.start();
            }
        });

        findViewById(R.id.as_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                securityCodeButton.stop();
            }
        });
    }



}
