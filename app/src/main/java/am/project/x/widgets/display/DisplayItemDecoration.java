package am.project.x.widgets.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * ItemDecoration
 * Created by Xiang Zhicheng on 2017/10/18.
 */
public class DisplayItemDecoration extends RecyclerView.ItemDecoration {

    private final int mMargin;
    private final Rect mBounds = new Rect();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DisplayItemDecoration(Context context) {
        mMargin = 54;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        final int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.top = mMargin;
            outRect.left = mMargin;
            outRect.bottom = mMargin;
            outRect.right = mMargin;
        } else {
            outRect.top = 0;
            outRect.left = mMargin;
            outRect.bottom = mMargin;
            outRect.right = mMargin;
        }
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
        mPaint.setColor(0x40000000);
        canvas.save();
        final int left;
        final int right;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            canvas.drawRect(mBounds, mPaint);
        }
        canvas.restore();
    }

    public int getMargin() {
        return mMargin;
    }
}