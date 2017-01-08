package am.widget.shapeimageview;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;

/**
 * 形状
 * Created by Alex on 2015/12/9.
 */
public abstract class ImageShape {

    /**
     * ClipPath模式下绘制边框
     *
     * @param view   View
     * @param canvas 画布
     * @param paint  边框绘制画笔
     */
    public abstract void drawBorder(ShapeImageView view, Canvas canvas, Paint paint);

    /**
     * PorterDuff模式下处理形状
     *
     * @param view   ImageView
     * @param canvas 画布
     * @param paint  画笔
     */
    public abstract void makeShapeByPorterDuff(ShapeImageView view, Canvas canvas, Paint paint);

    /**
     * ClipPath模式下处理形状
     *
     * @param view ImageView
     * @param path 路径
     */
    public abstract void makeShapeByClipPath(ShapeImageView view, Path path);

    /**
     * Outline模式下处理形状
     *
     * @param view    View
     * @param outline Outline
     */
    @TargetApi(21)
    public abstract void makeShapeByOutline(ShapeImageView view, Outline outline);

    /**
     * 判断是否可使用Outline方式
     * 强制打开会导致低版本上报错
     *
     * @return 是否可使用Outline方式
     */
    protected boolean isOutlineEnable() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

    /**
     * 判断是否可使用画布裁剪方式
     * 可强制打开，但低版本上需要关闭硬件加速，但部分机型在关闭硬件加速的前提下依然无效
     *
     * @return 是否可使用画布裁剪方式
     */
    protected boolean isClipPathEnable() {
        return android.os.Build.VERSION.SDK_INT >= 18;
    }
}
