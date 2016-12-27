package am.project.x.widgets.supergridview.support;

import android.annotation.TargetApi;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * AnimatorViewCompat
 * Created by Alex on 2016/6/3.
 */
@SuppressWarnings("all")
public class AnimatorViewCompat {

    interface ViewCompatImpl {
        float getAlpha(View view);

        void setAlpha(View view, float alpha);

        float getPivotX(View view);

        void setPivotX(View view, float pivotX);

        float getPivotY(View view);

        void setPivotY(View view, float pivotY);

        float getRotation(View view);

        void setRotation(View view, float rotation);

        float getRotationX(View view);

        void setRotationX(View view, float rotationX);

        float getRotationY(View view);

        void setRotationY(View view, float rotationY);

        float getScaleX(View view);

        void setScaleX(View view, float scaleX);

        float getScaleY(View view);

        void setScaleY(View view, float scaleY);

        void setScrollX(View view, int scrollX);

        void setScrollY(View view, int scrollY);

        float getTranslationX(View view);

        void setTranslationX(View view, float translationX);

        float getTranslationY(View view);

        void setTranslationY(View view, float translationY);

        float getX(View view);

        void setX(View view, float x);

        float getY(View view);

        void setY(View view, float y);
    }

    static class AnimatorProxy extends Animation {

        private static final WeakHashMap<View, AnimatorProxy> PROXIES = new WeakHashMap<>();

        public static AnimatorProxy wrap(View view) {
            AnimatorProxy proxy = PROXIES.get(view);
            if (proxy == null || proxy != view.getAnimation()) {
                proxy = new AnimatorProxy(view);
                PROXIES.put(view, proxy);
            }
            return proxy;
        }

        private final WeakReference<View> mView;
        private final Camera mCamera = new Camera();
        private boolean mHasPivot;

        private float mAlpha = 1;
        private float mPivotX;
        private float mPivotY;
        private float mRotationX;
        private float mRotationY;
        private float mRotationZ;
        private float mScaleX = 1;
        private float mScaleY = 1;
        private float mTranslationX;
        private float mTranslationY;

        private final RectF mBefore = new RectF();
        private final RectF mAfter = new RectF();
        private final Matrix mTempMatrix = new Matrix();

        private AnimatorProxy(View view) {
            setDuration(0);
            setFillAfter(true);
            view.setAnimation(this);
            mView = new WeakReference<>(view);
        }

        public float getAlpha() {
            return mAlpha;
        }

        public void setAlpha(float alpha) {
            if (mAlpha != alpha) {
                mAlpha = alpha;
                View view = mView.get();
                if (view != null) {
                    view.invalidate();
                }
            }
        }

        public float getPivotX() {
            return mPivotX;
        }

        public void setPivotX(float pivotX) {
            if (!mHasPivot || mPivotX != pivotX) {
                prepareForUpdate();
                mHasPivot = true;
                mPivotX = pivotX;
                invalidateAfterUpdate();
            }
        }

        public float getPivotY() {
            return mPivotY;
        }

        public void setPivotY(float pivotY) {
            if (!mHasPivot || mPivotY != pivotY) {
                prepareForUpdate();
                mHasPivot = true;
                mPivotY = pivotY;
                invalidateAfterUpdate();
            }
        }

        public float getRotation() {
            return mRotationZ;
        }

        public void setRotation(float rotation) {
            if (mRotationZ != rotation) {
                prepareForUpdate();
                mRotationZ = rotation;
                invalidateAfterUpdate();
            }
        }

        public float getRotationX() {
            return mRotationX;
        }

        public void setRotationX(float rotationX) {
            if (mRotationX != rotationX) {
                prepareForUpdate();
                mRotationX = rotationX;
                invalidateAfterUpdate();
            }
        }

        public float getRotationY() {
            return mRotationY;
        }

        public void setRotationY(float rotationY) {
            if (mRotationY != rotationY) {
                prepareForUpdate();
                mRotationY = rotationY;
                invalidateAfterUpdate();
            }
        }

        public float getScaleX() {
            return mScaleX;
        }

        public void setScaleX(float scaleX) {
            if (mScaleX != scaleX) {
                prepareForUpdate();
                mScaleX = scaleX;
                invalidateAfterUpdate();
            }
        }

        public float getScaleY() {
            return mScaleY;
        }

