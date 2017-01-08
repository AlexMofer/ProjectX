package am.widget.shapeimageview;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * 工具
 * Created by Alex on 2017/1/7.
 */

class Utils {

    static Bitmap createBitmap(Bitmap bitmap, int width, int height) {
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            return createBitmapKitkat(bitmap, width, height);
        }
        if (bitmap == null) {
            try {
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError e) {
                return null;
            }
        } else {
            if (bitmap.getWidth() < width || bitmap.getHeight() < height) {
                bitmap.recycle();
                try {
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError e) {
                    return null;
                } finally {
                    System.gc();
                }
            } else {
                bitmap.eraseColor(Color.TRANSPARENT);
            }
        }
        return bitmap;
    }

    @TargetApi(19)
    private static Bitmap createBitmapKitkat(Bitmap bitmap, int width, int height) {
        if (bitmap == null) {
            try {
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError e) {
                return null;
            }
        } else {
            if (bitmap.getWidth() < width || bitmap.getHeight() < height) {
                bitmap.eraseColor(Color.TRANSPARENT);
                try {
                    bitmap.reconfigure(width, height, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError e) {
                    bitmap.recycle();
                    return null;
                } finally {
                    System.gc();
                }
            } else {
                bitmap.eraseColor(Color.TRANSPARENT);
            }
        }
        return bitmap;
    }
}
