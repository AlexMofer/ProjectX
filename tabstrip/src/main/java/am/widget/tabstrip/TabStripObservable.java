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

package am.widget.tabstrip;

import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

/**
 * 标签条Observable
 */
@SuppressWarnings("unused")
public class TabStripObservable {

    private final ArrayList<View> mObservers = new ArrayList<>();

    void registerObserver(View observer) {
        if (observer == null)
            return;
        synchronized (mObservers) {
            if (mObservers.contains(observer))
                return;
            mObservers.add(observer);
        }
    }

    void unregisterObserver(View observer) {
        if (observer == null)
            return;
        synchronized (mObservers) {
            mObservers.remove(observer);
        }
    }

    /**
     * 全部注销
     */
    protected void unregisterAll() {
        synchronized (mObservers) {
            mObservers.clear();
        }
    }

    /**
     * 通知变化
     *
     * @param id       ID
     * @param position 坐标，为{@link PagerAdapter#POSITION_NONE}时，表示坐标无关或全部刷新
     * @param tag      附件，可以为空
     */
    @SuppressWarnings("SameParameterValue")
    protected void notifyChanged(int id, int position, @Nullable Object tag) {
        for (int i = mObservers.size() - 1; i >= 0; i--) {
            final View observer = mObservers.get(i);
            if (observer instanceof TabStripView) {
                ((TabStripView) observer).onObservableChangeNotified(id, position, tag);
            } else if (observer instanceof TabStripViewGroup) {
                ((TabStripViewGroup) observer).onObservableChangeNotified(id, position, tag);
            }
        }
    }
}
