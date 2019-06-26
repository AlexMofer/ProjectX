package am.project.x.privateapi.viewtreeobserver;

import android.annotation.SuppressLint;
import android.view.ViewTreeObserver;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * ViewTreeObserver 隐藏API
 * Created by Alex on 2019/6/11.
 */
@SuppressWarnings("unused")
public class ViewTreeObserverPrivateApis {

    private ViewTreeObserverPrivateApis() {
        //no instance
    }

    @SuppressLint("PrivateApi")
    private static Class<?> getInnerOnComputeInternalInsetsListenerClass()
            throws ClassNotFoundException {
        return Class.forName("android.view.ViewTreeObserver$OnComputeInternalInsetsListener");
    }

    @SuppressLint("PrivateApi")
    private static Method getDeclaredMethod(ViewTreeObserver observer, String name,
                                            Class<?>... parameterTypes)
            throws NoSuchMethodException, SecurityException {
        return observer.getClass().getDeclaredMethod(name, parameterTypes);
    }

    /**
     * 创建内部OnComputeInternalInsetsListener
     *
     * @param listener 回调监听
     * @return 创建的OnComputeInternalInsetsListener，创建失败时为空
     */
    public static Object newInnerOnComputeInternalInsetsListener(
            OnComputeInternalInsetsListener listener) {
        if (listener == null)
            return null;
        try {
            final Class<?> clazz = getInnerOnComputeInternalInsetsListenerClass();
            return Proxy.newProxyInstance(clazz.getClassLoader(),
                    new Class[]{clazz}, new OnComputeInternalInsetsListenerHandler(listener));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Register a callback to be invoked when the invoked when it is time to
     * compute the window's internal insets.
     *
     * @param listener The callback to add
     * @throws IllegalStateException If {@link ViewTreeObserver#isAlive()} returns false
     */
    public static boolean addOnComputeInternalInsetsListener(ViewTreeObserver observer,
                                                             Object listener) {
        if (observer == null)
            return false;
        try {
            getDeclaredMethod(observer, "addOnComputeInternalInsetsListener",
                    getInnerOnComputeInternalInsetsListenerClass())
                    .invoke(observer, listener);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Remove a previously installed internal insets computation callback
     *
     * @param listener The callback to remove
     * @throws IllegalStateException If {@link ViewTreeObserver#isAlive()} returns false
     * @see #addOnComputeInternalInsetsListener(ViewTreeObserver, Object)
     */
    public boolean removeOnComputeInternalInsetsListener(ViewTreeObserver observer,
                                                         Object listener) {
        if (observer == null)
            return false;
        try {
            getDeclaredMethod(observer, "removeOnComputeInternalInsetsListener",
                    getInnerOnComputeInternalInsetsListenerClass())
                    .invoke(observer, listener);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
