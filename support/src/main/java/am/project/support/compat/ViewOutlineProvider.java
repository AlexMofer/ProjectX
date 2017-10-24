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

package am.project.support.compat;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.view.View;

/**
 * Interface by which a View builds its {@link Outline}, used for shadow casting and clipping.
 */
@SuppressWarnings("all")
public interface ViewOutlineProvider {

    /**
     * Called to get the provider to populate the Outline.
     *
     * This method will be called by a View when its owned Drawables are invalidated, when the
     * View's size changes, or if {@link View#invalidateOutline()} is called
     * explicitly.
     *
     * The input outline is empty and has an alpha of <code>1.0f</code>.
     *
     * @param view The view building the outline.
     * @param outline The empty outline to be populated.
     */
    @TargetApi(21)
    void getOutline(View view, Outline outline);
}
