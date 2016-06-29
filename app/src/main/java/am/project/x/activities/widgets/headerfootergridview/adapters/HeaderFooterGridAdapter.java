package am.project.x.activities.widgets.headerfootergridview.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Locale;

import am.project.x.R;

/**
 * HeaderFooterGridView 容器
 * Created by Alex on 2016/6/28.
 */
public class HeaderFooterGridAdapter extends BaseAdapter {

    private int count;

    @Override
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Context context = parent.getContext();
        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_headerfootergridview_item, parent, false);
        }
        TextView tvItem = (TextView) view;
        tvItem.setText(String.format(Locale.getDefault(),
                context.getString(R.string.headerfootergridview_item), position + 1));
        return view;
    }
}
