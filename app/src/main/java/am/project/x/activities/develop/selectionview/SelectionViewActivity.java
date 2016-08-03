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

public class SelectionViewActivity extends BaseActivity implements Selection,
        SelectionView.OnSelectedListener {

    private static final String[] STR_DATA = {"\u2606", "A", "B", "C", "D", "E", "F",
            "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "W",
            "X", "Y", "Z",};
    private ArrayList<Drawable> mDrawables = new ArrayList<>();
    private ArrayList<Drawable> mDrawableNotices = new ArrayList<>();
    private ListView listView;
    private CitiesAdapter adapter;

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.dev_activity_selectionview;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.selection_toolbar);
        listView = (ListView) findViewById(R.id.list);
        SelectionView selection = (SelectionView) findViewById(R.id.selection);
        adapter = new CitiesAdapter(this, android.R.layout.simple_list_item_2);
        listView.setAdapter(adapter);
        for (int i = 0; i < 23; i++) {
            mDrawables.add(new TextDrawable(
                    getApplicationContext(), 36, 0xff000000, STR_DATA[i % STR_DATA.length]));
            mDrawableNotices.add(new TextDrawable(
                    getApplicationContext(), 60, 0xffffffff, STR_DATA[i % STR_DATA.length]));
        }
        selection.setSelection(this);
        selection.setOnSelectedListener(this);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SelectionViewActivity.class));
    }

    @Override
    public int getItemCount() {
        return 23;
    }

    @Override
    public Drawable getBar(int position) {
        if (mDrawables.size() > position) {
            return mDrawables.get(position);
        }
        return null;
    }

    @Override
    public Drawable getNotice(int position) {
        if (mDrawableNotices.size() > position) {
            return mDrawableNotices.get(position);
        }
        return null;
    }

    @Override
    public void onSelected(int position) {
        listView.setSelection(adapter.getSelection(position));
    }
}
