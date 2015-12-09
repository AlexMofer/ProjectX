package com.am.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * 使用授权
 * 
 * @author Mofer
 * 
 */
public class Authorization {

	private static final long DAY_LOG = 1480521600000L;// 2016-12-01
	private static final long DAY_TOAST = 1482854400000L;// 2016-12-28
	private static final long DAY_UNAVAILABLE = 1483200000000L;// 2017-01-01

	public static void hasAuthorization(Context context) {
		final long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis < DAY_LOG) {
			// do nothing
		} else if (currentTimeMillis >= DAY_LOG
				&& currentTimeMillis < DAY_TOAST) {
			Log.e("Authorization Error", "Authorization will expire soon.");
		} else if (currentTimeMillis >= DAY_TOAST
				&& currentTimeMillis < DAY_UNAVAILABLE) {
			Toast.makeText(context, "Authorization will expire soon.",
					Toast.LENGTH_SHORT).show();
		} else {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

	public static void logDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.DAY_OF_YEAR, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		Log.i("Authorization", sdf.format(calendar.getTime()));
		// 输出过期日期
		// System.out.println(sdf.format(calendar.getTime()));
		// System.out.println(calendar.getTimeInMillis());
		// // Toast提示日期
		// calendar.set(Calendar.DAY_OF_YEAR, -3);
		// System.out.println(sdf.format(calendar.getTime()));
		// System.out.println(calendar.getTimeInMillis());
		// //Log提示日期
		// calendar.set(Calendar.DAY_OF_YEAR, 3);
		// calendar.set(Calendar.MONTH, -1);
		// System.out.println(sdf.format(calendar.getTime()));
		// System.out.println(calendar.getTimeInMillis());
	}
}
