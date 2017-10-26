package am.project.x.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.storage.StorageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import am.project.support.compat.AMStorageManagerCompat;

/**
 * 文件存储工具类
 * Created by Alex on 2016/10/10.
 */
public class FileUtils {

    /**
     * 通过路径获取文件名
     *
     * @param path 路径
     * @return 文件名
     */
    public static String getFileName(String path) {
        if (path == null)
            return null;
        if (path.contains("/"))
            return path.substring(path.lastIndexOf("/") + 1, path.length());
        return path;
    }

    /**
     * 通过路径获取文件所在文件夹路径
     *
     * @param path 路径
     * @return 文件所在文件夹路径
     */
    public static String getFileDir(String path) {
        if (path == null)
            return null;
        if (path.contains("/"))
            return path.substring(0, path.lastIndexOf("/"));
        return path;
    }

    /**
     * 获取路径根目录
     *
     * @param manager 存储设备管理器
     * @param path    路径
     * @return 根目录
     */
    public static String getPathStorage(StorageManager manager, String path) {
        if (StringUtils.isNullOrEmpty(path))
            return null;
        List<AMStorageManagerCompat.StorageVolumeImpl> volumes =
                AMStorageManagerCompat.getEmulatedStorageVolumes(manager);
        if (volumes.size() < 1)
            return null;
        String storage = null;
        for (AMStorageManagerCompat.StorageVolumeImpl volume : volumes) {
            final String storagePath = volume.getPath();
            if (path.startsWith(storagePath)) {
                storage = storagePath;
                break;
            }
        }
        return storage;
    }

    /**
     * 复制文件
     *
     * @param source      源文件
     * @param destination 目标文件
     * @return 是否成功
     */
    public static boolean copyFile(File source, File destination) {
        if (source == null || !source.exists() || !source.canRead())
            return false;
        if (destination == null || !destination.exists() || !destination.canWrite())
            return false;
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = input.read(buffer)) != -1) {
                if (count == 0) {
                    count = input.read();
                    if (count < 0)
                        break;
                    output.write(count);
                    continue;
                }
                output.write(buffer, 0, count);
            }
            return true;
        } catch (Exception e) {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
                return false;
            } catch (IOException ioe) {
                return false;
            }
        }
    }

    /**
     * 删除文件
     *
     * @param source 源文件
     * @return 是否成功
     */
    public static boolean deleteFile(File source) {
        if (source == null || !source.exists())
            return true;
        if (source.isFile())
            return source.delete();
        if (source.isDirectory()) {
            File[] childFile = source.listFiles();
            if (childFile == null || childFile.length == 0) {
                return source.delete();
            }
            for (File child : childFile) {
                deleteFile(child);
            }
            return source.delete();
        }
        return source.delete();
    }


    /**
     * 移动文件并重命名
     *
     * @param file 文件
     * @param dir  目录
     * @param name 名字
     * @return 是否成功
     */
    public static boolean moveFile(File file, File dir, String name) {
        return !(file == null || !file.exists() || !file.canWrite()) &&
                !(dir == null || !dir.exists() || !dir.canWrite() || !dir.isDirectory()) &&
                file.renameTo(new File(dir, name));
    }

    /**
     * 重命名文件
     *
     * @param file 文件
     * @param name 文件名
     * @return 是否成功
     */
    public static boolean renameFile(File file, String name) {
        if (file == null || !file.exists() || !file.canWrite())
            return false;
        final File dest = new File(getFileDir(file.getPath()), name);
        return !dest.exists() && file.renameTo(dest);
    }

    /**
     * 存储图片到文件
     *
     * @param file 文件
     * @param crop 裁剪图片
     * @return 是否成功
     */
    public static boolean saveImage(File file, Bitmap crop) {
        FileOutputStream output;
        try {
            output = new FileOutputStream(file);
            crop.compress(Bitmap.CompressFormat.JPEG, 100, output);// TODO
        } catch (FileNotFoundException e) {
            return false;
        }
        try {
            output.flush();
            output.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否具备文件权限
     *
     * @param context Context
     * @param paths   路径
     * @return 是否具备
     */
    public static boolean hasFilePermission(Context context, String... paths) {
        if (Build.VERSION.SDK_INT < 23 || ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
            return true;
        File[] fileDirs = ContextCompat.getExternalFilesDirs(context, null);
        final ArrayList<File> appDirs = new ArrayList<>();
        for (File dir : fileDirs) {
            appDirs.add(dir.getParentFile());
        }
        for (String path : paths) {
            if (StringUtils.isNullOrEmpty(path))
                continue;
            for (File app : appDirs) {
                if (!path.startsWith(app.getPath())) {
                    return false;
                }
            }
        }
        return true;
    }
}
