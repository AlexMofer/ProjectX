package io.github.alexmofer.android.support.widget;

import android.view.View;

import androidx.annotation.NonNull;

import io.github.alexmofer.android.support.R;

/**
 * 屏蔽 ClickableSpan 点击事件
 * Created by Alex on 2026/1/22.
 */
public final class ClickableSpanViewOnClickListener implements View.OnClickListener {

    private final View.OnClickListener mListener;

    public ClickableSpanViewOnClickListener(@NonNull View.OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        final int id = R.id.support_v_tag_clickable_span;
        final Object tag = v.getTag(id);
        if (tag instanceof ClickableSpanClicker) {
            v.setTag(id, null);
            return;
        }
        mListener.onClick(v);
    }
}
