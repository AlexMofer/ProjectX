package am.project.x.activities.develop.selectionview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ListView;

import am.drawable.TextDrawable;
import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.selectionview.Selection;
import am.widget.selectionview.SelectionView;

public class SelectionViewActivity extends BaseActivity implements Selection,
        SelectionView.OnSelectedListener {

    private static final String[] STR_DATA = {"\u2606", "A", "B", "C", "D", "E", "F", "G", "H", "J",
            "K", "L", "M", "N", "P", "Q", "R", "S", "T", "W", "X", "Y", "Z",};
    private ListView listView;
    private CitiesAdapter adapter;
    private TextDrawable drawableBar;
    private TextDrawable drawableNotice;

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
        drawableBar = new TextDrawable(getApplicationContext(), 36, 0xff000000, "");
        drawableNotice = new TextDrawable(getApplicationContext(), 60, 0xffffffff, "");
        selection.setSelection(this);
        selection.setOnSelectedListener(this);

        selection.setBarStyle(SelectionView.STYLE_SLIDER);
        selection.setNoticeBackground(R.drawable.bg_selection_notice_slider);
        selection.setNoticeLocation(SelectionView.LOCATION_SLIDER_TOP);
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
        drawableBar.setText(STR_DATA[position % STR_DATA.length]);
        return drawableBar;
    }

    @Override
    public Drawable getNotice(int position) {
        drawableNotice.setText(STR_DATA[position % STR_DATA.length]);
        return drawableNotice;
    }

    @Override
    public void onSelected(int position) {
        listView.setSelection(adapter.getSelection(position));
    }
}
