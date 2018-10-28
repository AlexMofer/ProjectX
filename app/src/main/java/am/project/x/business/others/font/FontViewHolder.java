/*
 * Copyright (C) 2018 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.project.x.business.others.font;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import am.project.x.R;

/**
 * ViewHolder
 */
class FontViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    static final int TYPE_NAME = 0;
    static final int TYPE_ITEM = 1;
    private final OnViewHolderListener mListener;
    private TextView mVName;
    private TextView mVTitle;
    private ViewGroup mVItems;

    FontViewHolder(@NonNull ViewGroup parent, int type, OnViewHolderListener listener) {
        super(LayoutInflater.from(parent.getContext())
                .inflate((type == TYPE_NAME ? R.layout.item_font_name : R.layout.item_font_item),
                        parent, false));
        mListener = listener;
        if (type == TYPE_NAME) {
            mVName = (TextView) itemView;
        } else {
            mVTitle = itemView.findViewById(R.id.ifi_tv_title);
            mVItems = itemView.findViewById(R.id.ifi_lyt_items);
        }
    }

    void setName(String name) {
        if (getItemViewType() != TYPE_NAME)
            return;
        mVName.setText(name);
    }

    void setCommon(FontAdapterViewModel model) {
        mVTitle.setText(model.getCommonTitle());
        final int commonItemCount = model.getCommonItemCount();
        for (int i = 0; i < commonItemCount; i++) {
            setItem(model, model.getCommonItem(i), i + 1);
        }
        hideItem(commonItemCount + 1);
    }

    void setFallback(FontAdapterViewModel model, int position) {
        final Object fallback = model.getFallback(position);
        mVTitle.setText(model.getFallbackTitle(fallback));
        final int fallbackItemCount = model.getFallbackItemCount(fallback);
        for (int i = 0; i < fallbackItemCount; i++) {
            setItem(model, model.getFallbackItem(fallback, i), i + 1);
        }
        hideItem(fallbackItemCount + 1);
    }

    private void setItem(FontAdapterViewModel model, Object item, int index) {
        View child = mVItems.getChildAt(index);
        if (child == null) {
            child = LayoutInflater.from(mVItems.getContext()).inflate(R.layout.item_font_item_child,
                    mVItems, false);
            mVItems.addView(child);
            child.setOnClickListener(this);
        }
        child.setVisibility(View.VISIBLE);
        child.setTag(item);
        ((TextView) child.findViewById(R.id.fic_tv_name)).setText(model.getTypefaceItemName(item));
        ((TextView) child.findViewById(R.id.fic_tv_info)).setText(model.getTypefaceItemInfo(item));
    }

    private void hideItem(int index) {
        final int itemCount = mVItems.getChildCount();
        if (itemCount <= index)
            return;
        for (int i = index; i < itemCount; i++) {
            final View child = mVItems.getChildAt(i);
            if (child != null) {
                child.setVisibility(View.GONE);
                child.setTag(null);
            }
        }
    }

    // Listener
    @Override
    public void onClick(View v) {
        mListener.onItemClick(v.getTag());
    }

    public interface OnViewHolderListener {
        void onItemClick(Object item);
    }
}
