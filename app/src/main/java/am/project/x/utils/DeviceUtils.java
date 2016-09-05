package am.project.x.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

/**
 * 设备工具类
 * Created by Alex on 2015/8/20.
 */
@SuppressWarnings("unused")
public class DeviceUtils {

    public static final String NO_DEVICEID = "Cannot create an unique deviceId";
    public static final String BAD_DEVICEID = "000000000000000";
    public static final String BAD_ANDROIDSERIAL = "unknown";
    public static final String BAD_ANDROIDID = "9774d56d682e549c";
    private static final String PR_DEVICEID = "di_";
    private static final String PR_ANDROIDSERIAL = "as_";
    private static final String PR_WIFIMACADDRESS = "wma_";
    private static final String PR_BLUETOOTHMACADDRESS = "bma_";
    private static final String PR_ANDROIDID = "ai_";


    /**
     * Returns the unique device ID, for example, the IMEI for GSM and the MEID
     * or ESN for CDMA phones. Return null if device ID is not available.
     * 需要 android.permission.READ_PHONE_STATE 权限
     * 非手机设备无TELEPHONY_SERVICE
     * 厂商定制系统中的Bug：少数手机设备上，由于该实现有漏洞，会返回垃圾。
     *
     * @param context Context
     * @return 返回通讯设备的唯一ID
     */
    public static String getDeviceId(Context context) {
        try {
            return ((TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * A hardware serial number, if available.  Alphanumeric only, case-insensitive.
     * 需要 android.permission.READ_PHONE_STATE 权限
     */
    public static String getAndroidSerial() {
        return android.os.Build.SERIAL;
    }

    /**
     * 不是所有Android设备都有WiFi
     * 对于有WiFi的设备，如果WiFi没有开启，可能获取不了WiFi MAC地址
     * 需要android.permission.ACCESS_WIFI_STATE权限
     *
     * @param context Context
     * @return WiFi Mac地址
     */
    @SuppressWarnings("all")
    public static String getWiFiMACAddress(Context context) {
        try {
            return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                    .getConnectionInfo().getMacAddress();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取蓝牙MAC地址
     * 不是所有Android设备都有BlueTooth
     * 如果没有开启BlueTooth，可能获取不了BlueTooth MAC地址
     * 需要BLUETOOTH权限
     *
     * @return 蓝牙MAC地址
     */
    public static String getBlueToothMACAddress() {
        try {
            return BluetoothAdapter.getDefaultAdapter().getAddress();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Android Id
     * 64位的十六进制字符串，设备首次启动时会随时生成
     * 如果设备恢复了出厂设置，这个值可能会改变
     * 设备root了之后，这个值可以手动修改
     * Android 2.2发现bug，部分设备具有相同Android ID（9774d56d682e549c），模拟器的Android ID也是这个
     * 这个值有时会为null
     * 一般不推荐使用
     *
     * @param context Context
     * @return Android Id
     */
    public static String getAndroidId(Context context) {
        return Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
    }

    /**
     * 获取设备唯一ID
     *
     * @param context Context
     * @return 设备唯一ID
     */
    public static String getDeviceUUID(Context context) {
        String deviceUUID = getDeviceId(context);
        if (deviceUUID != null && !BAD_DEVICEID.equals(deviceUUID)) {
            return PR_DEVICEID + deviceUUID;
        }
        deviceUUID = getAndroidSerial();
        if (deviceUUID != null && !BAD_ANDROIDSERIAL.equals(deviceUUID)) {
            return PR_ANDROIDSERIAL + deviceUUID;
        }
        deviceUUID = getWiFiMACAddress(context);
        if (deviceUUID != null) {
            return PR_WIFIMACADDRESS + deviceUUID;
        }
        deviceUUID = getBlueToothMACAddress();
        if (deviceUUID != null) {
            return PR_BLUETOOTHMACADDRESS + deviceUUID;
        }
        deviceUUID = getAndroidId(context);
        if (deviceUUID != null && !BAD_ANDROIDID.equals(deviceUUID)) {
            return PR_ANDROIDID + deviceUUID;
        }
        return NO_DEVICEID;
    }

    /**
     * 计算设备唯一ID(MD5)
     *
     * @param context     Context
     * @param charsetName 编码
     * @param value       后缀字符串
     * @return 唯一字符串MD5值
     */
    public static String md5DeviceUUID(Context context, String charsetName, String value) {
        String deviceUUID = value == null ? getDeviceUUID(context) : getDeviceUUID(context) + value;
        return StringUtils.getMD5(deviceUUID, charsetName, true);
    }
}
