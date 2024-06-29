/*
 * Copyright (C) 2022 AlexMofer
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
package io.github.alexmofer.android.support.app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * PendingIntent兼容器
 * Created by Alex on 2022/6/17.
 */
public class PendingIntentCompat {

    private PendingIntentCompat() {
        //no instance
    }

    private static int makeFlags(int flags, boolean mutable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (mutable) {
                flags |= PendingIntent.FLAG_MUTABLE;
            } else {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!mutable) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
        }
        return flags;
    }

    /**
     * Retrieve a PendingIntent that will start a new activity, like calling
     * {@link Context#startActivity(Intent) Context.startActivity(Intent)}.
     * Note that the activity will be started outside of the context of an
     * existing activity, so you must use the {@link Intent#FLAG_ACTIVITY_NEW_TASK
     * Intent.FLAG_ACTIVITY_NEW_TASK} launch flag in the Intent.
     *
     * <p class="note">For security reasons, the {@link Intent}
     * you supply here should almost always be an <em>explicit intent</em>,
     * that is specify an explicit component to be delivered to through
     * {@link Intent#setClass(Context, Class) Intent.setClass}</p>
     *
     * @param context     The Context in which this PendingIntent should start
     *                    the activity.
     * @param requestCode Private request code for the sender
     * @param intent      Intent of the activity to be launched.
     * @param flags       May be {@link PendingIntent#FLAG_ONE_SHOT}, {@link PendingIntent#FLAG_NO_CREATE},
     *                    {@link PendingIntent#FLAG_CANCEL_CURRENT}, {@link PendingIntent#FLAG_UPDATE_CURRENT},
     *                    or any of the flags as supported by
     *                    {@link Intent#fillIn Intent.fillIn()} to control which unspecified parts
     *                    of the intent that can be supplied when the actual send happens.
     * @return Returns an existing or new PendingIntent matching the given
     * parameters.  May return null only if {@link PendingIntent#FLAG_NO_CREATE} has been
     * supplied.
     */
    public static PendingIntent getActivity(Context context, int requestCode,
                                            Intent intent, int flags,
                                            boolean mutable) {
        return PendingIntent.getActivity(context, requestCode, intent, makeFlags(flags, mutable));
    }

    /**
     * Retrieve a PendingIntent that will start a new activity, like calling
     * {@link Context#startActivity(Intent) Context.startActivity(Intent)}.
     * Note that the activity will be started outside of the context of an
     * existing activity, so you must use the {@link Intent#FLAG_ACTIVITY_NEW_TASK
     * Intent.FLAG_ACTIVITY_NEW_TASK} launch flag in the Intent.
     *
     * <p class="note">For security reasons, the {@link Intent}
     * you supply here should almost always be an <em>explicit intent</em>,
     * that is specify an explicit component to be delivered to through
     * {@link Intent#setClass(Context, Class) Intent.setClass}</p>
     *
     * @param context     The Context in which this PendingIntent should start
     *                    the activity.
     * @param requestCode Private request code for the sender
     * @param intent      Intent of the activity to be launched.
     * @param flags       May be {@link PendingIntent#FLAG_ONE_SHOT}, {@link PendingIntent#FLAG_NO_CREATE},
     *                    {@link PendingIntent#FLAG_CANCEL_CURRENT}, {@link PendingIntent#FLAG_UPDATE_CURRENT},
     *                    or any of the flags as supported by
     *                    {@link Intent#fillIn Intent.fillIn()} to control which unspecified parts
     *                    of the intent that can be supplied when the actual send happens.
     * @param options     Additional options for how the Activity should be started.
     *                    May be null if there are no options.
     * @return Returns an existing or new PendingIntent matching the given
     * parameters.  May return null only if {@link PendingIntent#FLAG_NO_CREATE} has been
     * supplied.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static PendingIntent getActivity(Context context, int requestCode,
                                            @NonNull Intent intent, int flags,
                                            @Nullable Bundle options,
                                            boolean mutable) {
        return PendingIntent.getActivity(context, requestCode, intent, makeFlags(flags, mutable),
                options);
    }

    /**
     * Like {@link PendingIntent#getActivity(Context, int, Intent, int)}, but allows an
     * array of Intents to be supplied.  The last Intent in the array is
     * taken as the primary key for the PendingIntent, like the single Intent
     * given to {@link PendingIntent#getActivity(Context, int, Intent, int)}.  Upon sending
     * the resulting PendingIntent, all of the Intents are started in the same
     * way as they would be by passing them to {@link Context#startActivities(Intent[])}.
     *
     * <p class="note">
     * The <em>first</em> intent in the array will be started outside of the context of an
     * existing activity, so you must use the {@link Intent#FLAG_ACTIVITY_NEW_TASK
     * Intent.FLAG_ACTIVITY_NEW_TASK} launch flag in the Intent.  (Activities after
     * the first in the array are started in the context of the previous activity
     * in the array, so FLAG_ACTIVITY_NEW_TASK is not needed nor desired for them.)
     * </p>
     *
     * <p class="note">
     * The <em>last</em> intent in the array represents the key for the
     * PendingIntent.  In other words, it is the significant element for matching
     * (as done with the single intent given to {@link PendingIntent#getActivity(Context, int, Intent, int)},
     * its content will be the subject of replacement by
     * {@link PendingIntent#send(Context, int, Intent)} and {@link PendingIntent#FLAG_UPDATE_CURRENT}, etc.
     * This is because it is the most specific of the supplied intents, and the
     * UI the user actually sees when the intents are started.
     * </p>
     *
     * <p class="note">For security reasons, the {@link Intent} objects
     * you supply here should almost always be <em>explicit intents</em>,
     * that is specify an explicit component to be delivered to through
     * {@link Intent#setClass(Context, Class) Intent.setClass}</p>
     *
     * @param context     The Context in which this PendingIntent should start
     *                    the activity.
     * @param requestCode Private request code for the sender
     * @param intents     Array of Intents of the activities to be launched.
     * @param flags       May be {@link PendingIntent#FLAG_ONE_SHOT}, {@link PendingIntent#FLAG_NO_CREATE},
     *                    {@link PendingIntent#FLAG_CANCEL_CURRENT}, {@link PendingIntent#FLAG_UPDATE_CURRENT},
     *                    or any of the flags as supported by
     *                    {@link Intent#fillIn Intent.fillIn()} to control which unspecified parts
     *                    of the intent that can be supplied when the actual send happens.
     * @return Returns an existing or new PendingIntent matching the given
     * parameters.  May return null only if {@link PendingIntent#FLAG_NO_CREATE} has been
     * supplied.
     */
    public static PendingIntent getActivities(Context context, int requestCode,
                                              @NonNull Intent[] intents, int flags,
                                              boolean mutable) {
        return PendingIntent.getActivities(context, requestCode, intents,
                makeFlags(flags, mutable));
    }

    /**
     * Like {@link PendingIntent#getActivity(Context, int, Intent, int)}, but allows an
     * array of Intents to be supplied.  The last Intent in the array is
     * taken as the primary key for the PendingIntent, like the single Intent
     * given to {@link PendingIntent#getActivity(Context, int, Intent, int)}.  Upon sending
     * the resulting PendingIntent, all of the Intents are started in the same
     * way as they would be by passing them to {@link Context#startActivities(Intent[])}.
     *
     * <p class="note">
     * The <em>first</em> intent in the array will be started outside of the context of an
     * existing activity, so you must use the {@link Intent#FLAG_ACTIVITY_NEW_TASK
     * Intent.FLAG_ACTIVITY_NEW_TASK} launch flag in the Intent.  (Activities after
     * the first in the array are started in the context of the previous activity
     * in the array, so FLAG_ACTIVITY_NEW_TASK is not needed nor desired for them.)
     * </p>
     *
     * <p class="note">
     * The <em>last</em> intent in the array represents the key for the
     * PendingIntent.  In other words, it is the significant element for matching
     * (as done with the single intent given to {@link PendingIntent#getActivity(Context, int, Intent, int)},
     * its content will be the subject of replacement by
     * {@link PendingIntent#send(Context, int, Intent)} and {@link PendingIntent#FLAG_UPDATE_CURRENT}, etc.
     * This is because it is the most specific of the supplied intents, and the
     * UI the user actually sees when the intents are started.
     * </p>
     *
     * <p class="note">For security reasons, the {@link Intent} objects
     * you supply here should almost always be <em>explicit intents</em>,
     * that is specify an explicit component to be delivered to through
     * {@link Intent#setClass(Context, Class) Intent.setClass}</p>
     *
     * @param context     The Context in which this PendingIntent should start
     *                    the activity.
     * @param requestCode Private request code for the sender
     * @param intents     Array of Intents of the activities to be launched.
     * @param flags       May be {@link PendingIntent#FLAG_ONE_SHOT}, {@link PendingIntent#FLAG_NO_CREATE},
     *                    {@link PendingIntent#FLAG_CANCEL_CURRENT}, {@link PendingIntent#FLAG_UPDATE_CURRENT}
     *                    or any of the flags as supported by
     *                    {@link Intent#fillIn Intent.fillIn()} to control which unspecified parts
     *                    of the intent that can be supplied when the actual send happens.
     * @return Returns an existing or new PendingIntent matching the given
     * parameters.  May return null only if {@link PendingIntent#FLAG_NO_CREATE} has been
     * supplied.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static PendingIntent getActivities(Context context, int requestCode,
                                              @NonNull Intent[] intents, int flags,
                                              @Nullable Bundle options,
                                              boolean mutable) {
        return PendingIntent.getActivities(context, requestCode, intents,
                makeFlags(flags, mutable), options);
    }

    /**
     * Retrieve a PendingIntent that will perform a broadcast, like calling
     * {@link Context#sendBroadcast(Intent) Context.sendBroadcast()}.
     *
     * <p class="note">For security reasons, the {@link Intent}
     * you supply here should almost always be an <em>explicit intent</em>,
     * that is specify an explicit component to be delivered to through
     * {@link Intent#setClass(Context, Class) Intent.setClass}</p>
     *
     * @param context     The Context in which this PendingIntent should perform
     *                    the broadcast.
     * @param requestCode Private request code for the sender
     * @param intent      The Intent to be broadcast.
     * @param flags       May be {@link PendingIntent#FLAG_ONE_SHOT}, {@link PendingIntent#FLAG_NO_CREATE},
     *                    {@link PendingIntent#FLAG_CANCEL_CURRENT}, {@link PendingIntent#FLAG_UPDATE_CURRENT},
     *                    or any of the flags as supported by
     *                    {@link Intent#fillIn Intent.fillIn()} to control which unspecified parts
     *                    of the intent that can be supplied when the actual send happens.
     * @return Returns an existing or new PendingIntent matching the given
     * parameters.  May return null only if {@link PendingIntent#FLAG_NO_CREATE} has been
     * supplied.
     */
    public static PendingIntent getBroadcast(Context context, int requestCode,
                                             @NonNull Intent intent, int flags,
                                             boolean mutable) {
        return PendingIntent.getBroadcast(context, requestCode, intent, makeFlags(flags, mutable));
    }

    /**
     * Retrieve a PendingIntent that will start a service, like calling
     * {@link Context#startService Context.startService()}.  The start
     * arguments given to the service will come from the extras of the Intent.
     *
     * <p class="note">For security reasons, the {@link Intent}
     * you supply here should almost always be an <em>explicit intent</em>,
     * that is specify an explicit component to be delivered to through
     * {@link Intent#setClass(Context, Class) Intent.setClass}</p>
     *
     * @param context     The Context in which this PendingIntent should start
     *                    the service.
     * @param requestCode Private request code for the sender
     * @param intent      An Intent describing the service to be started.
     * @param flags       May be {@link PendingIntent#FLAG_ONE_SHOT}, {@link PendingIntent#FLAG_NO_CREATE},
     *                    {@link PendingIntent#FLAG_CANCEL_CURRENT}, {@link PendingIntent#FLAG_UPDATE_CURRENT},
     *                    or any of the flags as supported by
     *                    {@link Intent#fillIn Intent.fillIn()} to control which unspecified parts
     *                    of the intent that can be supplied when the actual send happens.
     * @return Returns an existing or new PendingIntent matching the given
     * parameters.  May return null only if {@link PendingIntent#FLAG_NO_CREATE} has been
     * supplied.
     */
    public static PendingIntent getService(Context context, int requestCode,
                                           @NonNull Intent intent, int flags,
                                           boolean mutable) {
        return PendingIntent.getService(context, requestCode, intent, makeFlags(flags, mutable));
    }

    /**
     * Retrieve a PendingIntent that will start a foreground service, like calling
     * {@link Context#startForegroundService Context.startForegroundService()}.  The start
     * arguments given to the service will come from the extras of the Intent.
     *
     * <p class="note">For security reasons, the {@link Intent}
     * you supply here should almost always be an <em>explicit intent</em>,
     * that is specify an explicit component to be delivered to through
     * {@link Intent#setClass(Context, Class) Intent.setClass}</p>
     *
     * @param context     The Context in which this PendingIntent should start
     *                    the service.
     * @param requestCode Private request code for the sender
     * @param intent      An Intent describing the service to be started.
     * @param flags       May be {@link PendingIntent#FLAG_ONE_SHOT}, {@link PendingIntent#FLAG_NO_CREATE},
     *                    {@link PendingIntent#FLAG_CANCEL_CURRENT}, {@link PendingIntent#FLAG_UPDATE_CURRENT},
     *                    or any of the flags as supported by
     *                    {@link Intent#fillIn Intent.fillIn()} to control which unspecified parts
     *                    of the intent that can be supplied when the actual send happens.
     * @return Returns an existing or new PendingIntent matching the given
     * parameters.  May return null only if {@link PendingIntent#FLAG_NO_CREATE} has been
     * supplied.
     */
    public static PendingIntent getForegroundService(Context context, int requestCode,
                                                     @NonNull Intent intent, int flags,
                                                     boolean mutable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mutable) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    flags |= PendingIntent.FLAG_MUTABLE;
                }
            } else {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            return PendingIntent.getForegroundService(context, requestCode, intent, flags);
        }
        return getService(context, requestCode, intent, flags, mutable);
    }
}
