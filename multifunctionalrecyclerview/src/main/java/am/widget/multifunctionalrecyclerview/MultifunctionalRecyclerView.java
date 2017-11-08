/*
 * Copyright (C) 2017 AlexMofer
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

package am.widget.multifunctionalrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import am.widget.scalerecyclerview.ScaleRecyclerView;

/**
 * 多功能RecyclerView
 * Created by Alex on 2017/11/9.
 */
@SuppressWarnings("unused")
public class MultifunctionalRecyclerView extends ScaleRecyclerView {

    public MultifunctionalRecyclerView(Context context) {
        super(context);
    }

    public MultifunctionalRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MultifunctionalRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 内容提供者
     *
     * @param <VH> 视图持有者
     */
    public static abstract class Adapter<VH extends ViewHolder> extends
            ScaleRecyclerView.Adapter<VH> {
    }

    /**
     * 视图持有者
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
