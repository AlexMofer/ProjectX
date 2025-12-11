/*
 * Copyright (C) 2023 AlexMofer
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
package io.github.alexmofer.android.support.utils;

import android.accounts.Account;
import android.accounts.AccountManager;

/**
 * 用户数据辅助
 * Created by Alex on 2023/1/12.
 */
@SuppressWarnings("ALL")
public class UserDataUtils {

    private UserDataUtils() {
        //no instance
    }

    /**
     * 保存用户数据
     *
     * @param manager 账户管理器
     * @param account 账户
     * @param key     数据Key
     * @param value   数据值
     * @return 保存成功时返回true
     */
    public static boolean setUserData(AccountManager manager, Account account,
                                      String key, String value) {
        try {
            manager.setUserData(account, key, value);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 获取用户数据
     *
     * @param manager      账户管理器
     * @param account      账户
     * @param key          数据Key
     * @param defaultValue 数据默认值
     * @return 数据值
     */
    public static String getUserData(AccountManager manager, Account account,
                                     String key, String defaultValue) {
        final String value = manager.getUserData(account, key);
        return value == null ? defaultValue : value;
    }

    /**
     * 保存用户数据
     *
     * @param manager 账户管理器
     * @param account 账户
     * @param key     数据Key
     * @param value   数据值
     * @return 保存成功时返回true
     */
    public static boolean setUserData(AccountManager manager, Account account,
                                      String key, int value) {
        return setUserData(manager, account, key, Integer.toString(value));
    }

    /**
     * 获取用户数据
     *
     * @param manager      账户管理器
     * @param account      账户
     * @param key          数据Key
     * @param defaultValue 数据默认值
     * @return 数据值
     */
    public static int getUserData(AccountManager manager, Account account,
                                  String key, int defaultValue) {
        final String value = manager.getUserData(account, key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    /**
     * 保存用户数据
     *
     * @param manager 账户管理器
     * @param account 账户
     * @param key     数据Key
     * @param value   数据值
     * @return 保存成功时返回true
     */
    public static boolean setUserData(AccountManager manager, Account account,
                                      String key, long value) {
        return setUserData(manager, account, key, Long.toString(value));
    }

    /**
     * 获取用户数据
     *
     * @param manager      账户管理器
     * @param account      账户
     * @param key          数据Key
     * @param defaultValue 数据默认值
     * @return 数据值
     */
    public static long getUserData(AccountManager manager, Account account,
                                   String key, long defaultValue) {
        final String value = manager.getUserData(account, key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    /**
     * 保存用户数据
     *
     * @param manager 账户管理器
     * @param account 账户
     * @param key     数据Key
     * @param value   数据值
     * @return 保存成功时返回true
     */
    public static boolean setUserData(AccountManager manager, Account account,
                                      String key, float value) {
        return setUserData(manager, account, key, Float.toString(value));
    }

    /**
     * 获取用户数据
     *
     * @param manager      账户管理器
     * @param account      账户
     * @param key          数据Key
     * @param defaultValue 数据默认值
     * @return 数据值
     */
    public static float getUserData(AccountManager manager, Account account,
                                    String key, float defaultValue) {
        final String value = manager.getUserData(account, key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(value);
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    /**
     * 保存用户数据
     *
     * @param manager 账户管理器
     * @param account 账户
     * @param key     数据Key
     * @param value   数据值
     * @return 保存成功时返回true
     */
    public static boolean setUserData(AccountManager manager, Account account,
                                      String key, double value) {
        return setUserData(manager, account, key, Double.toString(value));
    }

    /**
     * 获取用户数据
     *
     * @param manager      账户管理器
     * @param account      账户
     * @param key          数据Key
     * @param defaultValue 数据默认值
     * @return 数据值
     */
    public static double getUserData(AccountManager manager, Account account,
                                     String key, double defaultValue) {
        final String value = manager.getUserData(account, key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    /**
     * 保存用户数据
     *
     * @param manager 账户管理器
     * @param account 账户
     * @param key     数据Key
     * @param value   数据值
     * @return 保存成功时返回true
     */
    public static boolean setUserData(AccountManager manager, Account account,
                                      String key, boolean value) {
        return setUserData(manager, account, key, Boolean.toString(value));
    }

    /**
     * 获取用户数据
     *
     * @param manager      账户管理器
     * @param account      账户
     * @param key          数据Key
     * @param defaultValue 数据默认值
     * @return 数据值
     */
    public static boolean getUserData(AccountManager manager, Account account,
                                      String key, boolean defaultValue) {
        final String value = manager.getUserData(account, key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (Throwable t) {
            return defaultValue;
        }
    }
}
