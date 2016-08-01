package am.project.x.activities.develop.selectionview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import am.drawable.TextDrawable;
import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.selectionview.Selection;
import am.widget.selectionview.SelectionView;

public class SelectionViewActivity extends BaseActivity implements Selection {

    private static final String[] STR_DATA = {"\u2606", "A", "B", "C", "D", "E", "F",
            "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "W",
            "X", "Y", "Z",};
    private ArrayList<Drawable> mDrawables = new ArrayList<>();
    private ArrayList<Drawable> mDrawableNotices = new ArrayList<>();

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.dev_activity_selectionview;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.selection_toolbar);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new MyCitysAdapter(this, android.R.layout.simple_list_item_2));
        SelectionView selection = (SelectionView) findViewById(R.id.selection);
        selection.setSelection(this);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SelectionViewActivity.class));
    }

    @Override
    public int getItemCount() {
        return 23;
    }

    @Override
    public Drawable getItemBar(int position) {
        if (mDrawables.size() > position) {
            return mDrawables.get(position);
        }
        TextDrawable drawable = new TextDrawable(
                getApplicationContext(), 36, 0xff000000, STR_DATA[position % STR_DATA.length]);
        mDrawables.add(drawable);
        return drawable;
    }

    @Override
    public Drawable getItemNotice(int position) {
        if (mDrawableNotices.size() > position) {
            return mDrawableNotices.get(position);
        }
        TextDrawable drawable = new TextDrawable(
                getApplicationContext(), 60, 0xffffffff, STR_DATA[position % STR_DATA.length]);
        mDrawableNotices.add(drawable);
        return drawable;
    }
}
