package am.project.x.activities.develop.selectionview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class SelectionViewActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.dev_activity_selectionview;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.selection_toolbar);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new MyCitysAdapter(this, android.R.layout.simple_list_item_2));
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SelectionViewActivity.class));
    }
}
