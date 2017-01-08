package am.widget.shapeimageview;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;


/**
 * 圆角矩形
 *
 */
public class RoundRectImageShape extends ImageShape {

    private static final RectF tRectF = new RectF();

    @Override
    public void drawBorder(ShapeImageView view, Canvas canvas, Paint paint) {
        tRectF.set(0, 0, view.getWidth(), view.getHeight());
        canvas.drawRoundRect(tRectF, view.getRoundRectRadius(),
                view.getRoundRectRadius(), paint);
    }

    @Override
    public void makeShapeByPorterDuff(ShapeImageView view, Canvas canvas, Paint paint) {
        tRectF.set(0, 0, view.getWidth(), view.getHeight());
        canvas.drawRoundRect(tRectF, view.getRoundRectRadius(), view.getRoundRectRadius(), paint);
    }

    @Override
    public void makeShapeByClipPath(ShapeImageView view, Path path) {
        tRectF.set(0, 0, view.getWidth(), view.getHeight());
        path.addRoundRect(tRectF, view.getRoundRectRadius(), view.getRoundRectRadius(),
                Path.Direction.CW);
    }

    @Override
    @TargetApi(21)
    public void makeShapeByOutline(ShapeImageView view, Outline outline) {
        outline.setRoundRect(0, 0, view.getMeasuredWidth(),
                view.getMeasuredHeight(), view.getRoundRectRadius());
    }

    @Override
    protected boolean isOutlineEnable() {
        // TODO
        return false;
    }

    @Override
    protected boolean isClipPathEnable() {
        return false;
    }
}
