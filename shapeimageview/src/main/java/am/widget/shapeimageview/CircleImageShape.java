package am.widget.shapeimageview;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;


/**
 * 圆形
 * @author Alex
 *
 */
public class CircleImageShape extends ImageShape {


    @SuppressWarnings("unused")
	public CircleImageShape() {
		this(false);
	}

	public CircleImageShape(boolean catchOnly) {
		this(0, 0, catchOnly);
	}


    @SuppressWarnings("unused")
	public CircleImageShape(int color, int width) {
		this(color, width, false);
	}
	
	public CircleImageShape(int color, int width, boolean catchOnly) {
		super(color, width, catchOnly);
	}
	
	@Override
	public void makeShapeBase(ImageView view, Canvas canvas, Paint paint) {
		if (view.getDrawable() != null) {
			final float cx = view.getWidth() * 0.5f;
			final float cy = view.getHeight() * 0.5f;
			final float radius = cx > cy ? cy : cx;
			canvas.drawCircle(cx, cy, radius, paint);
		}
	}

	@Override
	public void drawBorderBase(View view, Canvas canvas, float width,
			Paint paint) {
		if (width > 0) {
			paint.setStrokeWidth(width);
			final float radios = view.getWidth() > view.getHeight() ? (view
					.getHeight() - width) * 0.5f
					: (view.getWidth() - width) * 0.5f;
			canvas.drawCircle(view.getWidth() * 0.5f, view.getHeight() * 0.5f,
					radios, paint);
		}
	}

	@Override
	@TargetApi(21)
	public void makeShapeLollipop(View view, Outline outline) {
		final int width = view.getMeasuredWidth();
		final int height = view.getMeasuredHeight();
		final int radius = width > height ? height : width;
		final int left = (width - radius) / 2;
		final int top = (height - radius) / 2;
		final int right = left + radius;
		final int bottom = top + radius;
		outline.setOval(left, top, right, bottom);
	}

	@Override
	public void drawBorderLollipop(View view, Canvas canvas, float width,
			Paint paint) {
		if (width > 0) {
			paint.setStrokeWidth(width * 2);
			final float cx = view.getWidth() * 0.5f;
			final float cy = view.getHeight() * 0.5f;
			final float radius = cx > cy ? cy : cx;
			canvas.drawCircle(cx, cy, radius, paint);
		}
	}
}
