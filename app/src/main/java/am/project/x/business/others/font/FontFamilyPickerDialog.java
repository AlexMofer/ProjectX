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

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import am.project.x.R;

/**
 * 字体家族名选择对话框
 */
class FontFamilyPickerDialog extends AlertDialog implements AdapterView.OnItemClickListener {

    private final FontFamilyPickerViewModel mModel;
    private final Adapter mAdapter;
    private final TextView mMessage;
    private final OnPickerListener mListener;

    FontFamilyPickerDialog(Context context, FontFamilyPickerViewModel model,
                           OnPickerListener listener) {
        super(context);
        mModel = model;
        mAdapter = new Adapter(model);
        mListener = listener;
        setTitle(R.string.font_picker_title);
        final View content = View.inflate(context, R.layout.dlg_font_picker, null);
        mMessage = content.findViewById(R.id.dfp_tv_message);
        final ListView names = content.findViewById(R.id.dfp_lv_names);
        names.setAdapter(mAdapter);
        names.setOnItemClickListener(this);
        setView(content);
    }

    void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
        mMessage.setText(getContext().getString(R.string.font_picker_message,
                mModel.getDefaultFamilyName()));
    }

    // Listener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onItemPicked(mAdapter.getItem(position));
    }

    public interface OnPickerListener {
        void onItemPicked(String item);
    }

    private class Adapter extends BaseAdapter {

        private final FontFamilyPickerViewModel mModel;

        Adapter(FontFamilyPickerViewModel model) {
            mModel = model;
        }

        @Override
        public int getCount() {
            return mModel.getFamilyNameOrAliaCount();
        }

        @Override
        public String getItem(int position) {
            return mModel.getFamilyNameOrAlia(position);
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
            (((TextView) convertView)).setText(getItem(position));
            return convertView;
        }
    }
}
