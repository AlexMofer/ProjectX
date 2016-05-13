package am.project.x.activities.main.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;

import am.project.x.R;
import am.project.x.activities.main.fragments.DevelopFragment;
import am.project.x.activities.main.fragments.DrawablesFragment;
import am.project.x.activities.main.fragments.OthersFragment;
import am.project.x.activities.main.fragments.WidgetsFragment;
import am.project.x.widgets.tabstrips.GradientTabStrip;

/**
 * 主页Adapter
 * Created by Alex on 2016/5/13.
 */
public class MainPagerAdapter extends FragmentPagerAdapter implements
        GradientTabStrip.GradientTabAdapter {

    private Context context;
    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
            case 0:
                return DevelopFragment.newInstance();
            case 1:
                return WidgetsFragment.newInstance();
            case 2:
                return DrawablesFragment.newInstance();
            case 3:
                return OthersFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            default:
            case 0:
                return context.getString(R.string.main_tab_develop);
            case 1:
                return context.getString(R.string.main_tab_widgets);
            case 2:
                return context.getString(R.string.main_tab_drawables);
            case 3:
                return context.getString(R.string.main_tab_others);
        }
    }

    @Override
    public Drawable getNormalDrawable(int position, Context context) {
        switch (position) {
            default:
            case 0:
                return ContextCompat.getDrawable(context, R.drawable.ic_main_develop_normal);
            case 1:
                return ContextCompat.getDrawable(context, R.drawable.ic_main_widgets_normal);
            case 2:
                return ContextCompat.getDrawable(context, R.drawable.ic_main_drawables_normal);
            case 3:
                return ContextCompat.getDrawable(context, R.drawable.ic_main_others_normal);
        }
    }

    @Override
    public Drawable getSelectedDrawable(int position, Context context) {
        switch (position) {
            default:
            case 0:
                return ContextCompat.getDrawable(context, R.drawable.ic_main_develop_selected);
            case 1:
                return ContextCompat.getDrawable(context, R.drawable.ic_main_widgets_selected);
            case 2:
                return ContextCompat.getDrawable(context, R.drawable.ic_main_drawables_selected);
            case 3:
                return ContextCompat.getDrawable(context, R.drawable.ic_main_others_selected);
        }
    }

    @Override
    public boolean isTagEnable(int position) {
        return false;
    }

    @Override
    public String getTag(int position) {
        return null;
    }
}
