package com.am.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.am.widget.R;
import com.am.widget.supergridview.DragController;
import com.am.widget.supergridview.SuperGridView;
import com.am.widget.supergridview.SuperGridView.DeleteAnimator;
import com.am.widget.supergridview.SuperGridView.OnDragListener;

public class DragGridViewActivity extends Activity {

	private DragGridViewActivity me = this;
	private List<HashMap<String, Object>> dataSourceList = new ArrayList<HashMap<String, Object>>();
	private List<HashMap<String, Object>> dataSourceListNew = new ArrayList<HashMap<String, Object>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draggridview);
		final SuperGridView mGridView = (SuperGridView) findViewById(R.id.superGridView);
		for (int i = 0; i < 94; i++) {
			HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
			itemHashMap.put("item_image", R.drawable.ic_launcher);
			itemHashMap.put("item_text", "Item " + Integer.toString(i));
			dataSourceList.add(itemHashMap);
		}

		for (int i = 0; i < 34; i++) {
			HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
			itemHashMap.put("item_image", R.drawable.ic_launcher);
			itemHashMap.put("item_text", "New " + Integer.toString(i));
			dataSourceListNew.add(itemHashMap);
		}
		List<HashMap<String, Object>> hAf = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> h1 = new HashMap<String, Object>();
		h1.put("item_image", R.drawable.ic_launcher);
		h1.put("item_text", "Header 1");
		HashMap<String, Object> h2 = new HashMap<String, Object>();
		h2.put("item_image", R.drawable.ic_launcher);
		h2.put("item_text", "Header 2");
		HashMap<String, Object> f1 = new HashMap<String, Object>();
		f1.put("item_image", R.drawable.ic_launcher);
		f1.put("item_text", "Footer 1");
		HashMap<String, Object> f2 = new HashMap<String, Object>();
		f2.put("item_image", R.drawable.ic_launcher);
		f2.put("item_text", "Footer 2");
		hAf.add(h1);
		hAf.add(h2);
		hAf.add(f1);
		hAf.add(f2);
		final SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
				dataSourceList, R.layout.supergridview_item_griditem, new String[] {
						"item_image", "item_text" }, new int[] {
						R.id.item_image, R.id.item_text });
		final SimpleAdapter mSimpleAdapterNew = new SimpleAdapter(this,
				dataSourceListNew, R.layout.supergridview_item_griditem, new String[] {
						"item_image", "item_text" }, new int[] {
						R.id.item_image, R.id.item_text });

		final SimpleAdapter hAfA = new SimpleAdapter(this, hAf,
				R.layout.supergridview_item_griditem, new String[] { "item_image", "item_text" },
				new int[] { R.id.item_image, R.id.item_text });
		final View viewHeaderItem1 = hAfA.getView(0, null, null);
		final View viewHeaderItem2 = hAfA.getView(1, null, null);
		final View header = View.inflate(me, R.layout.supergridview_item_header, null);
		((Button) header.findViewById(R.id.button1)).setText("Add HeaderItem");
		((Button) header.findViewById(R.id.button2)).setText("Set Adapter");
		viewHeaderItem1.setOnClickListener(new OnClickListener() {
			private boolean once = true;

			@Override
			public void onClick(View v) {
				if (once) {
					once = false;
					mGridView.removeHeaderView(header);
				} else {
					once = true;
					mGridView.addHeaderView(header);
				}
			}
		});
		header.findViewById(R.id.button1).setOnClickListener(
				new OnClickListener() {
					private boolean once = false;

					@Override
					public void onClick(View v) {
						if (once) {
							once = false;
							mGridView.removeHeaderItem(viewHeaderItem2);
							((Button) v).setText("Add HeaderItem");
						} else {
							once = true;
							mGridView
									.addHeaderItem(viewHeaderItem2, null, true);
							((Button) v).setText("Remove HeaderItem");
						}

					}
				});
		header.findViewById(R.id.button2).setOnClickListener(
				new OnClickListener() {
					private boolean once = true;

					@Override
					public void onClick(View v) {
						if (once) {
							once = false;
							mGridView.setAdapter(mSimpleAdapterNew);
							mGridView.setDragable(false);
						} else {
							once = true;
							mGridView.setAdapter(mSimpleAdapter);
							mGridView.setDragable(true);
						}
					}
				});
		final View viewFooterItem1 = hAfA.getView(2, null, null);
		final View viewFooterItem2 = hAfA.getView(3, null, null);
		final View footer = View.inflate(me, R.layout.supergridview_item_header, null);
		((Button) footer.findViewById(R.id.button1)).setText("Add FooterItem");
		((Button) footer.findViewById(R.id.button2)).setText("Change Adapter");
		viewFooterItem1.setOnClickListener(new OnClickListener() {
			private boolean once = true;

			@Override
			public void onClick(View v) {
				if (once) {
					once = false;
					mGridView.removeFooterView(footer);
				} else {
					once = true;
					mGridView.addFooterView(footer);
				}
			}
		});
		footer.findViewById(R.id.button1).setOnClickListener(
				new OnClickListener() {
					private boolean once = false;

					@Override
					public void onClick(View v) {
						if (once) {
							once = false;
							mGridView.removeFooterItem(viewFooterItem2);
							((Button) v).setText("Add FooterItem");
						} else {
							once = true;
							mGridView
									.addFooterItem(viewFooterItem2, null, true);
							((Button) v).setText("Remove FooterItem");
						}

					}
				});

		footer.findViewById(R.id.button2).setOnClickListener(
				new OnClickListener() {
					private boolean once = true;

					@Override
					public void onClick(View v) {
						if (once) {
							once = false;
							dataSourceList.remove(dataSourceList.size() - 1);
							mSimpleAdapter.notifyDataSetChanged();
						} else {
							once = true;
							HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
							itemHashMap.put("item_image",
									R.drawable.ic_launcher);
							itemHashMap.put(
									"item_text",
									"Item "
											+ Integer.toString(dataSourceList
													.size()));
							dataSourceList.add(itemHashMap);
							mSimpleAdapter.notifyDataSetChanged();
						}
					}
				});
		mGridView.addHeaderItem(viewHeaderItem1);
		mGridView.addFooterItem(viewFooterItem1);
		mGridView.addHeaderView(header);
		//mGridView.addFooterView(footer);
		mGridView.setAdapter(mSimpleAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(me, "Click" + position, Toast.LENGTH_SHORT)
						.show();
			}
		});
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(me, "LongClick" + position, Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		});
		mGridView.setOnDragListener(new OnDragListener() {

			@Override
			public void onMerge(int position, int toPoaition) {
				Toast.makeText(me, "合并" + position + "到" + toPoaition,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onDelete(int position,
					int fristWrappedAdapterItemPosition,
					DeleteAnimator deleteAnimator) {
				// 当添加HeaderView或HeaderItem后position有偏移
				deleteAnimator.delete();
				//deleteAnimator.deleteWithoutAnimation();
				//deleteAnimator.cancel();
				dataSourceList.remove(position
						- fristWrappedAdapterItemPosition);
				mSimpleAdapter.notifyDataSetChanged();
			}
		});
		mGridView.setDeleteResource(R.drawable.ic_delete);
		// 设置删除位置
		mGridView.setDeleteDrawableLocation(0);
		mGridView.setStartScale(1.2f);
		mGridView.setDuration(180);
		mGridView.setDragController(new DragController() {
			@Override
			public Bitmap setDragView(View tagView) {
				View text = tagView.findViewById(R.id.item_text);
				text.setVisibility(View.INVISIBLE);
				tagView.setDrawingCacheEnabled(true);
				Bitmap bitmap = Bitmap.createBitmap(tagView.getDrawingCache());
				tagView.destroyDrawingCache();
				text.setVisibility(View.VISIBLE);
				return bitmap;
			}
		});
	}
}
