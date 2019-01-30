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

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter
 */
class FontAdapter extends RecyclerView.Adapter<FontViewHolder> {

    private final FontAdapterViewModel mModel;
    private final FontViewHolder.OnViewHolderListener mListener;

    FontAdapter(FontAdapterViewModel model, FontViewHolder.OnViewHolderListener listener) {
        mModel = model;
        mListener = listener;
    }

    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new FontViewHolder(parent, getItemViewType(position), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FontViewHolder holder, int position) {
        if (position == 0)
            holder.setName(mModel.getTypefaceName());
        else if (position == 1)
            holder.setCommon(mModel);
        else
            holder.setFallback(mModel, position - 2);
    }

    @Override
    public int getItemCount() {
        final int count = mModel.getTypefaceFallbackCount();
        return count == 0 ? 0 : count + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return FontViewHolder.TYPE_NAME;
        return FontViewHolder.TYPE_ITEM;
    }
}
