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
package am.widget.floatingactionmode;

import android.view.View;

/**
 * 次级菜单
 * Created by Alex on 2018/10/24.
 */
public interface FloatingSubMenu extends FloatingMenu {

    FloatingSubMenu setTitle(CharSequence title);

    CharSequence getTitle();

    FloatingSubMenu setTitle(int title);

    boolean isCustomMenu();

    View getCustomView();

    FloatingSubMenu setCustomView(View view);

    interface OnAttachStateChangeListener {

        void onViewAttachedToFloatingActionMode(FloatingActionMode mode);

        void onViewDetachedFromFloatingActionMode(FloatingActionMode mode);
    }
}
