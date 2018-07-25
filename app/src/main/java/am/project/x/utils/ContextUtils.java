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
package am.project.x.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Context工具类
 */
public class ContextUtils {

    private ContextUtils() {
        //no instance
    }

    /**
     * 打开浏览器访问网址
     *
     * @param context Context
     * @param url     网址
     */
    public static void openBrowser(Context context, String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    /**
     * 打开电子邮件发送邮件
     *
     * @param context    Context
     * @param subject    主题
     * @param attachment 附件
     * @param addresses  邮箱地址
     */
    public static void sendEmail(Context context, @Nullable String subject,
                                 @Nullable Uri attachment, String... addresses) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        if (subject != null)
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (attachment != null)
            intent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
