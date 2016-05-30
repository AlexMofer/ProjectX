package android.support.v4.widget;

import android.content.Context;

/**
 * ProgressImageView
 *
 * @see android.support.v4.widget.CircleImageView
 */
public class ProgressImageView extends CircleImageView {

    private static final int DEFAULT_COLOR = 0xFFFAFAFA;
    private static final int DEFAULT_RADIUS = 10;

    public ProgressImageView(Context context) {
        this(context, DEFAULT_COLOR,
                (int) (context.getResources().getDisplayMetrics().density * DEFAULT_RADIUS));
    }

    public ProgressImageView(Context context, int color, float radius) {
        super(context, color, radius);
    }
}
