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
package am.project.x.business.others.ftp;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import org.apache.ftpserver.FtpServer;

import java.util.ArrayList;

import am.project.x.R;
import am.project.x.broadcast.LocalBroadcastHelper;
import am.project.x.notification.NotificationMaker;
import am.project.x.utils.ContextUtils;
import am.project.x.utils.Utils;
import am.util.ftpserver.FTPHelper;
import am.util.ftpserver.FTPUser;

/**
 * 文件传输服务
 */
public class FTPService extends Service {
    private static final String EXTRA_PORT = "port";
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

    public FTPService() {
    }

    public static void start(Context context, int port) {
        context.startService(new Intent(context, FTPService.class).putExtra(EXTRA_PORT, port));
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, FTPService.class));
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
        mAutoClose = true;
        if (STARTED || !ContextUtils.hasWriteExternalStoragePermission(this)) {
            return super.onStartCommand(intent, flags, startId);
        }
        int port = intent.getIntExtra(EXTRA_PORT, 2020);
        if (port <= 0 || port > 65535)
            port = 2020;
        while (!Utils.isPortAvailable(port)) {
            port++;
            if (port > 65535) {
                port = 2020;
                break;
            }
        }
        final int maxLoginFailures = 5;
        final int loginFailureDelay = 60000;
        final boolean anonymousEnable = true;
        final String directory = Environment.getExternalStorageDirectory().getAbsolutePath();
        final ArrayList<FTPUser> users = null;
        mFTP = FTPHelper.createServer(port, maxLoginFailures, loginFailureDelay, anonymousEnable,
                directory, users);
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
        startForeground(NotificationMaker.ID_FTP,
                NotificationMaker.getFTPRunning(this, title, text,
                        PendingIntent.getActivity(this, NotificationMaker.ID_FTP,
                                FTPActivity.getStarter(this),
                                PendingIntent.FLAG_UPDATE_CURRENT)));
        STARTED = true;
        LocalBroadcastHelper.sendBroadcast(LocalBroadcastHelper.ACTION_FTP_STARTED);
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
        LocalBroadcastHelper.sendBroadcast(LocalBroadcastHelper.ACTION_FTP_STOPPED);
    }

    protected void onConnectivityChanged() {
        if (mAutoClose && !ContextUtils.isWifiConnected(this)) {
            stopSelf();
        }
    }
}
