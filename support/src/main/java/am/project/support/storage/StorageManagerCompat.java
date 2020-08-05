package am.project.support.storage;

import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * StorageManager兼容器
 * API 28 引入灰名单 所有hide方法均可访问
 * API 29 强化灰名单 少量不重要hide方法已禁止访问
 * API 30 进一步强化灰名单 部分重要hide方法已禁止访问（待正式版检查）
 * Created by Alex on 2020/8/4.
 */
@SuppressWarnings("unused")
public class StorageManagerCompat {

    /**
     * 获取外部存储
     * API 14及以上
     *
     * @param manager 存储管理器
     * @return 外部存储
     */
    public static List<StorageVolumeCompat> getStorageVolumes(StorageManager manager) {
        final int api = Build.VERSION.SDK_INT;
        if (api >= Build.VERSION_CODES.R) {
            // 由于getPath方法已禁止反射，因此需要另外进行路径获取
            final List<StorageVolume> volumes = manager.getStorageVolumes();
            final int size = volumes.size();
            if (size <= 0)
                return new ArrayList<>();
            if (size == 1) {
                // 单个
                final ArrayList<StorageVolumeCompat> result = new ArrayList<>();
                result.add(new StorageVolumeCompat(volumes.get(0),
                        Environment.getExternalStorageDirectory().getPath()));
                return result;
            }
            final String[] paths = getVolumePaths(manager);
            final ArrayList<StorageVolumeCompat> result = new ArrayList<>();
            if (paths != null && paths.length > 0) {
                for (String path : paths) {
                    final StorageVolume volume = manager.getStorageVolume(new File(path));
                    if (volume != null) {
                        volumes.remove(volume);
                        result.add(new StorageVolumeCompat(volume, path));
                    }
                }
            } else {
                StorageVolume primary = null;
                for (StorageVolume volume : volumes) {
                    if (volume.isPrimary()) {
                        primary = volume;
                        break;
                    }
                }
                if (primary != null) {
                    volumes.remove(primary);
                    result.add(new StorageVolumeCompat(primary,
                            Environment.getExternalStorageDirectory().getPath()));
                }
            }
            if (!volumes.isEmpty()) {
                for (StorageVolume volume : volumes) {
                    result.add(new StorageVolumeCompat(volume));// 无法获取Path的外部存储
                }
            }
            return result;
        } else if (api >= Build.VERSION_CODES.N) {
            final List<StorageVolume> volumes = manager.getStorageVolumes();
            final ArrayList<StorageVolumeCompat> result = new ArrayList<>();
            for (StorageVolume volume : volumes) {
                result.add(new StorageVolumeCompat(volume));
            }
            return result;
        } else if (api >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // Method removed at api 24
            // Method created at api 14
            final ArrayList<StorageVolumeCompat> result = new ArrayList<>();
            Object[] volumes;
            try {
                volumes = (Object[]) manager.getClass().getMethod("getVolumeList").invoke(manager);
            } catch (Exception e) {
                volumes = null;
            }
            if (volumes != null) {
                for (Object volume : volumes) {
                    if (volume instanceof Parcelable)
                        result.add(new StorageVolumeCompat((Parcelable) volume));
                }
            }
            return result;
        } else {
            // 不支持
            return new ArrayList<>();
        }
    }

    /**
     * 返回所有可挂载的外部存储的路径集
     * API 14及以上
     * 灰名单，允许访问
     *
     * @param manager 存储管理器
     * @return 路径集
     */
    public static String[] getVolumePaths(StorageManager manager) {
        final int api = Build.VERSION.SDK_INT;
        if (api >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // Method deprecated at api 23
            // Method created at api 14
            try {
                return (String[]) manager.getClass().getMethod("getVolumePaths").invoke(manager);
            } catch (Exception e) {
                // ignore
            }
        }
        // 不支持
        return new String[0];
    }

    /**
     * 获取主外部存储
     * API 17及以上
     *
     * @param manager 存储管理器
     * @return 主外部存储
     */
    public static StorageVolumeCompat getPrimaryStorageVolume(StorageManager manager) {
        final int api = Build.VERSION.SDK_INT;
        if (api >= Build.VERSION_CODES.R) {
            // 由于getPath方法已禁止反射，因此需要另外进行路径获取
            return new StorageVolumeCompat(manager.getPrimaryStorageVolume(),
                    Environment.getExternalStorageDirectory().getPath());
        } else if (api >= Build.VERSION_CODES.N) {
            return new StorageVolumeCompat(manager.getPrimaryStorageVolume());
        } else if (api >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Method removed at api 24
            // Method created at api 17
            Object volume;
            try {
                volume = manager.getClass().getMethod("getPrimaryVolume").invoke(manager);
            } catch (Exception e) {
                volume = null;
            }
            if (volume instanceof Parcelable)
                return new StorageVolumeCompat((Parcelable) volume);
        }
        // 不支持
        return null;
    }

    /**
     * 获取包含该文件的外部存储设备，查询不到或不支持时返回空
     * API 23及以上
     *
     * @param manager 存储管理器
     * @param file    文件
     * @return 外部存储
     */
    public static StorageVolumeCompat getStorageVolume(StorageManager manager, File file) {
        final int api = Build.VERSION.SDK_INT;
        if (api >= Build.VERSION_CODES.N) {
            return new StorageVolumeCompat(manager.getStorageVolume(file));
        } else if (api >= Build.VERSION_CODES.M) {
            Object volume;
            try {
                volume = manager.getClass().getMethod("getStorageVolume", File.class)
                        .invoke(manager, file);
            } catch (Exception e) {
                volume = null;
            }
            if (volume instanceof Parcelable)
                return new StorageVolumeCompat((Parcelable) volume);
        }
        return null;
    }

    /**
     * 获取外部存储设备兼容器
     *
     * @param volume 外部存储设备
     * @param path   外部存储根路径（API 30及以后无法再通过反射获取）
     * @return 外部存储设备兼容器
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static StorageVolumeCompat getStorageVolume(StorageVolume volume,
                                                       @Nullable String path) {
        return new StorageVolumeCompat(volume, path);
    }
}
