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

/**
 * 菜单
 * Created by Alex on 2018/10/24.
 */
@SuppressWarnings("unused")
public interface FloatingMenu {

    /**
     * Value to use for group and item identifier integers when you don't care
     * about them.
     */
    int NONE = 0;

    /**
     * Add a new item to the menu. This item displays the given title for its
     * label.
     *
     * @param title The text to display for the item.
     * @return The newly added menu item.
     */
    FloatingMenuItem add(CharSequence title);

    /**
     * Add a new item to the menu. This item displays the given title for its
     * label.
     *
     * @param titleRes Resource identifier of title string.
     * @return The newly added menu item.
     */
    FloatingMenuItem add(int titleRes);

    /**
     * Add a new item to the menu. This item displays the given title for its
     * label.
     *
     * @param itemId Unique item ID. Use {@link #NONE} if you do not need a
     *               unique ID.
     * @param order  The order for the item. Use {@link #NONE} if you do not care
     *               about the order. See {@link FloatingMenuItem#getOrder()}.
     * @param title  The text to display for the item.
     * @return The newly added menu item.
     */
    FloatingMenuItem add(int itemId, int order, CharSequence title);

    /**
     * Variation on {@link #add(int, int, CharSequence)} that takes a
     * string resource identifier instead of the string itself.
     *
     * @param itemId   Unique item ID. Use {@link #NONE} if you do not need a
     *                 unique ID.
     * @param order    The order for the item. Use {@link #NONE} if you do not care
     *                 about the order. See {@link FloatingMenuItem#getOrder()}.
     * @param titleRes Resource identifier of title string.
     * @return The newly added menu item.
     */
    FloatingMenuItem add(int itemId, int order, int titleRes);

    /**
     * Remove the item with the given identifier.
     *
     * @param id The item to be removed.  If there is no item with this
     *           identifier, nothing happens.
     */
    void removeItem(int id);

    /**
     * Remove all existing items from the menu, leaving it empty as if it had
     * just been created.
     */
    void clear();

    /**
     * Return the menu item with a particular identifier.
     *
     * @param id The identifier to find.
     * @return The menu item object, or null if there is no item with
     * this identifier.
     */
    FloatingMenuItem findItem(int id);

    /**
     * Get the number of items in the menu.  Note that this will change any
     * times items are added or removed from the menu.
     *
     * @return The item count.
     */
    int size();

    /**
     * Gets the menu item at the given index.
     *
     * @param index The index of the menu item to return.
     * @return The menu item.
     * @throws IndexOutOfBoundsException when {@code index < 0 || >= size()}
     */
    FloatingMenuItem getItem(int index);
}
