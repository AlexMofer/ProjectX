package io.github.alexmofer.android.support.widget;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import io.github.alexmofer.android.support.R;

/**
 * 可点击的
 * Created by Alex on 2026/1/22.
 */
public final class ClickableSpanClicker extends ClickableSpan {

    private final View.OnClickListener mListener;

    public ClickableSpanClicker(@NonNull View.OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.setColor(ds.linkColor);
    }

    @Override
    public void onClick(@NonNull View widget) {
        widget.setTag(R.id.support_v_tag_clickable_span, this);
        mListener.onClick(widget);
    }
}