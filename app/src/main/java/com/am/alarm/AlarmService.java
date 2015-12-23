package com.am.alarm;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;

import com.am.widget.R;


/**
 * 闹钟服务
 * Created by Alex on 2015/12/10.
 */
public class AlarmService extends Service {

    public static final String ACTION_START_ALARM = "com.mazing.tasty.business.operator.alarm.ACTION_START_ALARM";// 开启闹铃
    public static final String ACTION_STOP_ALARM = "com.mazing.tasty.business.operator.alarm.ACTION_STOP_ALARM";// 关闭闹铃
    public static final String ACTION_CLICK_ALARM = "com.mazing.tasty.business.operator.alarm.ACTION_CLICK_ALARM";// 点击闹铃通知栏
    public static final int NOTIFICATION_ID = 1086;
    public static boolean running = false;
    private AlarmService me = this;
    private LocalBroadcastManager mLocalBroadcastManager;
    private AMAudioManager tastyAudioManager;
    private BroadcastReceiver registerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (AMAudioManager.VOLUME_CHANGED_ACTION.equals(action)) {
                tastyAudioManager.updateVolume();
            } else if (ACTION_CLICK_ALARM.equals(action)) {
//                if (!SharedPreferencesManager.getMainActive(context) || MainActivity.isKill)
//                    startActivity(new Intent(context, MainActivity.class)
//                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        }
    };
    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_START_ALARM.equals(action)) {
                startAlarm();
            } else if (ACTION_STOP_ALARM.equals(action)) {
                pauseAlarm();
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        running = true;
        tastyAudioManager = new AMAudioManager(me);
        tastyAudioManager.setAlarm(me);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(me);
        IntentFilter localFilter = new IntentFilter();
        localFilter.addAction(ACTION_START_ALARM);
        localFilter.addAction(ACTION_STOP_ALARM);
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver, localFilter);
        IntentFilter filter = new IntentFilter();
        filter.addAction(AMAudioManager.VOLUME_CHANGED_ACTION);
        filter.addAction(ACTION_CLICK_ALARM);
        registerReceiver(registerReceiver, filter);
        startAlarm();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver);
        tastyAudioManager.release();
        unregisterReceiver(registerReceiver);
    }

    private void startAlarm() {
        PendingIntent intent = PendingIntent.getBroadcast(me, 0, new Intent(ACTION_CLICK_ALARM), 0);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(getString(R.string.app_name))
                .bigText("响铃");
        Notification notification = new NotificationCompat.Builder(me)
                .setColor(0xffff0027)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(intent)
                .setStyle(bigTextStyle)
                .setContentTitle(getString(R.string.app_name))
                .setLights(0xff00ff00, 500, 500)
                .setContentText("响铃").build();
        startForeground(NOTIFICATION_ID, notification);
        tastyAudioManager.startAlarm();
    }

    private void pauseAlarm() {
        stopForeground(true);
        tastyAudioManager.pauseAlarm();
    }

}
