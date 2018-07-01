package am.project.x.activities;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * 基础类
 * Created by Alex on 2016/5/23.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = getContentViewLayoutResources();
        if (layout != 0) {
            setContentView(layout);
        }
        initResource(savedInstanceState);
    }

    /**
     * 获取内容布局
     *
     * @return 布局资源ID
     */
    protected abstract int getContentViewLayoutResources();

    /**
     * 初始化
     */
    protected abstract void initResource(Bundle savedInstanceState);


    /**
     * 设置Toolbar 为ActionBar
     *
     * @param toolbarId Toolbar资源ID
     * @param showTitle 是否显示标题
     */
    public void setSupportActionBar(@IdRes int toolbarId, boolean showTitle) {
        Toolbar mToolbar = findViewById(toolbarId);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationOnClickListener(new NavigationOnClickListener());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(showTitle);
            }
        }
    }

    /**
     * 设置Toolbar 为ActionBar
     *
     * @param toolbarId Toolbar资源ID
     */
    public void setSupportActionBar(@IdRes int toolbarId) {
        setSupportActionBar(toolbarId, false);
    }

    /**
     * Toolbar 返回按钮点击
     */
    protected void onNavigationClick() {
        onBackPressed();
    }

    private class NavigationOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            onNavigationClick();
        }
    }
}
