package io.github.alexmofer.projectx.business.widgets.multifunctionalrecyclerview;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.am.widget.multifunctionalrecyclerview.MultifunctionalRecyclerView;

import java.util.Locale;


/**
 * ViewHolder
 * Created by Alex on 2017/10/18.
 */

class ViewHolder extends MultifunctionalRecyclerView.ViewHolder {

    private AppCompatTextView mVText;

    ViewHolder(ViewGroup parent) {
        super(new AppCompatTextView(parent.getContext()));
        mVText = (AppCompatTextView) itemView;
        mVText.setGravity(Gravity.CENTER);
        mVText.setBackgroundColor(0xff00dd00);
        mVText.setTextColor(0xffffffff);
    }

    static int getViewWidth(int position) {
        return 600 + position * 40;
    }

    static int getViewHeight(int position) {
        return 900 + position * 40;
    }

    private static float getTextSize(int position) {
        return 30 + position;
    }

    void bind(int position) {
        final int width = getViewWidth(position);
        final int height = getViewHeight(position);
        mVText.setMinimumWidth(width);
        mVText.setMinimumHeight(height);
        mVText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getTextSize(position));
        mVText.setText(String.format(Locale.getDefault(), "%d", position));
    }

    @Override
    public void setScale(float scale) {
        super.setScale(scale);
        final int position = getAdapterPosition();
        if (position == RecyclerView.NO_POSITION)
            return;
        final int width = Math.round(getViewWidth(position) * scale);
        final int height = Math.round(getViewHeight(position) * scale);
        mVText.setMinimumWidth(width);
        mVText.setMinimumHeight(height);
        mVText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getTextSize(position) * scale);
    }
}