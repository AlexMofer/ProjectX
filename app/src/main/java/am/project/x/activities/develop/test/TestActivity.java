package am.project.x.activities.develop.test;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class TestActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_test;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.test_toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), Boolean.toString(checkApkExist(getApplicationContext(), "com.tencent.mm")), Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("all")
    public boolean checkApkExist(Context context, String packageName) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            if (info != null)
                return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return false;
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }
}
