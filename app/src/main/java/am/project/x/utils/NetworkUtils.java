package am.project.x.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetworkUtils {

	/**
	 * 网络类型
	 * @author Mofer
	 *
	 */
	public enum NetType {
		TYPE_NONET, TYPE_UNKNOWN, TYPE_WIFI, TYPE_2G, TYPE_3G, TYPE_4G;
	}
	
	/**
	 * 网络是否已连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
			return ni != null && ni.isConnected();
		}
		return false;
	}

	/**
	 * 网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnectedOrConnecting(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
			return ni != null && ni.isConnectedOrConnecting();
		} else {
			Log.e("NetworkUtils", "Can't get ConnectivityManager");
		}
		return false;
	}

	/**
	 * 检查WIFI是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnectedOrConnecting(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
			return ni != null && ni.isConnectedOrConnecting()
					&& ni.getType() == ConnectivityManager.TYPE_WIFI;
		} else {
			Log.e("NetworkUtils", "Can't get ConnectivityManager");
		}
		return false;
	}
	
	/**
	 * 检查WIFI已连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
			return ni != null && ni.isConnected()
					&& ni.getType() == ConnectivityManager.TYPE_WIFI;
		} else {
			Log.e("NetworkUtils", "Can't get ConnectivityManager");
		}
		return false;
	}

	/**
	 * 检查手机网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobileisConnectedOrConnecting(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
			return ni != null && ni.isConnectedOrConnecting()
					&& ni.getType() == ConnectivityManager.TYPE_MOBILE;
		} else {
			Log.e("NetworkUtils", "Can't get ConnectivityManager");
		}
		return false;
	}
	
	/**
	 * 检查手机网络已连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobileisConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
			return ni != null && ni.isConnected()
					&& ni.getType() == ConnectivityManager.TYPE_MOBILE;
		} else {
			Log.e("NetworkUtils", "Can't get ConnectivityManager");
		}
		return false;
	}

	/**
	 * 获取手机网络类型
	 * @param context
	 * @return
	 */
	public static NetType getMobileNetworkType(Context context) {
	    TelephonyManager mTelephonyManager = (TelephonyManager)
	            context.getSystemService(Context.TELEPHONY_SERVICE);
	    int networkType = mTelephonyManager.getNetworkType();
	    switch (networkType) {
	        case TelephonyManager.NETWORK_TYPE_GPRS: // ~ 100 kbps
	        case TelephonyManager.NETWORK_TYPE_EDGE: // ~ 50-100 kbps
	        case TelephonyManager.NETWORK_TYPE_CDMA: // ~ 14-64 kbps
	        case TelephonyManager.NETWORK_TYPE_1xRTT:// ~ 50-100 kbps
	        case TelephonyManager.NETWORK_TYPE_IDEN: // ~25 kbps
	            return NetType.TYPE_2G;
	        case TelephonyManager.NETWORK_TYPE_UMTS: // ~ 400-7000 kbps
	        case TelephonyManager.NETWORK_TYPE_EVDO_0: // ~ 400-1000 kbps
	        case TelephonyManager.NETWORK_TYPE_EVDO_A: // ~ 600-1400 kbps
	        case TelephonyManager.NETWORK_TYPE_HSDPA: // ~ 2-14 Mbps
	        case TelephonyManager.NETWORK_TYPE_HSUPA: // ~ 1-23 Mbps
	        case TelephonyManager.NETWORK_TYPE_HSPA: // ~ 700-1700 kbps
	        case TelephonyManager.NETWORK_TYPE_EVDO_B: // ~ 5 Mbps
	        case TelephonyManager.NETWORK_TYPE_EHRPD: // ~ 1-2 Mbps
	        case TelephonyManager.NETWORK_TYPE_HSPAP: // ~ 10-20 Mbps
	            return NetType.TYPE_3G;
	        case TelephonyManager.NETWORK_TYPE_LTE: // ~ 10+ Mbps
	            return NetType.TYPE_4G;
	        default:
	            return NetType.TYPE_UNKNOWN;
	    }
	}
	
	/**
	 * 获取网络类型
	 * @param context
	 * @return
	 */
	public static NetType getNetworkType(Context context) {
		if (isConnected(context)) {
			if (isWifiConnected(context)) {
				return NetType.TYPE_WIFI;
			} else if (isMobileisConnected(context)) {
				return getMobileNetworkType(context);
			} else {
				return NetType.TYPE_UNKNOWN;
			}
		} else {
			return NetType.TYPE_NONET;
		}
	}

}
