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
package am.project.x.business.others.opentypelist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Adapter
 */
class OpenTypeListAdapter extends RecyclerView.Adapter<OpenTypeListViewHolder> {

    private final OpenTypeListAdapterViewModel mModel;
    private final OpenTypeListViewHolder.OnViewHolderListener mListener;

    OpenTypeListAdapter(OpenTypeListAdapterViewModel model,
                        OpenTypeListViewHolder.OnViewHolderListener listener) {
        mModel = model;
        mListener = listener;
    }

    @NonNull
    @Override
    public OpenTypeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new OpenTypeListViewHolder(parent, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OpenTypeListViewHolder holder, int position) {
        holder.bind(position, mModel);
    }

    @Override
    public int getItemCount() {
        return mModel.getItemCount();
    }
}
