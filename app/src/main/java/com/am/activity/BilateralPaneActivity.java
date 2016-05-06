package com.am.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.am.widget.R;
import com.am.widget.bilateralpanelayout.BilateralPaneLayout;
import com.am.widget.viewpager.ViewsPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BilateralPaneActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bilateralpane);
		BilateralPaneLayout slidingPaneLayout = (BilateralPaneLayout) findViewById(R.id.sliding);
		ViewPager pagerR = (ViewPager) findViewById(R.id.pager_right);
		ViewPager pagerL = (ViewPager) findViewById(R.id.pager_left);
		View pager1 = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_bilateralpane_pager, pagerR, false);
		((TextView)pager1.findViewById(R.id.text)).setText("第一屏");
		pager1.setBackgroundColor(0xff00ddff);
		View pager2 = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_bilateralpane_pager, pagerR, false);
		((TextView)pager2.findViewById(R.id.text)).setText("第二屏");
		pager2.setBackgroundColor(0xffffbb33);
		View pager3 = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_bilateralpane_pager, pagerR, false);
		((TextView)pager3.findViewById(R.id.text)).setText("第三屏");
		pager3.setBackgroundColor(0xff99ea10);
		ArrayList<View> view = new ArrayList<View>();
		view.add(pager1);
		view.add(pager2);
		view.add(pager3);
		pagerR.setAdapter(new ViewsPagerAdapter(view));
		
		View pager4 = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_bilateralpane_pager, pagerL, false);
		((TextView)pager4.findViewById(R.id.text)).setText("第一屏");
		pager4.setBackgroundColor(0xff00ddff);
		View pager5 = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_bilateralpane_pager, pagerL, false);
		((TextView)pager5.findViewById(R.id.text)).setText("第二屏");
		pager5.setBackgroundColor(0xffffbb33);
		View pager6 = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_bilateralpane_pager, pagerL, false);
		((TextView)pager6.findViewById(R.id.text)).setText("第三屏");
		pager6.setBackgroundColor(0xff99ea10);
		ArrayList<View> viewL = new ArrayList<View>();
		viewL.add(pager4);
		viewL.add(pager5);
		viewL.add(pager6);
		pagerL.setAdapter(new ViewsPagerAdapter(viewL));
		slidingPaneLayout.addRightCheckView(pagerR);
		slidingPaneLayout.addLeftCheckView(pagerL);
		
		List<HashMap<String, Object>> dataSourceList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 95; i++) {
			HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
			itemHashMap.put("item_image", R.drawable.ic_bilateralpane);
			itemHashMap.put("item_text", "Item " + Integer.toString(i));
			dataSourceList.add(itemHashMap);
		}
		GridView mGridView = (GridView) findViewById(R.id.grid);
		SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
				dataSourceList, R.layout.supergridview_item_griditem, new String[] {
						"item_image", "item_text" }, new int[] {
						R.id.item_image, R.id.item_text });
		mGridView.setAdapter(mSimpleAdapter);
	}
}
