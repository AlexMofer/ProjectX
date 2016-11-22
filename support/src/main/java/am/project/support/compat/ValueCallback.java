package am.project.support.compat;


/**
 * A callback interface used to provide values asynchronously.
 * Created by Alex on 2016/11/22.
 */
@SuppressWarnings("all")
public interface ValueCallback {
    /**
     * Invoked when the value is available.
     *
     * @param value The value.
     */
    void onReceiveValue(String value);
}
