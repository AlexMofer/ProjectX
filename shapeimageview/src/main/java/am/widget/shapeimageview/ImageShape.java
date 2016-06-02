package am.widget.shapeimageview;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;

/**
 * 形状
 * Created by Alex on 2015/12/9.
 */
public abstract class ImageShape {


    private ArrayList<ShapeImageView> views = new ArrayList<>();


    /**
     * 低版本下的形状处理
     *
     * @param view   ImageView
     * @param canvas 画布
     * @param paint  画笔
     */
    public abstract void makeShapeBase(ShapeImageView view, Canvas canvas, Paint paint);

    /**
     * 低版本下的绘制边框
     *
     * @param view   View
     * @param canvas 画布
     * @param width  边框宽度
     * @param paint  边框绘制画笔
     */
    public abstract void drawBorderBase(ShapeImageView view, Canvas canvas, float width, Paint paint);

    /**
     * Lollipop 下的形状处理
     *
     * @param view    View
     * @param outline Outline
     */
    @TargetApi(21)
    public abstract void makeShapeLollipop(ShapeImageView view, Outline outline);

    /**
     * Lollipop 下的绘制边框
     *
     * @param view   View
     * @param canvas 画布
     * @param width  边框宽度
     * @param paint  边框绘制画笔
     */
    public abstract void drawBorderLollipop(ShapeImageView view, Canvas canvas, float width, Paint paint);

    /**
     * 始终使用基础方法
     *
     * @return 默认关闭
     */
    public boolean alwaysUseBaseWay() {
        return false;
    }

    /**
     * 捆绑ShapeImageView
     *
     * @param view ShapeImageView
     */
    void onAttached(ShapeImageView view) {
        views.add(view);
    }

    /**
     * 解绑ShapeImageView
     *
     * @param view ShapeImageView
     */
    void onDetached(ShapeImageView view) {
        views.remove(view);
    }

    /**
     * 通知View变化
     */
    public void notifyViewSetChanged() {
        for (View view : views) {
            if (canUseLollipopWay()) {
                ShapeCompat.invalidateOutline(view);
                view.invalidate();
            } else {
                view.invalidate();
            }
        }
    }

    /**
     * 判断是否可使用Lollipop方式
     *
     * @return 是否可使用Lollipop方式
     */
    protected boolean canUseLollipopWay() {
        return !alwaysUseBaseWay() && android.os.Build.VERSION.SDK_INT >= 21;
    }

}
