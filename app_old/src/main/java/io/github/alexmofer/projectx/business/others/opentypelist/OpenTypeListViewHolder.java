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
package io.github.alexmofer.projectx.business.others.opentypelist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.alexmofer.projectx.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder
 */
class OpenTypeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView mVName;
    private final OnViewHolderListener mListener;
    private Object mItem;

    OpenTypeListViewHolder(@NonNull ViewGroup parent, OnViewHolderListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_opentypelist_item, parent, false));
        mListener = listener;
        mVName = (TextView) itemView;
        itemView.setOnClickListener(this);
    }

    void bind(int position, OpenTypeListAdapterViewModel model) {
        mItem = model.getItem(position);
        mVName.setText(model.getItemName(mItem));
    }

    @Override
    public void onClick(View v) {
        mListener.onItemClick(mItem);
    }

    public interface OnViewHolderListener {
        void onItemClick(Object item);
    }
}
