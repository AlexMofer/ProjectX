package am.project.x.utils;

import android.content.Context;

import java.io.File;

/**
 * 文件存储工具类
 * Created by Alex on 2016/10/10.
 */

public class FileUtils {

    public static File getExternalFilesDir(Context context, String type) {
        File dirs = context.getExternalFilesDir(type);
        if (dirs != null)
            return dirs;
        return context.getFilesDir();
    }
}
