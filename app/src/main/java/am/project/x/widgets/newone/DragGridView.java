package am.project.x.widgets.newone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import am.project.x.widgets.headerfootergridview.HeaderFooterGridView;


public class DragGridView extends HeaderFooterGridView {

	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	public DragGridView(Context context) {
		this(context, null);
	}
	
	public DragGridView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public DragGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		// TODO 保证绘制在最上层
		mPaint.setColor(0xffff00ff);
		canvas.drawCircle(50, 50, 50, mPaint);
		System.out.println("here");
	}
}
