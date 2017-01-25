package am.widget.shapeimageview;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;


/**
 * 圆形
 */
@SuppressWarnings("all")
public class CircleImageShape extends ImageShape {

    @Override
    public void drawBorder(ShapeImageView view, Canvas canvas, Paint paint) {
        final float cx = view.getWidth() * 0.5f;
        final float cy = view.getHeight() * 0.5f;
        final float radius = cx > cy ? cy : cx;
        canvas.drawCircle(cx, cy, radius, paint);
    }

    @Override
    public void makeShapeByPorterDuff(ShapeImageView view, Canvas canvas, Paint paint) {
        final float cx = view.getWidth() * 0.5f;
        final float cy = view.getHeight() * 0.5f;
        final float radius = cx > cy ? cy : cx;
        canvas.drawCircle(cx, cy, radius, paint);
    }

    @Override
    public void makeShapeByClipPath(ShapeImageView view, Path path) {
        final int width = view.getMeasuredWidth();
        final int height = view.getMeasuredHeight();
        final int radius = width > height ? height : width;
        final float x = width * 0.5f;
        final float y = height * 0.5f;
        path.addCircle(x, y, radius * 0.5f, Path.Direction.CW);
    }

    @Override
    @TargetApi(21)
    public void makeShapeByOutline(ShapeImageView view, Outline outline) {
        final int width = view.getMeasuredWidth();
        final int height = view.getMeasuredHeight();
        final int radius = width > height ? height : width;
        final int left = (width - radius) / 2;
        final int top = (height - radius) / 2;
        final int right = left + radius;
        final int bottom = top + radius;
        outline.setOval(left, top, right, bottom);
    }
}
