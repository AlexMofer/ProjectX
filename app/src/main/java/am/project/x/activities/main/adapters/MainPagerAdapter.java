package am.project.x.activities.main.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import am.project.x.R;
import am.project.x.activities.main.fragments.DevelopFragment;
import am.project.x.activities.main.fragments.DrawablesFragment;
import am.project.x.activities.main.fragments.OthersFragment;
import am.project.x.activities.main.fragments.WidgetsFragment;

/**
 * 主页Adapter
 * Created by Alex on 2016/5/13.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

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
}
