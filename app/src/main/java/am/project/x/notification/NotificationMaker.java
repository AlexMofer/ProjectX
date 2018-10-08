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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import am.project.x.R;
import am.project.x.utils.ContextUtils;


/**
 * 通知构造器
 * Created by Alex on 2017/8/24.
 */
public class NotificationMaker {

    public static final int ID_FTP = 1;


    public static Notification getFTPRunning(Context context, String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                NotificationChannelHelper.getChannelLow(context))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_notification_ftp)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(PendingIntent.getActivity(context, ID_FTP,
                        ContextUtils.getLaunchIntent(context), PendingIntent.FLAG_UPDATE_CURRENT));
        return builder.build();
    }
}
