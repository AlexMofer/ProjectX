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
package am.project.x.broadcast;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import am.project.x.ProjectXApplication;

/**
 * 应用内广播辅助器
 * Created by Alex on 2018/7/23.
 */
public class LocalBroadcastHelper {

    public static final String ACTION_FTP_STARTED = "am.project.x.action.ACTION_FTP_STARTED";
    public static final String ACTION_FTP_STOPPED = "am.project.x.action.ACTION_FTP_STOPPED";

    private LocalBroadcastHelper() {
        //no instance
    }

    /**
     * 发送本地广播
     *
     * @param action 要发送的广播动作
     */
    public static void sendBroadcast(@NonNull String action) {
        sendBroadcast(new Intent(action));
    }

    /**
     * 发送本地广播
     *
     * @param intent Intent
     */
    @SuppressWarnings("WeakerAccess")
    public static void sendBroadcast(@NonNull Intent intent) {
        final ProjectXApplication application = ProjectXApplication.getInstance();
        if (application == null)
            return;
        final LocalBroadcastManager manager = application.getLocalBroadcastManager();
        if (manager == null)
            return;
        manager.sendBroadcast(intent);
    }

}
