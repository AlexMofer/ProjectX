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
package io.github.alexmofer.projectx.business.others.font;

/**
 * AdapterViewModel
 */
interface FontAdapterViewModel {
    int getTypefaceFallbackCount();

    String getTypefaceName();

    String getCommonTitle();

    int getCommonItemCount();

    Object getCommonItem(int position);

    Object getFallback(int position);

    String getFallbackTitle(Object fallback);

    int getFallbackItemCount(Object fallback);

    Object getFallbackItem(Object fallback, int position);

    String getTypefaceItemName(Object item);

    String getTypefaceItemInfo(Object item);
}
