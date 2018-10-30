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

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * 菜单子项
 * Created by Alex on 2018/10/24.
 */
@SuppressWarnings("unused")
public interface FloatingMenuItem {

    int SHOW_TYPE_AUTO = -1;
    int SHOW_TYPE_TEXT = 0;
    int SHOW_TYPE_ICON = 1;
    int SHOW_TYPE_ALL = 2;

    /**
     * Return the identifier for this menu item.  The identifier can not
     * be changed after the menu is created.
     *
     * @return The menu item's identifier.
     */
    int getItemId();

    /**
     * Return the category and order within the category of this item. This
     * item will be shown before all items (within its category) that have
     * order greater than this value.
     *
     * @return The order of this item.
     */
    int getOrder();

    /**
     * Change the title associated with this item.
     *
     * @param title The new text to be displayed.
     * @return This Item so additional setters can be called.
     */
    FloatingMenuItem setTitle(CharSequence title);

    /**
     * Retrieve the current title of the item.
     *
     * @return The title.
     */
    CharSequence getTitle();

    /**
     * Change the title associated with this item.
     * <p>
     * Some menu types do not sufficient space to show the full title, and
     * instead a condensed title is preferred.
     *
     * @param title The resource id of the new text to be displayed.
     * @return This Item so additional setters can be called.
     */

    FloatingMenuItem setTitle(int title);

    /**
     * Change the icon associated with this item. This icon will not always be
     * shown, so the title should be sufficient in describing this item.
     *
     * @param icon The new icon (as a Drawable) to be displayed.
     * @return This Item so additional setters can be called.
     */
    FloatingMenuItem setIcon(Drawable icon);

    /**
     * Returns the icon for this item as a Drawable (getting it from resources if it hasn't been
     * loaded before).
     *
     * @return The icon as a Drawable.
     */
    Drawable getIcon();

    /**
     * Change the icon associated with this item. This icon will not always be
     * shown, so the title should be sufficient in describing this item.
     * <p>
     * This method will set the resource ID of the icon which will be used to
     * lazily get the Drawable when this item is being shown.
     *
     * @param iconRes The new icon (as a resource ID) to be displayed.
     * @return This Item so additional setters can be called.
     */
    FloatingMenuItem setIcon(int iconRes);

    int getShowType();

    FloatingMenuItem setShowType(int type);

    FloatingMenuItem setContentDescription(CharSequence contentDescription);

    /**
     * Retrieve the content description associated with this menu item.
     *
     * @return The content description.
     */
    CharSequence getContentDescription();

    FloatingMenuItem setContentDescription(int contentDescription);

    /**
     * Check whether this item has an associated sub-menu.  I.e. it is a
     * sub-menu of another menu.
     *
     * @return If true this item has a menu; else it is a
     * normal item.
     */
    boolean hasSubMenu();

    /**
     * Get the sub-menu to be invoked when this item is selected, if it has
     * one. See {@link #hasSubMenu()}.
     *
     * @return The associated menu if there is one, else null
     */
    FloatingSubMenu getSubMenu();

    FloatingSubMenu setSubMenu();

    FloatingSubMenu setSubMenu(View custom);
}
