package io.github.alexmofer.projectx.ui.builders;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.function.Consumer;

import io.github.alexmofer.android.support.utils.EditTextUtils;
import io.github.alexmofer.android.support.utils.InputMethodManagerUtils;

/**
 * EditText 构建器
 * Created by Alex on 2025/6/24.
 */
public class EditTextBuilder extends TextViewBuilder {

    private final EditText mView;

    public EditTextBuilder(EditText view) {
        super(view);
        this.mView = view;
    }

    public EditTextBuilder(Context context) {
        this(new AppCompatEditText(context));
        setBackgroundColor(Color.TRANSPARENT);
        setMinimumHeight(0);
        setMinHeight(0);
        setMinimumWidth(0);
        setMinWidth(0);
    }

    @Override
    public EditText build() {
        return this.mView;
    }

    public EditTextBuilder showSoftInputWhenAttached(long delayMillis) {
        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                v.postDelayed(() -> {
                    if (v.isAttachedToWindow()) {
                        InputMethodManagerUtils.showSoftInput(v);
                    }
                }, delayMillis);
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {

            }
        });
        return this;
    }

    public EditTextBuilder addOnTextChangedListener(Consumer<Editable> consumer) {
        EditTextUtils.addOnTextChangedListener(mView, consumer);
        return this;
    }

    public EditTextBuilder setOnEditorActionListener(TextView.OnEditorActionListener l) {
        mView.setOnEditorActionListener(l);
        return this;
    }
}
