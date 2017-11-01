package am.project.x.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;

/**
 * 通知频道辅助器
 * Created by Alex on 2017/11/2.
 */

public class NotificationChannelHelper {
    private static final String CHANEL_ID_MIN = "am.project.x.notification.CHANEL_MIN";// 不发出提示音，不在状态栏显示图标
    private static final String CHANEL_ID_LOW = "am.project.x.notification.CHANEL_LOW";// 不发出提示音
    private static final String CHANEL_ID_DEFAULT = "am.project.x.notification.CHANEL_DEFAULT";// 发出提示音
    private static final String CHANEL_ID_HIGH = "am.project.x.notification.CHANEL_HIGH";// 发出提示音并弹出

    /**
     * 更新通知渠道
     *
     * @param context Context
     */
    public static void updateNotificationChannel(Context context) {
        updateChannelMin(context);
        updateChannelLow(context);
        updateChannelDefault(context);
        updateChannelHigh(context);
    }

    static String getChannelMin(Context context) {
        updateChannelMin(context);
        return CHANEL_ID_MIN;
    }

    static String getChannelLow(Context context) {
        updateChannelLow(context);
        return CHANEL_ID_LOW;
    }

    static String getChannelDefault(Context context) {
        updateChannelDefault(context);
        return CHANEL_ID_DEFAULT;
    }

    static String getChannelHigh(Context context) {
        updateChannelHigh(context);
        return CHANEL_ID_HIGH;
    }

    @Nullable
    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
    }

    private static void updateChannelMin(Context context) {
        if (Build.VERSION.SDK_INT < 26)
            return;
        NotificationManager manager = getNotificationManager(context);
        if (manager == null)
            return;
        NotificationChannel channel = manager.getNotificationChannel(CHANEL_ID_MIN);
        final String name = "Min";
        if (channel == null) {
            channel = new NotificationChannel(CHANEL_ID_MIN, name,
                    NotificationManager.IMPORTANCE_MIN);
            manager.createNotificationChannel(channel);
        }
        channel.setName(name);
    }

    private static void updateChannelLow(Context context) {
        if (Build.VERSION.SDK_INT < 26)
            return;
        NotificationManager manager = getNotificationManager(context);
        if (manager == null)
            return;
        NotificationChannel channel = manager.getNotificationChannel(CHANEL_ID_LOW);
        final String name = "Low";
        if (channel == null) {
            channel = new NotificationChannel(CHANEL_ID_LOW, name,
                    NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
        }
        channel.setName(name);
    }

    private static void updateChannelDefault(Context context) {
        if (Build.VERSION.SDK_INT < 26)
            return;
        NotificationManager manager = getNotificationManager(context);
        if (manager == null)
            return;
        NotificationChannel channel = manager.getNotificationChannel(CHANEL_ID_DEFAULT);
        final String name = "Default";
        if (channel == null) {
            channel = new NotificationChannel(CHANEL_ID_DEFAULT, name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        channel.setName(name);
    }

    private static void updateChannelHigh(Context context) {
        if (Build.VERSION.SDK_INT < 26)
            return;
        NotificationManager manager = getNotificationManager(context);
        if (manager == null)
            return;
        NotificationChannel channel = manager.getNotificationChannel(CHANEL_ID_HIGH);
        final String name = "High";
        if (channel == null) {
            channel = new NotificationChannel(CHANEL_ID_HIGH, name,
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        channel.setName(name);
    }
}