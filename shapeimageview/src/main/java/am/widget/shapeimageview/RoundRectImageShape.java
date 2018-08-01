/*
 * Copyright (C) 2015 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package am.widget.shapeimageview;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;


/**
 * 圆角矩形
 */
@SuppressWarnings("all")
public class RoundRectImageShape extends ImageShape {

    private static final RectF tRectF = new RectF();

    @Override
    public void drawBorder(ShapeImageView view, Canvas canvas, Paint paint) {
        tRectF.set(0, 0, view.getWidth(), view.getHeight());
        canvas.drawRoundRect(tRectF, view.getRoundRectRadius(),
                view.getRoundRectRadius(), paint);
    }

    @Override
    public void makeShapeByPorterDuff(ShapeImageView view, Canvas canvas, Paint paint) {
        tRectF.set(0, 0, view.getWidth(), view.getHeight());
        canvas.drawRoundRect(tRectF, view.getRoundRectRadius(), view.getRoundRectRadius(), paint);
    }

    @Override
    public void makeShapeByClipPath(ShapeImageView view, Path path) {
        tRectF.set(0, 0, view.getWidth(), view.getHeight());
        path.addRoundRect(tRectF, view.getRoundRectRadius(), view.getRoundRectRadius(),
                Path.Direction.CW);
    }

    @Override
    @TargetApi(21)
    public void makeShapeByOutline(ShapeImageView view, Outline outline) {
        outline.setRoundRect(0, 0, view.getMeasuredWidth(),
                view.getMeasuredHeight(), view.getRoundRectRadius());
    }
}