        public void setScaleY(float scaleY) {
            if (mScaleY != scaleY) {
                prepareForUpdate();
                mScaleY = scaleY;
                invalidateAfterUpdate();
            }
        }

        public void setScrollX(int value) {
            View view = mView.get();
            if (view != null) {
                view.scrollTo(value, view.getScrollY());
            }
        }

        public void setScrollY(int value) {
            View view = mView.get();
            if (view != null) {
                view.scrollTo(view.getScrollX(), value);
            }
        }

        public float getTranslationX() {
            return mTranslationX;
        }

        public void setTranslationX(float translationX) {
            if (mTranslationX != translationX) {
                prepareForUpdate();
                mTranslationX = translationX;
                invalidateAfterUpdate();
            }
        }

        public float getTranslationY() {
            return mTranslationY;
        }

        public void setTranslationY(float translationY) {
            if (mTranslationY != translationY) {
                prepareForUpdate();
                mTranslationY = translationY;
                invalidateAfterUpdate();
            }
        }

        public float getX() {
            View view = mView.get();
            if (view == null) {
                return 0;
            }
            return view.getLeft() + mTranslationX;
        }

        public void setX(float x) {
            View view = mView.get();
            if (view != null) {
                setTranslationX(x - view.getLeft());
            }
        }

        public float getY() {
            View view = mView.get();
            if (view == null) {
                return 0;
            }
            return view.getTop() + mTranslationY;
        }

        public void setY(float y) {
            View view = mView.get();
            if (view != null) {
                setTranslationY(y - view.getTop());
            }
        }

        private void prepareForUpdate() {
            View view = mView.get();
            if (view != null) {
                computeRect(mBefore, view);
            }
        }

        private void invalidateAfterUpdate() {
            View view = mView.get();
            if (view == null || view.getParent() == null) {
                return;
            }

            final RectF after = mAfter;
            computeRect(after, view);
            after.union(mBefore);

            ((View) view.getParent()).invalidate(
                    (int) Math.floor(after.left),
                    (int) Math.floor(after.top),
                    (int) Math.ceil(after.right),
                    (int) Math.ceil(after.bottom));
        }

        private void computeRect(final RectF r, View view) {
            // compute current rectangle according to matrix transformation
            final float w = view.getWidth();
            final float h = view.getHeight();

            // use a rectangle at 0,0 to make sure we don't run into issues with scaling
            r.set(0, 0, w, h);

            final Matrix m = mTempMatrix;
            m.reset();
            transformMatrix(m, view);
            mTempMatrix.mapRect(r);

            r.offset(view.getLeft(), view.getTop());

            // Straighten coords if rotations flipped them
            if (r.right < r.left) {
                final float f = r.right;
                r.right = r.left;
                r.left = f;
            }
            if (r.bottom < r.top) {
                final float f = r.top;
                r.top = r.bottom;
                r.bottom = f;
            }
        }

        private void transformMatrix(Matrix m, View view) {
            final float w = view.getWidth();
            final float h = view.getHeight();
            final boolean hasPivot = mHasPivot;
            final float pX = hasPivot ? mPivotX : w / 2f;
            final float pY = hasPivot ? mPivotY : h / 2f;

            final float rX = mRotationX;
            final float rY = mRotationY;
            final float rZ = mRotationZ;
            if ((rX != 0) || (rY != 0) || (rZ != 0)) {
                final Camera camera = mCamera;
                camera.save();
                camera.rotateX(rX);
                camera.rotateY(rY);
                camera.rotateZ(-rZ);
                camera.getMatrix(m);
                camera.restore();
                m.preTranslate(-pX, -pY);
                m.postTranslate(pX, pY);
            }

            final float sX = mScaleX;
            final float sY = mScaleY;
            if ((sX != 1.0f) || (sY != 1.0f)) {
                m.postScale(sX, sY);
                final float sPX = -(pX / w) * ((sX * w) - w);
                final float sPY = -(pY / h) * ((sY * h) - h);
                m.postTranslate(sPX, sPY);
            }

            m.postTranslate(mTranslationX, mTranslationY);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            View view = mView.get();
            if (view != null) {
                t.setAlpha(mAlpha);
                transformMatrix(t.getMatrix(), view);
            }
        }
    }


    static class BaseViewCompatImpl implements ViewCompatImpl {

