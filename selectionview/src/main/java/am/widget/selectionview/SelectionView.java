package am.widget.selectionview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * 快速跳选
 * Created by Alex on 2016/7/29.
 */
public class SelectionView extends View {

    public static final int STYLE_LIST = 0;// 列表模式
    public static final int STYLE_SLIDER = 1;// 滑块模式
    public static final int LOCATION_VIEW_CENTER = 0;// View中央
    public static final int LOCATION_SLIDER_TOP = 1;// 与滑块顶部对齐
    private int mBarWidth;
    private int mBarItemHeight;
    private final Rect mBarPadding = new Rect();
    private Drawable mBarBackground;
    private int mBarStyle;
    private Drawable mBarSlider;
    private int mNoticeLocation;
    private int mNoticeWidth;
    private int mNoticeHeight;
    private Drawable mNoticeBackground;
    private final Rect mNoticePadding = new Rect();
    private float mItemHeightActual;

    private Selection mSelection;

    public SelectionView(Context context) {
        this(context, null);
    }

    public SelectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.SelectionView);

        custom.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int suggestedMinimumWidth = getSuggestedMinimumWidth();
        final int suggestedMinimumHeight = getSuggestedMinimumHeight();
        final int paddingStart = Compat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int paddingBottom = getPaddingBottom();
//        final int progressDrawableWidth = mProgressDrawable == null ?
//                0 : mProgressDrawable.getIntrinsicWidth();
//        final int progressDrawableHeight = mProgressDrawable == null ?
//                0 : mProgressDrawable.getIntrinsicHeight();
//        final int secondaryProgressWidth = mSecondaryProgress == null ?
//                0 : mSecondaryProgress.getIntrinsicWidth();
//        final int secondaryProgressHeight = mSecondaryProgress == null ?
//                0 : mSecondaryProgress.getIntrinsicHeight();
//        drawableWidth = Math.max(progressDrawableWidth, secondaryProgressWidth);
//        drawableHeight = Math.max(progressDrawableHeight, secondaryProgressHeight);
//        final int itemWidth = drawableWidth * mMax + mDrawablePadding * (mMax - 1);
//        final int width = Math.max(itemWidth + paddingStart + paddingEnd, suggestedMinimumWidth);
//        final int height = Math.max(drawableHeight + paddingTop + paddingBottom,
//                suggestedMinimumHeight);
//        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
//                resolveSize(height, heightMeasureSpec));


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int width = getWidth();
        final int height = getHeight();
        Paint paint = new Paint();
        paint.setColor(0xff00ff00);
        canvas.drawRect(width * 0.5f - 50, height * 0.5f - 50, width * 0.5f + 50, height * 0.5f + 50, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean touch = false;
        final int width = getWidth();
        final int height = getHeight();
        if (event.getX() < width * 0.5f + 50 && event.getX() > width * 0.5f - 50 && event.getY() < height * 0.5f + 50 && event.getY() > height * 0.5f - 50)
            touch = true;
        return super.onTouchEvent(event) || touch;
    }
}
