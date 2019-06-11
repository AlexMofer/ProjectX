package am.project.x.privateapi;

import android.graphics.Rect;
import android.graphics.Region;

import androidx.annotation.Nullable;

/**
 * Parameters used with OnComputeInternalInsetsListener.
 * Created by Alex on 2019/6/11.
 */
public final class InternalInsetsInfo {

    /**
     * Option for {@link #setTouchableInsets(int)}: the entire window frame
     * can be touched.
     */
    public static final int TOUCHABLE_INSETS_FRAME = 0;

    /**
     * Option for {@link #setTouchableInsets(int)}: the area inside of
     * the content insets can be touched.
     */
    public static final int TOUCHABLE_INSETS_CONTENT = 1;

    /**
     * Option for {@link #setTouchableInsets(int)}: the area inside of
     * the visible insets can be touched.
     */
    public static final int TOUCHABLE_INSETS_VISIBLE = 2;

    /**
     * Option for {@link #setTouchableInsets(int)}: the area inside of
     * the provided touchable region can be touched.
     */
    public static final int TOUCHABLE_INSETS_REGION = 3;

    private final Object mInfo;

    InternalInsetsInfo(Object info) {
        mInfo = info;
    }

    /**
     * @return Offsets from the frame of the window at which the content of
     * windows behind it should be placed.
     */
    @Nullable
    public final Rect getContentInsets() {
        return null;// TODO
    }

    /**
     * @return Offsets from the frame of the window at which windows behind it
     * are visible.
     */
    @Nullable
    public final Rect getVisibleInsets() {
        return null;// TODO
    }

    /**
     * @return Touchable region defined relative to the origin of the frame of the window.
     * Only used when {@link #setTouchableInsets(int)} is called with
     * the option {@link #TOUCHABLE_INSETS_REGION}.
     */
    @Nullable
    public final Region getTouchableRegion() {
        return null;// TODO
    }

    /**
     * Set which parts of the window can be touched: either
     * {@link #TOUCHABLE_INSETS_FRAME}, {@link #TOUCHABLE_INSETS_CONTENT},
     * {@link #TOUCHABLE_INSETS_VISIBLE}, or {@link #TOUCHABLE_INSETS_REGION}.
     */
    public boolean setTouchableInsets(int val) {
        // TODO
        return false;
    }

    @Override
    public String toString() {
        return "InternalInsetsInfo{" +
                "mInfo=" + mInfo +
                '}';
    }
}
