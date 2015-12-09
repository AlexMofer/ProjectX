package com.am.widget.supergridview.support;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.view.Window;

public class Tools {
	/**
	 * 获取状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {
		try {
			Rect rectangle = new Rect();
			Window window = ((Activity) context).getWindow();
			window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
			int statusBarHeight = rectangle.top;
			int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT)
					.getTop();
			int titleBarHeight = contentViewTop - statusBarHeight;
			return titleBarHeight;
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static Drawable getDefaultDrawable(int deleteHeight, int deleteWidth) {
		deleteHeight = deleteWidth;
		ShapeDrawable shapeDrawable = new ShapeDrawable();
		Shape s = new Shape() {

			@Override
			public void draw(Canvas canvas, Paint paint) {

				canvas.save();
				canvas.translate(canvas.getWidth() * 0.5f,
						canvas.getHeight() * 0.5f);
				paint.setColor(0xffff4444);
				canvas.drawCircle(0, 0, 50, paint);
				paint.setColor(0xffffffff);
				canvas.rotate(45);
				canvas.drawRect(-40, -5, 40, 5, paint);
				canvas.rotate(-90);
				canvas.drawRect(-40, -5, 40, 5, paint);
				canvas.restore();
			}
		};
		shapeDrawable.setShape(s);
		shapeDrawable.setIntrinsicHeight(deleteHeight);
		shapeDrawable.setIntrinsicWidth(deleteHeight);

		return shapeDrawable;
	}
}
