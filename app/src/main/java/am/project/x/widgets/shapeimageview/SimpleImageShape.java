package am.project.x.widgets.shapeimageview;

/**
 *
 * Created by Alex on 2015/12/9.
 */
public abstract class SimpleImageShape implements ImageShape {

    private boolean catchOnly = false;
    private int color = 0;
    private int width = 0;

    public SimpleImageShape(int color, int width, boolean catchOnly) {
        this.color = color;
        this.width = width;
        this.catchOnly = catchOnly;
    }

    @Override
    public boolean isCatchBitmapOnly() {
        return catchOnly;
    }

    @Override
    public int getBorderColor() {
        return color;
    }

    @Override
    public float getBorderWidth() {
        return width;
    }
}


