package am.project.x.activities.develop.selectionview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import am.project.x.widgets.citylistview.CitysListView;
import am.project.x.widgets.citylistview.CitysUtils;


public class MyCitysAdapter extends CitysListView.BaseCitysAdapter {


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
		CitysUtils.CityDto country = getItem(position);
		textView.setText(country.getCityName());
		return view;
	}
}
