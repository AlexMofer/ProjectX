package am.project.x.privateapi.viewtreeobserver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * OnComputeInternalInsetsListener回调
 * Created by Alex on 2019/6/11.
 */
final class OnComputeInternalInsetsListenerHandler implements InvocationHandler {

    private final OnComputeInternalInsetsListener mListener;

    OnComputeInternalInsetsListenerHandler(OnComputeInternalInsetsListener listener) {
        mListener = listener;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (args != null && args.length > 0)
            onComputeInternalInsets(args[0]);
        else
            onComputeInternalInsets(null);
        return null;
    }

    private void onComputeInternalInsets(Object info) {
        if (info == null || mListener == null)
            return;
        mListener.onComputeInternalInsets(new InternalInsetsInfo(info));
    }
}
