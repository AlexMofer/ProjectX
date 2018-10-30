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
    private final MenuWindow mWindow;
    private Object mTag;

    private FloatingActionMode(View target, Callback callback) {
        mWindow = new MenuWindow(target, callback, this);
    }

    public static FloatingActionMode startFloatingActionMode(View target, Callback callback) {
        final FloatingActionMode mode = new FloatingActionMode(target, callback);
        return mode.start() ? mode : null;
    }

    private boolean start() {
        return mWindow.start(this);
    }

    /**
     * Retrieve the tag object associated with this FloatingActionMode.
     *
     * <p>Like the tag available to views, this allows applications to associate arbitrary
     * data with an FloatingActionMode for later reference.
     *
     * @return Tag associated with this FloatingActionMode
     * @see #setTag(Object)
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * Set a tag object associated with this FloatingActionMode.
     *
     * <p>Like the tag available to views, this allows applications to associate arbitrary
     * data with an FloatingActionMode for later reference.
     *
     * @param tag Tag to associate with this FloatingActionMode
     * @see #getTag()
     */
    public void setTag(Object tag) {
        mTag = tag;
    }

    /**
     * Sets this action mode show location.
     *
     * @param location where to show this action mode.
     */
    public void setLocation(int location) {
        if (location != LOCATION_BELOW_PRIORITY && location != LOCATION_BELOW_ALWAYS &&
                location != LOCATION_ABOVE_PRIORITY && location != LOCATION_ABOVE_ALWAYS)
            return;
        mWindow.setLocation(location);
    }

    /**
     * Sets this action mode to light theme.
     */
    public void setLightTheme(boolean light, boolean useTheme) {
        mWindow.setLightTheme(light, useTheme);
    }

    /**
     * Returns {@code true} if this action mode is light theme. {@code false} otherwise.
     */
    public boolean isLightTheme() {
        return mWindow.isLightTheme();
    }

    /**
     * Invalidate the action mode and refresh menu content. The mode's
     * {@link Callback} will have its
     * {@link Callback#onPrepareActionMode(FloatingActionMode, FloatingMenu)} method called.
     * If it returns true the menu will be scanned for updated content and any relevant changes
     * will be reflected to the user.
     */
    public void invalidate() {
        mWindow.invalidate(this);
    }

    /**
     * Invalidate the content rect associated to this FloatingActionMode. This only makes sense for
     * action modes that support dynamic positioning on the screen, and provides a more efficient
     * way to reposition it without invalidating the whole action mode.
     *
     * @see Callback#onGetContentRect(FloatingActionMode, View, Rect) .
     */
    public void invalidateContentRect() {
        mWindow.invalidateContentRect(this);
    }

    /**
     * Finish and close this action mode. The action mode's {@link Callback} will
     * have its {@link Callback#onDestroyActionMode(FloatingActionMode)} method called.
     */
    public void finish() {
        mWindow.finish(this, false);
    }

    /**
     * Hides this action mode.
     * Use {@link #isHidden()} to distinguish between a hidden and a dismissed menu.
     */
    public void hide() {
        mWindow.hide();
    }

    /**
     * Shows this action mode.
     */
    public void show() {
        mWindow.show();
    }

    /**
     * Returns {@code true} if this action mode is currently hidden. {@code false} otherwise.
     */
    public boolean isHidden() {
        return mWindow.isHidden();
    }

    /**
     * Returns {@code true} if this action mode is currently finished. {@code false} otherwise.
     */
    public boolean isFinished() {
        return mWindow.isFinished();
    }

    public interface Callback {
        /**
         * Called when action mode is first created. The menu supplied will be used to
         * generate action buttons for the action mode.
         *
         * @param mode FloatingActionMode being created
         * @param menu FloatingMenu used to populate action buttons
         * @return true if the action mode should be created, false if entering this
         * mode should be aborted.
         */
        boolean onCreateActionMode(FloatingActionMode mode, FloatingMenu menu);

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
