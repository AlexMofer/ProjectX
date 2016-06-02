package am.widget.shapeimageview;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;


/**
 * 圆角矩形
 *
 * @author Alex
 */
public class RoundRectImageShape extends ImageShape {

    private float mRadius = 0;
    private final RectF mRectF = new RectF();

    public RoundRectImageShape(float radius) {
        mRadius = radius;
    }

    @Override
    public void makeShapeBase(ShapeImageView view, Canvas canvas, Paint paint) {
        if (view.getDrawable() != null) {
            float half = view.getBorderWidth() * 0.5f;
            mRectF.set(half, half, view.getWidth() - half, view.getHeight() - half);
            canvas.drawRoundRect(mRectF, mRadius, mRadius, paint);
        }
    }

    @Override
    public void drawBorderBase(ShapeImageView view, Canvas canvas, float width,
                               Paint paint) {
        if (width > 0) {
            paint.setStrokeWidth(width);
            final float halfWidth = width * 0.5f;
            mRectF.set(halfWidth - 0.5f, halfWidth - 0.5f, view.getWidth()
                    - halfWidth + 0.5f, view.getHeight() - halfWidth + 0.5f);
            canvas.drawRoundRect(mRectF, mRadius, mRadius, paint);
        }
    }

    @Override
    @TargetApi(21)
    public void makeShapeLollipop(ShapeImageView view, Outline outline) {
        outline.setRoundRect(0, 0, view.getMeasuredWidth(),
                view.getMeasuredHeight(), mRadius);
    }

    @Override
    public void drawBorderLollipop(ShapeImageView view, Canvas canvas, float width,
                                   Paint paint) {
        if (width > 0) {
            paint.setStrokeWidth(width * 2f);
            mRectF.set(0, 0, view.getWidth(), view.getHeight());
            canvas.drawRoundRect(mRectF, mRadius, mRadius, paint);
        }
    }

    /**
     * 获取圆角
     * @return 圆角
     */
    @SuppressWarnings("unused")
    public float getRadius() {
        return mRadius;
    }

    /**
     * 设置圆角
     * @param radius 圆角
     */
    @SuppressWarnings("unused")
    public void setRadius(float radius) {
        if (mRadius != radius) {
            mRadius = radius;
            notifyViewSetChanged();
        }
    }
}
