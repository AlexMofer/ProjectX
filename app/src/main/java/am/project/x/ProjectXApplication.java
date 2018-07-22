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
package am.project.x;

import am.project.x.base.BroadcastApplication;

/**
 * 应用Application
 * 做一些全局变量存储
 * Created by Alex on 2018/7/23.
 */
public class ProjectXApplication extends BroadcastApplication {

    @SuppressWarnings("all")
    private static ProjectXApplication mInstance;

    @Override
    public void onCreate() {
        mInstance = this;
        super.onCreate();
    }

    /**
     * 获取Application
     *
     * @return Application
     */
    public static ProjectXApplication getInstance() {
        return mInstance;
    }
}
