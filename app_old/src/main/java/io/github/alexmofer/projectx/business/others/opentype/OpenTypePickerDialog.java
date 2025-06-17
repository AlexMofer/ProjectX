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
package io.github.alexmofer.projectx.business.others.opentype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import io.github.alexmofer.projectx.R;
import androidx.appcompat.app.AlertDialog;

/**
 * 字体选择对话框
 */
class OpenTypePickerDialog extends AlertDialog implements AdapterView.OnItemClickListener {

    private final Adapter mAdapter;
    private final OnPickerListener mListener;

    OpenTypePickerDialog(Context context, OpenTypePickerViewModel model,
                         OnPickerListener listener) {
        super(context);
        mAdapter = new Adapter(model);
        mListener = listener;
        setTitle(R.string.ot_title_picker);
        final ListView content = (ListView) View.inflate(context, R.layout.dlg_opentype_picker,
                null);
        content.setAdapter(mAdapter);
        content.setOnItemClickListener(this);
        setView(content);
    }

    void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    // Listener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onItemPicked(position);
    }

    public interface OnPickerListener {
        void onItemPicked(int position);
    }

    private class Adapter extends BaseAdapter {

        private final OpenTypePickerViewModel mModel;

        Adapter(OpenTypePickerViewModel model) {
            mModel = model;
        }

        @Override
        public int getCount() {
            return mModel.getSubCount();
        }

        @Override
        public Object getItem(int position) {
            return mModel.getSubItem(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_opentype_font, parent, false);
            }
            (((TextView) convertView)).setText(mModel.getSubName(getItem(position)));
            return convertView;
        }
    }
}
