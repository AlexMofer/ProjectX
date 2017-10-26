package am.project.x.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Presenter
 * Created by Alex on 2017/3/13.
 */
@SuppressWarnings("all")
public abstract class BasePresenter<V extends BaseView, M extends BaseModel> {
    private V mView;

    public BasePresenter(V view) {
        mView = view;
    }

    protected final V getView() {
        return mView;
    }

    protected abstract M getModel();

    protected void onSaveInstanceState(Bundle outState) {
        final BaseModel model = getModel();
        if (model != null)
            model.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        final M model = getModel();
        if (model != null)
            model.onRestoreInstanceState(savedInstanceState);
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        final M model = getModel();
        if (model != null)
            model.onCreate(savedInstanceState);
    }

    protected void onRestart() {
        final M model = getModel();
        if (model != null)
            model.onRestart();
    }

    protected void onStart() {
        final M model = getModel();
        if (model != null)
            model.onStart();
    }

    protected void onResume() {
        final M model = getModel();
        if (model != null)
            model.onResume();
    }

    protected void onPause() {
        final M model = getModel();
        if (model != null)
            model.onPause();
    }

    protected void onStop() {
        final M model = getModel();
        if (model != null)
            model.onStop();
    }

    protected void onDestroy() {
        final M model = getModel();
        if (model != null) {
            model.onDestroy();
            model.detach();
        }
        mView = null;
        onDetachedView();
    }

    protected void onDetachedView() {

    }

    protected boolean isDetachedView() {
        return mView == null;
    }
}