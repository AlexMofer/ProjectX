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

import android.support.v4.view.PagerAdapter;

/**
 * 圆点Adapter
 */
@SuppressWarnings("unused")
public abstract class TabStripDotAdapter extends TabStripObservable {

    /**
     * 获取圆点更新ID
     *
     * @return ID
     */
    protected abstract int getDotNotifyId();

    /**
     * 获取圆点文字
     *
     * @param position 位置
     * @param count    总数
     * @return 圆点文字，返回null表示关闭
     */
    public String getDotText(int position, int count) {
        return null;
    }

    /**
     * 通知圆点已改变
     */
    public void notifyDotChanged() {
        notifyChanged(getDotNotifyId(), PagerAdapter.POSITION_NONE, null);
    }

    /**
     * 通知圆点已改变
     *
     * @param position 位置
     */
    public void notifyDotChanged(int position) {
        notifyChanged(getDotNotifyId(), position, null);
    }
}
