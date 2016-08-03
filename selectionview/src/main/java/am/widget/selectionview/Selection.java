package am.widget.selectionview;

import android.graphics.drawable.Drawable;

/**
 * 选择器
 * Created by Alex on 2016/7/29.
 */
public interface Selection {

    int getItemCount();

    Drawable getBar(int position);

    Drawable getNotice(int position);
}
