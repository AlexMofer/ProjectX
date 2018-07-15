package am.project.x.activities.main.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import am.project.x.R;
import am.widget.gradienttabstrip.GradientTabStrip;

public class TabStripAdapter extends GradientTabStrip.Adapter {

    private final Drawable mNormalDevelop;
    private final Drawable mNormalWidgets;
    private final Drawable mNormalDrawables;
    private final Drawable mNormalOthers;
    private final Drawable mSelectedDevelop;
    private final Drawable mSelectedWidgets;
    private final Drawable mSelectedDrawables;
    private final Drawable mSelectedOthers;

    public TabStripAdapter(Context context) {
        mNormalDevelop = ContextCompat.getDrawable(context, R.drawable.ic_main_develop_normal);
        mNormalWidgets = ContextCompat.getDrawable(context, R.drawable.ic_main_widgets_normal);
        mNormalDrawables = ContextCompat.getDrawable(context, R.drawable.ic_main_drawables_normal);
        mNormalOthers = ContextCompat.getDrawable(context, R.drawable.ic_main_others_normal);
        mSelectedDevelop = ContextCompat.getDrawable(context, R.drawable.ic_main_develop_selected);
        mSelectedWidgets = ContextCompat.getDrawable(context, R.drawable.ic_main_widgets_selected);
        mSelectedDrawables = ContextCompat.getDrawable(context, R.drawable.ic_main_drawables_selected);
        mSelectedOthers = ContextCompat.getDrawable(context, R.drawable.ic_main_others_selected);
    }

    @Nullable
    @Override
    public Drawable getDrawableNormal(int position, int count) {
        switch (position) {
            default:
            case 0:
                return mNormalDevelop;
            case 1:
                return mNormalWidgets;
            case 2:
                return mNormalDrawables;
            case 3:
                return mNormalOthers;
        }
    }

    @Nullable
    @Override
    public Drawable getDrawableSelected(int position, int count) {
        switch (position) {
            default:
            case 0:
                return mSelectedDevelop;
            case 1:
                return mSelectedWidgets;
            case 2:
                return mSelectedDrawables;
            case 3:
                return mSelectedOthers;
        }
    }
}
