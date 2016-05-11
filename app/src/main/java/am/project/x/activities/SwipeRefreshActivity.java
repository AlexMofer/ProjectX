package am.project.x.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.project.x.R;

public class SwipeRefreshActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_swiperefresh);
		List<HashMap<String, Object>> dataSourceList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
			itemHashMap.put("item_image", R.drawable.ic_swipelayout);
			itemHashMap.put("item_text", "Item " + Integer.toString(i));
			dataSourceList.add(itemHashMap);
		}
		final SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
				dataSourceList, R.layout.supergridview_item_griditem, new String[] {
						"item_image", "item_text" }, new int[] {
						R.id.item_image, R.id.item_text });
		((ListView)findViewById(R.id.list)).setAdapter(mSimpleAdapter);
	}
}
