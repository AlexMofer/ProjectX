package am.project.x.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment
 * Created by Alex on 2017/3/14.
 */
@SuppressWarnings("unused")
public abstract class BaseSupportFragment extends Fragment {

    private LocalBroadcastManager mLocalBroadcastManager;// 应用内部广播
    /**
     * 广播接受器
     */
    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            onReceiveLocalBroadcast(context, intent);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onCreate(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(
                getContentViewLayout(inflater, container, savedInstanceState),
                container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeFragment(savedInstanceState);
        if (isLocalBroadcastEnable()) {
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
            IntentFilter filter = new IntentFilter();
            onAddLocalAction(filter);
            mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver, filter);
            onRegisteredLocalBroadcastReceiver();
        }
    }

    /**
     * 获取内容Layout
     *
     * @param inflater           布局容器
     * @param container          根View
     * @param savedInstanceState 保存的状态
     * @return 资源id
     */
    @LayoutRes
    protected abstract int getContentViewLayout(LayoutInflater inflater,
                                                ViewGroup container, Bundle savedInstanceState);

    /**
     * 初始化Fragment
     *
     * @param savedInstanceState 保存的实例数据
     */
    protected abstract void initializeFragment(@Nullable Bundle savedInstanceState);

    /**
     * 获取基础Presenter
     *
     * @return 基础Presenter
     */
    protected BasePresenter getPresenter() {
        return null;
    }

    /**
     * 判断是否开启应用内广播
     * 对于Fragment来说其为非必须选项
     *
     * @return 是否开启
     */
    protected boolean isLocalBroadcastEnable() {
        return false;
    }

    /**
     * 添加本地广播监听意图
     *
     * @param filter 意图过滤器
     */
    protected void onAddLocalAction(IntentFilter filter) {
    }

    /**
     * 接收到本地广播
     *
     * @param context 发起广播的context
     * @param intent  发起广播的意图
     */
    protected void onReceiveLocalBroadcast(Context context, Intent intent) {
    }

    /**
     * 已注册本地广播接收器
     */
    protected void onRegisteredLocalBroadcastReceiver() {
    }

    /**
     * 已取消注册本地广播接收器
     */
    protected void onUnregisteredLocalBroadcastReceiver() {
    }

    /**
     * 判断本地广播接收器是否已取消注册
     *
     * @return 是否已取消注册
     */
    public final boolean isLocalBroadcastUnregistered() {
        return mLocalBroadcastManager == null;
    }

    /**
     * 通过ID查找View
     *
     * @param id  View 的资源ID
     * @param <V> View类型
     * @return 对应资源ID的View
     */
    @SuppressWarnings("unchecked")
    public final <V extends View> V findViewById(int id) {
        if (null == getView()) {
            // 在错误的时机调用
            throw new IllegalStateException("Fragment " + this
                    + " has not created its view yet.");
        }
        return (V) getView().findViewById(id);
    }

    /**
     * 发送广播
     *
     * @param action 广播动作
     */
    public final void sendLocalBroadcast(String action) {
        sendLocalBroadcast(new Intent(action));
    }

    /**
     * 发送广播
     *
     * @param intent 广播意图
     */
    public final void sendLocalBroadcast(Intent intent) {
        if (isLocalBroadcastUnregistered()) {
            return;
        }
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 判断权限是否被授予
     *
     * @param permission 权限
     * @return 是否被授予
     */
    public final boolean isPermissionGranted(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(getActivity(), permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onDestroy();
        }
        if (!isLocalBroadcastUnregistered()) {
            mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver);
            mLocalBroadcastManager = null;
            onUnregisteredLocalBroadcastReceiver();
        }
    }
}