package am.widget.shapeimageview;

import android.view.View;

import java.util.ArrayList;

/**
 * BaseImageShape
 * Created by Alex on 2015/12/9.
 */
public abstract class BaseImageShape implements ImageShape {

    private ArrayList<ShapeImageView> views = new ArrayList<>();
    private boolean catchOnly = false;
    private int color = 0;
    private int width = 0;

    public BaseImageShape(int color, int width, boolean catchOnly) {
        this.color = color;
        this.width = width;
        this.catchOnly = catchOnly;
    }

    @Override
    public boolean isCatchBitmapOnly() {
        return catchOnly;
    }

    /**
     * 设置是否需要进行Bitmap释放，一般在复用的过程中最好为true
     *
     * @param catchOnly 是否需要进行Bitmap释放
     */
    @SuppressWarnings("unused")
    public void setCatchOnly(boolean catchOnly) {
        if (this.catchOnly != catchOnly) {
            this.catchOnly = catchOnly;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getBorderColor() {
        return color;
    }

    /**
     * 设置边框颜色
     *
     * @param color 边框颜色
     */
    @SuppressWarnings("unused")
    public void setBorderColor(int color) {
        if (this.color != color) {
            this.color = color;
            notifyDataSetChanged();
        }
    }

    @Override
    public float getBorderWidth() {
        return width;
    }

    /**
     * 设置边框宽度
     *
     * @param width 边框宽度
     */
    @SuppressWarnings("unused")
    public void setBorderWidth(int width) {
        if (this.width != width) {
            this.width = width;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onAttached(ShapeImageView view) {
        views.add(view);
    }

    @Override
    public void onDetached(ShapeImageView view) {
        views.remove(view);
    }

    /**
     * 数据变化，刷新界面
     */
    public void notifyDataSetChanged() {
        for (View view : views) {
            view.requestLayout();
            view.invalidate();
        }
    }
}


