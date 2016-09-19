#getApplicationInfo的坑
一般情况下我们通过PackageManager.GET_UNINSTALLED_PACKAGES来检查应用是否安装，且不论其本身存在的不正确性，自Android 4.2（API 17）以后，多账户的出现，其又新出现一些坑。现在Android 7.0（API 24）使用MATCH_UNINSTALLED_PACKAGES 将其替换。

##问题描述
在做微信分享操作之前，应用内先对微信App进行一次判断是否安装，使用的方法：
```java
public boolean checkApkExist(Context context, String packageName) {
    try {
        ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
            PackageManager.GET_UNINSTALLED_PACKAGES);
        if (info != null)
            return true;
    } catch (PackageManager.NameNotFoundException e) {
        return false;
    }
    return false;
}
```
在我的设备上存在两个账户，一个管理员账户用于日常使用，另一个普通账户用于开发，在我的管理员账户中安装了微信，而普通账户里面没有安装微信，但是在普通账户里运行这段代码，其会告诉你安装了微信。

##问题分析
从API 24的源码可以看到，其实GET_UNINSTALLED_PACKAGES标志仅仅用于查询在手机系统分区存在数据目录的应用：
```java
/**
 * @deprecated replaced with {@link #MATCH_UNINSTALLED_PACKAGES}
 */
@Deprecated
public static final int GET_UNINSTALLED_PACKAGES = 0x00002000;

/**
 * Flag parameter to retrieve some information about all applications (even
 * uninstalled ones) which have data directories. This state could have
 * resulted if applications have been deleted with flag
 * {@code DONT_DELETE_DATA} with a possibility of being replaced or
 * reinstalled in future.
 * <p>
 * Note: this flag may cause less information about currently installed
 * applications to be returned.
 */
public static final int MATCH_UNINSTALLED_PACKAGES = 0x00002000;
```
其实这个标签都不一定能保证应用就一定安装着，只能说应用是否安装过，而多账户下就更不能保证这个账户下就一定安装过了，因为可以是另一个账户下安装过。

##问题解决
- 尽量不要使用GET_UNINSTALLED_PACKAGES或者MATCH_UNINSTALLED_PACKAGES去判断应用是否已安装或者安装过。
- 微信的API自有判断是否安装的方法。