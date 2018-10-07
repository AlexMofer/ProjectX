package am.project.x.business.others.ftp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import am.project.x.R;
import am.project.x.base.BaseActivity;

/**
 * 文件传输
 */
public class FtpActivity extends BaseActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, FtpActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_ftp;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.ftp_toolbar);
    }
}
