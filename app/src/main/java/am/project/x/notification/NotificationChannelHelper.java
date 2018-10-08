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
package am.project.x.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;

import am.project.x.R;


/**
 * 通知频道辅助器
 * Created by Alex on 2017/12/18.
 */
public class NotificationChannelHelper {
    private static final String CHANEL_ID_LOW = "am.project.ftpgo.notification.CHANEL_LOW";// 不发出提示音

    /**
     * 更新通知渠道
     *
     * @param context Context
     */
    public static void updateNotificationChannel(Context context) {
        updateChannelLow(context);
    }

    static String getChannelLow(Context context) {
        updateChannelLow(context);
        return CHANEL_ID_LOW;
    }

    @Nullable
    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
    }

    private static void updateChannelLow(Context context) {
        if (Build.VERSION.SDK_INT < 26)
            return;
        NotificationManager manager = getNotificationManager(context);
        if (manager == null)
            return;
        NotificationChannel channel = manager.getNotificationChannel(CHANEL_ID_LOW);
        final String name = context.getString(R.string.notification_name_low);
        if (channel == null) {
            channel = new NotificationChannel(CHANEL_ID_LOW, name,
                    NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
        }
        channel.setName(name);
    }

}