        @Override
        public float getAlpha(View view) {
            return AnimatorProxy.wrap(view).getAlpha();
        }

        @Override
        public void setAlpha(View view, float alpha) {
            AnimatorProxy.wrap(view).setAlpha(alpha);
        }

        @Override
        public float getPivotX(View view) {
            return AnimatorProxy.wrap(view).getPivotX();
        }

        @Override
        public void setPivotX(View view, float pivotX) {
            AnimatorProxy.wrap(view).setPivotX(pivotX);
        }

        @Override
        public float getPivotY(View view) {
            return AnimatorProxy.wrap(view).getPivotY();
        }

        @Override
        public void setPivotY(View view, float pivotY) {
            AnimatorProxy.wrap(view).setPivotY(pivotY);
        }

        @Override
        public float getRotation(View view) {
            return AnimatorProxy.wrap(view).getRotation();
        }

        @Override
        public void setRotation(View view, float rotation) {
            AnimatorProxy.wrap(view).setRotation(rotation);
        }

        @Override
        public float getRotationX(View view) {
            return AnimatorProxy.wrap(view).getRotationX();
        }

        @Override
        public void setRotationX(View view, float rotationX) {
            AnimatorProxy.wrap(view).setRotationX(rotationX);
        }

        @Override
        public float getRotationY(View view) {
            return AnimatorProxy.wrap(view).getRotationY();
        }

        @Override
        public void setRotationY(View view, float rotationY) {
            AnimatorProxy.wrap(view).setRotationY(rotationY);
        }

        @Override
        public float getScaleX(View view) {
            return AnimatorProxy.wrap(view).getScaleX();
        }

        @Override
        public void setScaleX(View view, float scaleX) {
            AnimatorProxy.wrap(view).setScaleX(scaleX);
        }

        @Override
        public float getScaleY(View view) {
            return AnimatorProxy.wrap(view).getScaleY();
        }

        @Override
        public void setScaleY(View view, float scaleY) {
            AnimatorProxy.wrap(view).setScaleY(scaleY);
        }

        @Override
        public void setScrollX(View view, int scrollX) {
            AnimatorProxy.wrap(view).setScrollX(scrollX);
        }

        @Override
        public void setScrollY(View view, int scrollY) {
            AnimatorProxy.wrap(view).setScrollY(scrollY);
        }

        @Override
        public float getTranslationX(View view) {
            return AnimatorProxy.wrap(view).getTranslationX();
        }

        @Override
        public void setTranslationX(View view, float translationX) {
            AnimatorProxy.wrap(view).setTranslationX(translationX);
        }

        @Override
        public float getTranslationY(View view) {
            return AnimatorProxy.wrap(view).getTranslationY();
        }

        @Override
        public void setTranslationY(View view, float translationY) {
            AnimatorProxy.wrap(view).setTranslationY(translationY);
        }

        @Override
        public float getX(View view) {
            return AnimatorProxy.wrap(view).getX();
        }

        @Override
        public void setX(View view, float x) {
            AnimatorProxy.wrap(view).setX(x);
        }

        @Override
        public float getY(View view) {
            return AnimatorProxy.wrap(view).getY();
        }

        @Override
        public void setY(View view, float y) {
            AnimatorProxy.wrap(view).setY(y);
        }
    }

    @TargetApi(11)
    static class HCViewCompatImpl extends BaseViewCompatImpl {

        @Override
        public float getAlpha(View view) {
            return view.getAlpha();
        }

        @Override
        public void setAlpha(View view, float alpha) {
            view.setAlpha(alpha);
        }

        @Override
        public float getPivotX(View view) {
            return view.getPivotX();
        }

        @Override
        public void setPivotX(View view, float pivotX) {
            view.setPivotX(pivotX);
        }

        @Override
        public float getPivotY(View view) {
            return view.getPivotY();
        }

        @Override
        public void setPivotY(View view, float pivotY) {
            view.setPivotY(pivotY);
        }

        @Override
        public float getRotation(View view) {
            return view.getRotation();
        }

        @Override
        public void setRotation(View view, float rotation) {
            view.setRotation(rotation);
        }

        @Override
        public float getRotationX(View view) {
            return view.getRotationX();
        }

        @Override
        public void setRotationX(View view, float rotationX) {
            view.setRotationX(rotationX);
        }

