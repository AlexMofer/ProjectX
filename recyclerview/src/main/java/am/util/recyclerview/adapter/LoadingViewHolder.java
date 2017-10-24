/*
 * Copyright (C) 2015 AlexMofer
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

package am.util.recyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 尾部载入的ViewHolder
 * Created by Alex on 2016/12/27.
 */
@SuppressWarnings("all")
public abstract class LoadingViewHolder<T> extends RecyclerView.ViewHolder {

    public static final int NORMAL = 0;
    public static final int LOADING = 1;
    protected final int viewType;
    protected T data;

    public LoadingViewHolder(View itemView, int viewType) {
        super(itemView);
        this.viewType = viewType;
    }

    public void bindNormal(T data) {
        if (viewType != NORMAL)
            return;
        this.data = data;
        onBindNormal(data);
    }

    protected abstract void onBindNormal(T data);

    public void bindLoading(boolean error) {
        if (viewType != LOADING)
            return;
        onBindLoading(error);
    }

    protected abstract void onBindLoading(boolean error);

}