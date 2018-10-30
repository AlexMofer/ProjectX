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

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.StateSet;
import android.view.View;

/**
 * Drawable 工具
 * Created by Alex on 2018/10/17.
 */
@SuppressWarnings("unused")
final class DrawableUtils {

    private static final int DIP_BACKGROUND_RADIUS = 2;

    private DrawableUtils() {
        //no instance
    }

    static void setRootBackground(View root, boolean light) {
        final float radius = TypedValueUtils.complexToDimension(
                DIP_BACKGROUND_RADIUS, root.getResources().getDisplayMetrics());
        final GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(radius);
        if (light) {
            background.setColor(0xFFFFFFFF);
        } else {
            background.setColor(0xFF424242);
        }
        root.setBackgroundDrawable(background);
    }

    static void changeRootBackground(View root, boolean light) {
        final Drawable background = root.getBackground();
        if (background instanceof GradientDrawable) {
            if (light) {
                ((GradientDrawable) background).setColor(0xFFFFFFFF);
            } else {
                ((GradientDrawable) background).setColor(0xFF424242);
            }
        }
    }

    static void setButtonBackground(View button, boolean light, boolean useTheme) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (useTheme) {
                final TypedArray a = button.getContext().obtainStyledAttributes(
                        new int[]{android.R.attr.colorControlHighlight});
                final ColorStateList color = a.getColorStateList(0);
                a.recycle();
                if (color != null) {
                    button.setBackground(new RippleDrawable(color, null,
                            new ColorDrawable(Color.WHITE)));
                    return;
                }
            }
            button.setBackground(new RippleDrawable(
                    ColorStateList.valueOf(light ? 0x1f000000 : 0x33ffffff), null,
                    new ColorDrawable(Color.WHITE)));
            return;
        }
        final StateListDrawable background = new StateListDrawable();
        background.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(light ? 0x1f000000 : 0x33ffffff));
        background.addState(StateSet.NOTHING, new ColorDrawable(Color.TRANSPARENT));
        button.setBackgroundDrawable(background);
    }

    static void changeButtonBackground(View button, boolean light, boolean useTheme) {
        if (Build.VERSION.SDK_INT >= 21) {
            final Drawable background = button.getBackground();
            if (background instanceof RippleDrawable) {
                final RippleDrawable ripple = (RippleDrawable) background;
                if (useTheme) {
                    final TypedArray a = button.getContext().obtainStyledAttributes(
                            new int[]{android.R.attr.colorControlHighlight});
                    ColorStateList color = a.getColorStateList(0);
                    a.recycle();
                    if (color != null) {
                        ripple.setColor(color);
                        return;
                    }
                }
                ripple.setColor(ColorStateList.valueOf(light ? 0x1f000000 : 0x33ffffff));
            }
            return;
        }
        final StateListDrawable background = new StateListDrawable();
        background.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(light ? 0x1f000000 : 0x33ffffff));
        background.addState(StateSet.NOTHING, new ColorDrawable(Color.TRANSPARENT));
        button.setBackgroundDrawable(background);
    }

    static Drawable getListSelector(Context context, boolean light, boolean useTheme) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (useTheme) {
                final TypedArray a = context.obtainStyledAttributes(
                        new int[]{android.R.attr.colorControlHighlight});
                final ColorStateList color = a.getColorStateList(0);
                a.recycle();
                if (color != null) {
                    return new RippleDrawable(color, null, new ColorDrawable(Color.WHITE));
                }
            }
            return new RippleDrawable(
                    ColorStateList.valueOf(light ? 0x1f000000 : 0x33ffffff), null,
                    new ColorDrawable(Color.WHITE));
        }
        final StateListDrawable background = new StateListDrawable();
        background.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(light ? 0x1f000000 : 0x33ffffff));
        background.addState(StateSet.NOTHING, new ColorDrawable(Color.TRANSPARENT));
        return background;
    }

    static void setOverflowButtonBackground(View button, boolean light, boolean useTheme) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (useTheme) {
                final TypedArray a = button.getContext().obtainStyledAttributes(
                        new int[]{android.R.attr.actionBarItemBackground});
                final int resId = a.getResourceId(0, 0);
                a.recycle();
                if (resId != 0) {
                    button.setBackgroundResource(resId);
                    return;
                }
            }
            final RippleDrawable ripple = new RippleDrawable(
                    ColorStateList.valueOf(light ? 0x1f000000 : 0x33ffffff),
                    null, null);
            if (Build.VERSION.SDK_INT >= 23)
                ripple.setRadius(TypedValueUtils.complexToDimensionPixelOffset(20,
                        button.getResources().getDisplayMetrics()));
            button.setBackground(ripple);
            return;
        }
        final StateListDrawable background = new StateListDrawable();
        background.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(light ? 0x1f000000 : 0x33ffffff));
        background.addState(StateSet.NOTHING, new ColorDrawable(Color.TRANSPARENT));
        button.setBackgroundDrawable(background);
    }

    static void changeOverflowButtonBackground(View button, boolean light, boolean useTheme) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (useTheme) {
                final TypedArray a = button.getContext().obtainStyledAttributes(
                        new int[]{android.R.attr.actionBarItemBackground});
                final int resId = a.getResourceId(0, 0);
                a.recycle();
                if (resId != 0) {
                    button.setBackgroundResource(resId);
                    return;
                }
            }
            final Drawable background = button.getBackground();
            if (background instanceof RippleDrawable) {
                ((RippleDrawable) background).setColor(
                        ColorStateList.valueOf(light ? 0x1f000000 : 0x33ffffff));
                return;
            }
            final RippleDrawable ripple = new RippleDrawable(
                    ColorStateList.valueOf(light ? 0x1f000000 : 0x33ffffff),
                    null, null);
            if (Build.VERSION.SDK_INT >= 23)
                ripple.setRadius(TypedValueUtils.complexToDimensionPixelOffset(20,
                        button.getResources().getDisplayMetrics()));
            button.setBackground(ripple);
            return;
        }
        final StateListDrawable background = new StateListDrawable();
        background.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(light ? 0x1f000000 : 0x33ffffff));
        background.addState(StateSet.NOTHING, new ColorDrawable(Color.TRANSPARENT));
        button.setBackgroundDrawable(background);
    }

    static Drawable getOverflowButtonArrow(View button, boolean light, boolean useTheme) {
        final Context context = button.getContext();
        if (light) {
            if (Build.VERSION.SDK_INT >= 21)
                return context.getDrawable(R.drawable.fam_avd_tooverflow);
            else
                //noinspection deprecation
                return context.getResources().getDrawable(R.drawable.fam_avd_tooverflow);
        } else {
            if (Build.VERSION.SDK_INT >= 21)
                return context.getDrawable(R.drawable.fam_avd_tooverflow_dark);
            else
                //noinspection deprecation
                return context.getResources().getDrawable(R.drawable.fam_avd_tooverflow_dark);
        }
    }

    static Drawable getOverflowButtonOverflow(View button, boolean light, boolean useTheme) {
        final Context context = button.getContext();
        if (light) {
            if (Build.VERSION.SDK_INT >= 21)
                return context.getDrawable(R.drawable.fam_avd_toarrow);
            else
                //noinspection deprecation
                return context.getResources().getDrawable(R.drawable.fam_avd_toarrow);
        } else {
            if (Build.VERSION.SDK_INT >= 21)
                return context.getDrawable(R.drawable.fam_avd_toarrow_dark);
            else
                //noinspection deprecation
                return context.getResources().getDrawable(R.drawable.fam_avd_toarrow_dark);
        }
    }

    static Drawable getOverflowButtonToArrow(View button, boolean light, boolean useTheme) {
        final Context context = button.getContext();
        if (Build.VERSION.SDK_INT >= 21) {
            if (light)
                return context.getDrawable(R.drawable.fam_avd_toarrow_animation);
            else
                return context.getDrawable(R.drawable.fam_avd_toarrow_animation_dark);
        } else
            return null;
    }

    static Drawable getOverflowButtonToOverflow(View button, boolean light, boolean useTheme) {
        final Context context = button.getContext();
        if (Build.VERSION.SDK_INT >= 21) {
            if (light)
                return context.getDrawable(R.drawable.fam_avd_tooverflow_animation);
            else
                return context.getDrawable(R.drawable.fam_avd_tooverflow_animation_dark);
        } else
            return null;
    }
}