        @Override
        public float getRotationY(View view) {
            return view.getRotationY();
        }

        @Override
        public void setRotationY(View view, float rotationY) {
            view.setRotationY(rotationY);
        }

        @Override
        public float getScaleX(View view) {
            return view.getScaleX();
        }

        @Override
        public void setScaleX(View view, float scaleX) {
            view.setScaleX(scaleX);
        }

        @Override
        public float getScaleY(View view) {
            return view.getScaleY();
        }

        @Override
        public void setScaleY(View view, float scaleY) {
            view.setScaleY(scaleY);
        }

        @Override
        public float getTranslationX(View view) {
            return view.getTranslationX();
        }

        @Override
        public void setTranslationX(View view, float translationX) {
            view.setTranslationX(translationX);
        }

        @Override
        public float getTranslationY(View view) {
            return view.getTranslationY();
        }

        @Override
        public void setTranslationY(View view, float translationY) {
            view.setTranslationY(translationY);
        }

        @Override
        public float getX(View view) {
            return view.getX();
        }

        @Override
        public void setX(View view, float x) {
            view.setX(x);
        }

        @Override
        public float getY(View view) {
            return view.getY();
        }

        @Override
        public void setY(View view, float y) {
            view.setY(y);
        }
    }

    @TargetApi(14)
    static class ICSViewCompatImpl extends HCViewCompatImpl {
        @Override
        public void setScrollX(View view, int scrollX) {
            view.setScrollX(scrollX);
        }

        @Override
        public void setScrollY(View view, int scrollY) {
            view.setScrollY(scrollY);
        }
    }

    static final ViewCompatImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 14) {
            IMPL = new ICSViewCompatImpl();
        } else if (version >= 11) {
            IMPL = new HCViewCompatImpl();
        } else {
            IMPL = new BaseViewCompatImpl();
        }
    }

    public static float getAlpha(View view) {
        return IMPL.getAlpha(view);
    }

    public static void setAlpha(View view, float alpha) {
        IMPL.setAlpha(view, alpha);
    }

    public static float getPivotX(View view) {
        return IMPL.getPivotX(view);
    }

    public static void setPivotX(View view, float pivotX) {
        IMPL.setPivotX(view, pivotX);
    }

    public static float getPivotY(View view) {
        return IMPL.getPivotY(view);
    }

    public static void setPivotY(View view, float pivotY) {
        IMPL.setPivotY(view, pivotY);
    }

    public static float getRotation(View view) {
        return IMPL.getRotation(view);
    }

    public static void setRotation(View view, float rotation) {
        IMPL.setRotation(view, rotation);
    }

    public static float getRotationX(View view) {
        return IMPL.getRotationX(view);
    }

    public static void setRotationX(View view, float rotationX) {
        IMPL.setRotationX(view, rotationX);
    }

    public static float getRotationY(View view) {
        return IMPL.getRotationY(view);
    }

    public static void setRotationY(View view, float rotationY) {
        IMPL.setRotationY(view, rotationY);
    }

    public static float getScaleX(View view) {
        return IMPL.getScaleX(view);
    }

    public static void setScaleX(View view, float scaleX) {
        IMPL.setScaleX(view, scaleX);
    }

    public static float getScaleY(View view) {
        return IMPL.getScaleY(view);
    }

    public static void setScaleY(View view, float scaleY) {
        IMPL.setScaleY(view, scaleY);
    }

    public static void setScrollX(View view, int scrollX) {
        IMPL.setScrollX(view, scrollX);
    }

    public static void setScrollY(View view, int scrollY) {
        IMPL.setScrollY(view, scrollY);
    }

    public static float getTranslationX(View view) {
        return IMPL.getTranslationX(view);
    }

    public static void setTranslationX(View view, float translationX) {
        IMPL.setTranslationX(view, translationX);
    }

    public static float getTranslationY(View view) {
        return IMPL.getTranslationY(view);
    }

    public static void setTranslationY(View view, float translationY) {
        IMPL.setTranslationY(view, translationY);
    }

    public static float getX(View view) {
        return IMPL.getX(view);
    }

    public static void setX(View view, float x) {
        IMPL.setX(view, x);
    }

    public static float getY(View view) {
        return IMPL.getY(view);
    }

    public static void setY(View view, float y) {
        IMPL.setY(view, y);
    }
}
