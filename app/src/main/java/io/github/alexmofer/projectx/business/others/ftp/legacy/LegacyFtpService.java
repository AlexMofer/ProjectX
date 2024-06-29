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
package io.github.alexmofer.projectx.business.others.ftp.legacy;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.am.ftp.ftpserver.FtpServer;

import io.github.alexmofer.projectx.R;
import io.github.alexmofer.projectx.business.others.ftp.FtpActivity;
import io.github.alexmofer.projectx.notification.NotificationMaker;
import io.github.alexmofer.projectx.utils.ContextUtils;
import io.github.alexmofer.projectx.utils.Utils;

/**
 * 文件传输服务
 */
public class LegacyFtpService extends Service {
    private static boolean STARTED = false;
    private FtpServer mFTP;
    private boolean mAutoClose = false;
    private final BroadcastReceiver mBroadcastReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                        onConnectivityChanged();
                    }
                }
            };

    public LegacyFtpService() {
    }

    public static void start(Context context) {
        context.startService(new Intent(context, LegacyFtpService.class));
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, LegacyFtpService.class));
    }

    public static boolean isStarted() {
        return STARTED;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (STARTED)
            return super.onStartCommand(intent, flags, startId);
        if (!ContextUtils.isWifiConnected(this)) {
            Toast.makeText(this, R.string.ftp_toast_no_wifi, Toast.LENGTH_SHORT).show();
            return super.onStartCommand(intent, flags, startId);
        }
        mAutoClose = true;
        final LegacyFtpConfig config = new LegacyFtpConfig(this);
        int port = config.getPort();
        if (config.isAutoChangePort()) {
            boolean check = false;
            while (!Utils.isPortAvailable(port)) {
                port++;
                if (port > 65535) {
                    if (check) {
                        port = 65535;
                        break;
                    } else {
                        port = 1;
                        check = true;
                    }
                }
            }
        }
        if (!Utils.isPortAvailable(port)) {
            Toast.makeText(this, R.string.ftp_toast_port, Toast.LENGTH_SHORT).show();
            return super.onStartCommand(intent, flags, startId);
        }
        final String path = config.getPath();
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, R.string.ftp_toast_no_root, Toast.LENGTH_SHORT).show();
            return super.onStartCommand(intent, flags, startId);
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                !ContextUtils.hasWritePermission(this, path)) {
            Toast.makeText(this, R.string.ftp_toast_no_permission, Toast.LENGTH_SHORT).show();
            return super.onStartCommand(intent, flags, startId);
        }
        mFTP = FtpServer.createServer(port, path);
        try {
            mFTP.start();
        } catch (Exception e) {
            mFTP = null;
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            stopSelf(startId);
        }
        final String title = getString(R.string.ftp_notification_title);
        final String text = getString(R.string.ftp_notification_text,
                ContextUtils.getWifiIp(this), port);
        // TODO 前台服务需要权限
//        startForeground(NotificationMaker.ID_FTP,
//                NotificationMaker.getFTPRunning(this, title, text,
//                        PendingIntent.getActivity(this, NotificationMaker.ID_FTP,
//                                FtpActivity.getStarter(this),
//                                PendingIntent.FLAG_UPDATE_CURRENT)));
        STARTED = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        if (mFTP != null) {
            mFTP.stop();
            mFTP = null;
        }
        STARTED = false;
        super.onDestroy();
    }

    protected void onConnectivityChanged() {
        if (mAutoClose && !ContextUtils.isWifiConnected(this)) {
            stopSelf();
        }
    }
}
