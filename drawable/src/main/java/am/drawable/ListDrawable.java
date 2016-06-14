package am.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * 列表Drawable
 * Created by Alex on 2016/5/17.
 */
public class ListDrawable extends Drawable {


    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private Drawable item;
    private int gap;
    private int type;
    private int count;

    @SuppressWarnings("unused")
    public ListDrawable(Drawable item) {
        this(item, 0);
    }

    public ListDrawable(Drawable item, int gap) {
        this(item, gap, HORIZONTAL);
    }

    public ListDrawable(Drawable item, int gap, int type) {
        this.item = item;
        this.gap = gap;
        this.type = type;
        count = 0;
    }

    @Override
    public void draw(Canvas canvas) {
        if (count == 0 || item == null)
            return;
        final int itemWidth = item.getIntrinsicWidth();
        final int itemHeight = item.getIntrinsicHeight();
        item.setBounds(0, 0, itemWidth, itemHeight);
        canvas.save();
        if (type == HORIZONTAL) {
            for (int i = 0; i < count; i++) {
                item.draw(canvas);
                canvas.translate(itemWidth + gap, 0);
            }
        } else {
            for (int i = 0; i < count; i++) {
                item.draw(canvas);
                canvas.translate(0, itemHeight + gap);
            }
        }
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        if (item != null) {
            item.setAlpha(alpha);
        }
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (item != null) {
            item.setColorFilter(colorFilter);
        }
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return item == null ? PixelFormat.TRANSLUCENT : item.getOpacity();
    }

    @Override
    public int getIntrinsicHeight() {
        if (item == null)
            return super.getIntrinsicHeight();
        if (type == HORIZONTAL) {
            return item.getIntrinsicHeight();
        } else {
            return count == 0 ? 0 : item.getIntrinsicHeight() * count + gap * (count - 1);
        }
    }

    @Override
    public int getIntrinsicWidth() {
        if (item == null)
            return super.getIntrinsicWidth();
        if (type == HORIZONTAL) {
            return count == 0 ? 0 : item.getIntrinsicWidth() * count + gap * (count - 1);
        } else {
            return item.getIntrinsicWidth();
        }
    }

    public void setCount(int count) {
        if (this.count == count)
            return;
        this.count = count;
        invalidateSelf();
    }

    public void setDirection(int type) {
        this.type = type;
    }
}
