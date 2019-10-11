/*
 * Copyright (C) 2019 AlexMofer
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

package am.util.ftpserver;

import androidx.documentfile.provider.DocumentFile;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 工具
 * Created by Alex on 2019/10/11.
 */
final class Utils {

    private static final String BASE_NAME = "PDFelement_ftp_server_create_";
    private static final String MIME = "text/plain";
    private static final Object LOCK = new Object();
    private static byte[] BUFFER = null;

    private Utils() {
        //no instance
    }

    /**
     * 创建文件
     *
     * @param dir  目录
     * @param name 文件名
     * @return 创建的文件，失败时返回null
     */
    static DocumentFile createNewFile(final DocumentFile dir, String name) {
        for (int i = 0; i < 5; i++) {
            final DocumentFile file = dir.createFile(MIME,
                    BASE_NAME + System.currentTimeMillis());
            if (file != null) {
                // 创建成功
                if (file.renameTo(name))
                    return file;
                else {
                    // 重命名失败，删除并返回
                    file.delete();
                    return null;
                }
            }
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    /**
     * 复制
     *
     * @param output 输出
     * @param input  输入
     * @return 是否成功
     */
    static boolean copy(OutputStream output, InputStream input) {
        if (output == null || input == null)
            return false;
        final byte[] buffer;
        synchronized (LOCK) {
            if (BUFFER == null)
                buffer = new byte[1024];
            else
                buffer = BUFFER;
            BUFFER = null;
        }
        try {
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
                output.flush();
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            synchronized (LOCK) {
                BUFFER = buffer;
            }
        }
    }
}
