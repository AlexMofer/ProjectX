package am.project.x.privateapi.viewtreeobserver;

import android.graphics.Rect;
import android.graphics.Region;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Parameters used with OnComputeInternalInsetsListener.
 * Created by Alex on 2019/6/11.
 */
@SuppressWarnings("WeakerAccess")
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
    private Field mContentInsets;
    private Field mVisibleInsets;
    private Field mTouchableRegion;
    private int mFrame;
    private int mContent;
    private int mVisible;
    private int mRegion;
    private Method mMethodSetTouchableInsets;

    InternalInsetsInfo(Object info) {
        mInfo = info;
        final Class<?> clazz = info.getClass();
        try {
            mContentInsets = clazz.getDeclaredField("contentInsets");
        } catch (Exception e) {
            mContentInsets = null;
        }
        try {
            mVisibleInsets = clazz.getDeclaredField("visibleInsets");
        } catch (Exception e) {
            mVisibleInsets = null;
        }
        try {
            mTouchableRegion = clazz.getDeclaredField("touchableRegion");
        } catch (Exception e) {
            mTouchableRegion = null;
        }
        try {
            mFrame = (int) clazz.getDeclaredField("TOUCHABLE_INSETS_FRAME").get(null);
            mContent = (int) clazz.getDeclaredField("TOUCHABLE_INSETS_CONTENT").get(null);
            mVisible = (int) clazz.getDeclaredField("TOUCHABLE_INSETS_VISIBLE").get(null);
            mRegion = (int) clazz.getDeclaredField("TOUCHABLE_INSETS_REGION").get(null);
        } catch (Exception e) {
            // ignore
        }
        try {
            mMethodSetTouchableInsets =
                    clazz.getDeclaredMethod("setTouchableInsets", int.class);
        } catch (Exception e) {
            mMethodSetTouchableInsets = null;
        }
    }

    /**
     * @return Offsets from the frame of the window at which the content of
     * windows behind it should be placed.
     */
    @NonNull
    public final Rect getContentInsets() throws Exception {
        if (mContentInsets == null)
            throw new UnsupportedOperationException();
        return (Rect) mContentInsets.get(mInfo);
    }

    /**
     * @return Offsets from the frame of the window at which windows behind it
     * are visible.
     */
    @NonNull
    public final Rect getVisibleInsets() throws Exception {
        if (mVisibleInsets == null)
            throw new UnsupportedOperationException();
        return (Rect) mVisibleInsets.get(mInfo);
    }

    /**
     * @return Touchable region defined relative to the origin of the frame of the window.
     * Only used when {@link #setTouchableInsets(int)} is called with
     * the option {@link #TOUCHABLE_INSETS_REGION}.
     */
    @NonNull
    public final Region getTouchableRegion() throws Exception {
        if (mTouchableRegion == null)
            throw new UnsupportedOperationException();
        return (Region) mTouchableRegion.get(mInfo);
    }

    /**
     * Set which parts of the window can be touched: either
     * {@link #TOUCHABLE_INSETS_FRAME}, {@link #TOUCHABLE_INSETS_CONTENT},
     * {@link #TOUCHABLE_INSETS_VISIBLE}, or {@link #TOUCHABLE_INSETS_REGION}.
     *
     * @return succeed or not.
     */
    public boolean setTouchableInsets(int val) {
        if (mMethodSetTouchableInsets == null)
            return false;
        if (mFrame == mContent && mContent == mVisible && mVisible == mRegion)
            return false;
        switch (val) {
            case TOUCHABLE_INSETS_FRAME:
                val = mFrame;
                break;
            case TOUCHABLE_INSETS_CONTENT:
                val = mContent;
                break;
            case TOUCHABLE_INSETS_VISIBLE:
                val = mVisible;
                break;
            case TOUCHABLE_INSETS_REGION:
                val = mRegion;
                break;
            default:
                return false;
        }
        try {
            mMethodSetTouchableInsets.invoke(mInfo, val);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
