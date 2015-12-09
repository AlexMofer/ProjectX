package com.am.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.am.widget.citylistview.CitysListView.BaseCitysAdapter;
import com.am.widget.citylistview.CitysUtils.CityDto;


public class MyCitysAdapter extends BaseCitysAdapter {


	private Context context;
	private int textViewResourceId;

	public MyCitysAdapter(Context context, int textViewResourceId) {
		this.context = context;
		this.textViewResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(textViewResourceId, parent, false);
		}
		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		CityDto country = getItem(position);
		textView.setText(country.getCityName());
		return view;
	}
}
