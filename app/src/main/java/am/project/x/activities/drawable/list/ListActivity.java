package am.project.x.activities.drawable.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class ListActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_list;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.list_toolbar);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ListActivity.class));
    }
}
