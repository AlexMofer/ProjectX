package am.project.x.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Model
 * Created by Alex on 2017/3/13.
 */
@SuppressWarnings("all")
public class BaseModel<P extends BasePresenter> {

    private P mPresenter;

    public BaseModel(P presenter) {
        mPresenter = presenter;
    }

    protected final P getPresenter() {
        return mPresenter;
    }

    protected void onSaveInstanceState(Bundle outState) {
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
    }

    protected void onRestart() {
    }

    protected void onStart() {
    }

    protected void onResume() {
    }

    protected void onPause() {
    }

    protected void onStop() {
    }

    protected void onDestroy() {
    }

    void detach() {
        mPresenter = null;
        onDetachedPresenter();
    }

    protected void onDetachedPresenter() {

    }

    protected boolean isDetachedPresenter() {
        return mPresenter == null;
    }
}
