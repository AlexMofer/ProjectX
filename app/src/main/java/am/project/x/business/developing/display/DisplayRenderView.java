package am.project.x.business.developing.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.View;

/**
 * 显示渲染视图
 * Created by Xiang Zhicheng on 2017/11/3.
 */

public class DisplayRenderView extends View {

    private final TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private String mText;

    DisplayRenderView(Context context) {
        super(context);
        mPaint.density = context.getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mText == null || mText.length() <= 0)
            return;
        mPaint.setColor(0x8000ff00);
        canvas.drawRect(10, 10, getMeasuredWidth() - 10, getMeasuredHeight() - 10, mPaint);
        mPaint.setColor(0xffffff00);
        canvas.save();
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(getWidth() * 0.8f);
        canvas.translate(getWidth() * 0.5f, getHeight() * 0.5f);
        canvas.drawText(mText, 0, 0, mPaint);
        canvas.restore();
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }
}
