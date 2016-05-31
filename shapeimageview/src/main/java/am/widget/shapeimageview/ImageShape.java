package am.widget.shapeimageview;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;

/**
 * 形状
 * Created by Alex on 2015/12/9.
 */
public interface ImageShape {

    /**
     * 低版本下的形状处理
     *
     * @param view   ImageView
     * @param canvas 画布
     * @param paint  画笔
     */
    void makeShapeBase(ImageView view, Canvas canvas, Paint paint);

    /**
     * 低版本下的绘制边框
     *
     * @param view   View
     * @param canvas 画布
     * @param width  边框宽度
     * @param paint  边框绘制画笔
     */
    void drawBorderBase(View view, Canvas canvas, float width, Paint paint);

    /**
     * Lollipop 下的形状处理
     *
     * @param view    View
     * @param outline Outline
     */
    @TargetApi(21)
    void makeShapeLollipop(View view, Outline outline);

    /**
     * Lollipop 下的绘制边框
     *
     * @param view   View
     * @param canvas 画布
     * @param width  边框宽度
     * @param paint  边框绘制画笔
     */
    void drawBorderLollipop(View view, Canvas canvas, float width, Paint paint);

    /**
     * 是否需要进行Bitmap释放，一般在复用的过程中最好返回true
     *
     * @return 是否需要进行Bitmap释放
     */
    boolean isCatchBitmapOnly();

    /**
     * 获取边框颜色
     *
     * @return 边框颜色
     */
    int getBorderColor();

    /**
     * 获取边框宽度
     *
     * @return 边框宽度
     */
    float getBorderWidth();

    /**
     * 捆绑ShapeImageView
     *
     * @param view ShapeImageView
     */
    void onAttached(ShapeImageView view);

    /**
     * 解绑ShapeImageView
     *
     * @param view ShapeImageView
     */
    void onDetached(ShapeImageView view);
}
