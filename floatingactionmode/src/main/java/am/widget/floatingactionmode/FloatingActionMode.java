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

import android.graphics.Rect;
import android.view.View;

import am.widget.floatingactionmode.impl.FloatingActionModeImpl;

/**
 * 浮动ActionMode
 * TODO 面板切换动画Drawable，各面板的视图裁剪
 * Created by Alex on 2018/10/24.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FloatingActionMode {

    public static final int LOCATION_BELOW_PRIORITY = 0;// 优先考虑显示在矩形区域的下方
    public static final int LOCATION_BELOW_ALWAYS = 1;// 固定显示在矩形区域的下方
    public static final int LOCATION_ABOVE_PRIORITY = 2;// 优先考虑显示在矩形区域的上方
    public static final int LOCATION_ABOVE_ALWAYS = 3;// 固定显示在矩形区域的上方
    private Object mTag;
    private final FloatingActionModeImpl mImpl;

    public FloatingActionMode(View target, Callback callback) {
        this(target, callback, 0);
    }

    public FloatingActionMode(View target, Callback callback, int themeResId) {
        mImpl = new FloatingActionModeImpl(this, target, callback, themeResId);
    }

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public void setLocation(int location) {
        if (location != LOCATION_BELOW_PRIORITY && location != LOCATION_BELOW_ALWAYS &&
                location != LOCATION_ABOVE_PRIORITY && location != LOCATION_ABOVE_ALWAYS)
            return;
        mImpl.setLocation(location);
    }

    public boolean isClippingEnabled() {
        return mImpl.isClippingEnabled();
    }

    public void setClippingEnabled(boolean enabled) {
        mImpl.setClippingEnabled(enabled);
    }

    public boolean isLayoutInScreenEnabled() {
        return mImpl.isLayoutInScreenEnabled();
    }

    public void setLayoutInScreenEnabled(boolean enabled) {
        mImpl.setLayoutInScreenEnabled(enabled);
    }

    public void start() {
        mImpl.start();
    }

    public void invalidate() {
        mImpl.invalidate();
    }

    public void invalidateContentRect() {
        mImpl.invalidateContentRect();
    }

    public void finish() {
        mImpl.finish();
    }

    public void hide() {
        mImpl.hide();
    }

    public void show() {
        mImpl.show();
    }

    public boolean isHidden() {
        return mImpl.isHidden();
    }

    public boolean isFinished() {
        return mImpl.isFinished();
    }

    public void performActionItemClicked(FloatingMenuItem item) {
        mImpl.performActionItemClicked(item);
    }

    public void backToMain(boolean animate) {
        mImpl.backToMain(animate);
    }

    public void openOverflow(boolean animate) {
        mImpl.openOverflow(animate);
    }

    public FloatingMenu getMenu() {
        return mImpl.getMenu();
    }

    public interface Callback {

        /**
         * Called to refresh an action mode's action menu whenever it is invalidated.
         *
         * @param mode FloatingActionMode being prepared
         * @param menu FloatingMenu used to populate action buttons
         * @return true if the menu or action mode was updated, false otherwise.
         */
        boolean onPrepareActionMode(FloatingActionMode mode, FloatingMenu menu);

        /**
         * Called when an FloatingActionMode needs to be positioned on screen, potentially occluding view
         * content. Note this may be called on a per-frame basis.
         *
         * @param mode    The FloatingActionMode that requires positioning.
         * @param view    The View that originated the FloatingActionMode, in whose coordinates the Rect should
         *                be provided.
         * @param outRect The Rect to be populated with the content position. Use this to specify
         *                where the content in your app lives within the given view. This will be used
         *                to avoid occluding the given content Rect with the created FloatingActionMode.
         */
        void onGetContentRect(FloatingActionMode mode, View view, Rect outRect);

        /**
         * Called to report a user click on an action button.
         *
         * @param mode The current FloatingActionMode
         * @param item The item that was clicked
         * @return true if this callback handled the event, false if the standard MenuItem
         * invocation should continue.
         */
        boolean onActionItemClicked(FloatingActionMode mode, FloatingMenuItem item);

        /**
         * Called when an action mode is about to be exited and destroyed.
         *
         * @param mode The current FloatingActionMode being destroyed
         */
        void onDestroyActionMode(FloatingActionMode mode);
    }
}
