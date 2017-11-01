package am.project.x.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * 通知构造器
 * Created by Alex on 2017/11/2.
 */

public class NotificationMaker {
    private static final int ID_SCAN = 1;
    private static final int ID_DEFAULT = 2;

    /**
     * 通知扫描
     *
     * @param context Context
     * @param title   标题
     * @param text    内容
     */
    public static void notifyScan(Context context, String title, String text) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title).bigText(text);
        NotificationCompat.Builder builder =
                NotificationBuilderHelper.getNotificationBuilderMin(context);
        builder.setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(bigTextStyle)
                .setContentTitle(title)
                .setOngoing(true)
                .setContentText(text);
        NotificationManagerCompat.from(context).notify(ID_SCAN, builder.build());
    }

    /**
     * 获取下载中通知
     *
     * @param context       Context
     * @param title         标题
     * @param text          内容
     * @param progress      进度指
     * @param indeterminate 是否为不确定进度
     * @return 下载中通知
     */
    public static Notification getNotificationDownloading(Context context,
                                                          String title, String text,
                                                          int progress, boolean indeterminate) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title).bigText(text);
        NotificationCompat.Builder builder =
                NotificationBuilderHelper.getNotificationBuilderLow(context);
        builder.setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setAutoCancel(true)
                .setStyle(bigTextStyle)
                .setContentTitle(title)
                .setContentText(text)
                .setProgress(100, progress, indeterminate);
        return builder.build();
    }


    /**
     * 发送推送默认通知
     *
     * @param context   Context
     * @param title     消息标题
     * @param text      消息文本
     * @param tag       标签
     * @param broadcast 广播
     */
    public static void notifyPushDefault(Context context, String title, String text, String tag,
                                         @Nullable Intent broadcast) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title).bigText(text);
        NotificationCompat.Builder builder =
                NotificationBuilderHelper.getNotificationBuilderDefault(context);
        builder.setCategory(NotificationCompat.CATEGORY_PROMO)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setStyle(bigTextStyle)
                .setContentTitle(title)
                .setContentText(text);
        if (broadcast != null) {
            builder.setContentIntent(PendingIntent.getBroadcast(context, ID_DEFAULT, broadcast,
                    PendingIntent.FLAG_UPDATE_CURRENT));
        }
        NotificationManagerCompat.from(context).notify(tag, ID_DEFAULT, builder.build());
    }
}
