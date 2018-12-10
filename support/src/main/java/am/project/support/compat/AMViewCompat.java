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

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.lang.ref.WeakReference;


/**
 * View版本兼容控制器
 * Created by Alex on 2016/11/21.
 */
@SuppressWarnings("unused")
public final class AMViewCompat {

    private static final AMViewCompatImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new AMViewCompatLollipop();
        } else if (version >= 16) {
            IMPL = new AMViewCompatJB();
        } else {
            IMPL = new AMViewCompatBase();
        }
    }

    private AMViewCompat() {
        //no instance
    }

    /**
     * Set the background to a given Drawable, or remove the background. If the
     * background has padding, this View's padding is set to the background's
     * padding. However, when a background is removed, this View's padding isn't
     * touched. If setting the padding is desired, please use
     * setPadding(int, int, int, int).
     *
     * @param view       View
     * @param background The Drawable to use as the background, or null to remove the
     *                   background
     */
    public static void setBackground(View view, Drawable background) {
        IMPL.setBackground(view, background);
    }

    /**
     * Sets the {@link android.view.ViewOutlineProvider} of the view, which generates the Outline that defines
     * the shape of the shadow it casts, and enables outline clipping.
     * <p>
     * The default ViewOutlineProvider, {@link android.view.ViewOutlineProvider#BACKGROUND}, queries the Outline
     * from the View's background drawable, via {@link Drawable#getOutline(Outline)}. Changing the
     * outline provider with this method allows this behavior to be overridden.
     * <p>
     * If the ViewOutlineProvider is null, if querying it for an outline returns false,
     * or if the produced Outline is {@link Outline#isEmpty()}, shadows will not be cast.
     * <p>
     * Only outlines that return true from {@link Outline#canClip()} may be used for clipping.
     *
     * @see View#setClipToOutline(boolean)
     * @see View#getClipToOutline()
     * @see View#getOutlineProvider()
     */
    public static void setOutlineProvider(View view, ViewOutlineProvider provider) {
        IMPL.setOutlineProvider(view, provider);
    }

    /**
     * Sets whether the View's Outline should be used to clip the contents of the View.
     * <p>
     * Only a single non-rectangular clip can be applied on a View at any time.
     * Circular clips from a {@link android.view.ViewAnimationUtils#createCircularReveal(View, int, int, float, float)
     * circular reveal} animation take priority over Outline clipping, and
     * child Outline clipping takes priority over Outline clipping done by a
     * parent.
     * <p>
     * Note that this flag will only be respected if the View's Outline returns true from
     * {@link Outline#canClip()}.
     *
     * @see View#setOutlineProvider(android.view.ViewOutlineProvider)
     * @see View#getClipToOutline()
     */
    public static void setClipToOutline(View view, boolean clipToOutline) {
        IMPL.setClipToOutline(view, clipToOutline);
    }

    /**
     * Attaches the provided StateListAnimator to this View.
     * <p>
     * Any previously attached StateListAnimator will be detached.
     *
     * @param stateListAnimator The StateListAnimator to update the view
     * @see android.animation.StateListAnimator
     */
    public static void setStateListAnimator(View view, StateListAnimator stateListAnimator) {
        IMPL.setStateListAnimator(view, stateListAnimator);
    }

    /**
     * Attaches the provided StateListAnimator to this View.
     * <p>
     * Any previously attached StateListAnimator will be detached.
     *
     * @param id The id of StateListAnimator to update the view
     * @see android.animation.StateListAnimator
     */
    public static void setStateListAnimator(View view, int id) {
        IMPL.setStateListAnimator(view, id);
    }

    interface AMViewCompatImpl {
        void setBackground(View view, Drawable background);

        void setOutlineProvider(View view, ViewOutlineProvider provider);

        void setClipToOutline(View view, boolean clipToOutline);

        void setStateListAnimator(View view, StateListAnimator stateListAnimator);

        void setStateListAnimator(View view, int id);
    }

    private static class AMViewCompatBase implements AMViewCompatImpl {
        @SuppressWarnings("deprecation")
        @Override
        public void setBackground(View view, Drawable background) {
            view.setBackgroundDrawable(background);
        }

        @Override
        public void setOutlineProvider(View view, ViewOutlineProvider provider) {
            // do nothing
        }

        @Override
        public void setClipToOutline(View view, boolean clipToOutline) {
            // do nothing
        }

        @Override
        public void setStateListAnimator(View view, StateListAnimator stateListAnimator) {
            // do nothing
        }

        @Override
        public void setStateListAnimator(View view, int id) {

        }
    }

    @TargetApi(16)
    private static class AMViewCompatJB extends AMViewCompatBase {
        @Override
        public void setBackground(View view, Drawable background) {
            view.setBackground(background);
        }
    }

    @TargetApi(21)
    private static class AMViewCompatLollipop extends AMViewCompatJB {
        @Override
        public void setOutlineProvider(View view, ViewOutlineProvider provider) {
            view.setOutlineProvider(new ViewOutlineProviderLollipop(provider));
        }

        @Override
        public void setClipToOutline(View view, boolean clipToOutline) {
            view.setClipToOutline(clipToOutline);
        }

        @Override
        public void setStateListAnimator(View view, StateListAnimator stateListAnimator) {
            view.setStateListAnimator(stateListAnimator);
        }

        @Override
        public void setStateListAnimator(View view, int id) {
            view.setStateListAnimator(AnimatorInflater.loadStateListAnimator(view.getContext(), id));
        }
    }

    @TargetApi(21)
    private static class ViewOutlineProviderLollipop extends android.view.ViewOutlineProvider {
        private final WeakReference<ViewOutlineProvider> providerWeakReference;

        ViewOutlineProviderLollipop(ViewOutlineProvider provider) {
            providerWeakReference = new WeakReference<>(provider);
        }

        @Override
        public void getOutline(View view, Outline outline) {
            ViewOutlineProvider provider = providerWeakReference.get();
            if (provider != null)
                provider.getOutline(view, outline);
        }
    }
}
