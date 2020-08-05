/*
 * Copyright (C) 2020 AlexMofer
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

package am.project.support.storage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.File;

/**
 * StorageVolume兼容器
 * Created by Alex on 2020/8/4.
 */
@SuppressWarnings("unused")
public class StorageVolumeCompat implements Parcelable {

    public static final Creator<StorageVolumeCompat> CREATOR = new Creator<StorageVolumeCompat>() {
        @Override
        public StorageVolumeCompat createFromParcel(Parcel source) {
            return new StorageVolumeCompat(source);
        }

        @Override
        public StorageVolumeCompat[] newArray(int size) {
            return new StorageVolumeCompat[size];
        }
    };
    private final Parcelable mVolume;
    private final String mPath;
    private final File mFile;

    protected StorageVolumeCompat(Parcelable volume) {
        mVolume = volume;
        mPath = null;
        mFile = null;
    }

    protected StorageVolumeCompat(Parcelable volume, String path) {
        mVolume = volume;
        mPath = path;
        mFile = mPath == null ? null : new File(mPath);
    }

    protected StorageVolumeCompat(Parcel in) {
        mPath = in.readString();
        mFile = mPath == null ? null : new File(mPath);
        Parcelable volume = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            volume = in.readParcelable(StorageVolume.class.getClassLoader());
        } else {
            try {
                volume = in.readParcelable(Class.forName("android.os.storage.StorageVolume")
                        .getClassLoader());
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        mVolume = volume;
    }

    /**
     * 获取ID
     * API 23及以上
     *
     * @return ID
     */
    public String getId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                return (String) mVolume.getClass().getMethod("getId").invoke(mVolume);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    /**
     * Returns the mount path for the volume.
     * API 14及以上
     * API 30 目前灰名单，拒绝访问，因此可能出现无法获取的情况，一般只出现在Secondary External Storage上。
     *
     * @return the mount path
     */
    @Nullable
    public String getPath() {
        if (mPath != null)
            return mPath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                return (String) mVolume.getClass().getMethod("getPath").invoke(mVolume);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    /**
     * 获取路径文件
     * API 14及以上
     * API 30 目前灰名单，拒绝访问，因此可能出现无法获取的情况，一般只出现在Secondary External Storage上。
     *
     * @return 文件
     */
    @Nullable
    public File getPathFile() {
        if (mFile != null)
            return mFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                return (File) mVolume.getClass().getMethod("getPathFile").invoke(mVolume);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    /**
     * Returns a user-visible description of the volume.
     * API 14及以上
     *
     * @return the volume description
     */
    public String getDescription(Context context) {
        final int api = Build.VERSION.SDK_INT;
        if (api >= Build.VERSION_CODES.N) {
            return ((StorageVolume) mVolume).getDescription(context);
        } else if (api >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                return (String) mVolume.getClass().getMethod("getDescription", Context.class)
                        .invoke(mVolume, context);
            } catch (Exception e) {
                // ignore
            }
        } else if (api >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                return (String) mVolume.getClass().getMethod("getDescription")
                        .invoke(mVolume);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    /**
     * Returns true if the volume is the primary shared/external storage, which is the volume
     * backed by {@link Environment#getExternalStorageDirectory()}.
     * API 17及以上
     */
    public boolean isPrimary() {
        final int api = Build.VERSION.SDK_INT;
        if (api >= Build.VERSION_CODES.N) {
            return ((StorageVolume) mVolume).isPrimary();
        } else if (api >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                return (boolean) mVolume.getClass().getMethod("isPrimary").invoke(mVolume);
            } catch (Exception e) {
                // ignore
            }
        }
        return false;
    }

    /**
     * Returns true if the volume is removable.
     * API 14及以上
     *
     * @return is removable
     */
    public boolean isRemovable() {
        final int api = Build.VERSION.SDK_INT;
        if (api >= Build.VERSION_CODES.N) {
            return ((StorageVolume) mVolume).isRemovable();
        } else if (api >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                return (boolean) mVolume.getClass().getMethod("isRemovable").invoke(mVolume);
            } catch (Exception e) {
                // ignore
            }
        }
        return false;
    }

    /**
     * Returns true if the volume is emulated.
     * API 14及以上
     *
     * @return is removable
     */
    public boolean isEmulated() {
        final int api = Build.VERSION.SDK_INT;
        if (api >= Build.VERSION_CODES.N) {
            return ((StorageVolume) mVolume).isEmulated();
        } else if (api >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                return (boolean) mVolume.getClass().getMethod("isEmulated").invoke(mVolume);
            } catch (Exception e) {
                // ignore
            }
        }
        return false;
    }

    /**
     * Returns true if this volume can be shared via USB mass storage.
     * API 14及以上
     * 灰名单，允许访问
     *
     * @return whether mass storage is allowed
     */
    public boolean allowMassStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                return (boolean) mVolume.getClass().getMethod("allowMassStorage").invoke(mVolume);
            } catch (Exception e) {
                // ignore
            }
        }
        return false;
    }

    /**
     * Returns maximum file size for the volume, or zero if it is unbounded.
     * API 14及以上
     * 灰名单，允许访问
     *
     * @return maximum file size
     */
    public long getMaxFileSize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                return (long) mVolume.getClass().getMethod("getMaxFileSize").invoke(mVolume);
            } catch (Exception e) {
                // ignore
            }
        }
        return 0;
    }

    /**
     * Gets the volume UUID, if any.
     * API 19及以上
     */
    @Nullable
    public String getUuid() {
        final int api = Build.VERSION.SDK_INT;
        if (api >= Build.VERSION_CODES.N) {
            return ((StorageVolume) mVolume).getUuid();
        } else if (api >= Build.VERSION_CODES.KITKAT) {
            try {
                return (String) mVolume.getClass().getMethod("getUuid").invoke(mVolume);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    /**
     * Parse and return volume UUID as FAT volume ID, or return -1 if unable to
     * parse or UUID is unknown.
     * API 19及以上
     * 灰名单，允许访问
     */
    public int getFatVolumeId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                return (int) mVolume.getClass().getMethod("getFatVolumeId").invoke(mVolume);
            } catch (Exception e) {
                // ignore
            }
        }
        return -1;
    }

    /**
     * 获取用户标签
     * API 19及以上
     * 灰名单，允许访问
     *
     * @return 用户标签
     */
    public String getUserLabel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                return (String) mVolume.getClass().getMethod("getUserLabel").invoke(mVolume);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    /**
     * Returns the current state of the volume.
     * API 19及以上
     *
     * @return one of {@link Environment#MEDIA_UNKNOWN}, {@link Environment#MEDIA_REMOVED},
     * {@link Environment#MEDIA_UNMOUNTED}, {@link Environment#MEDIA_CHECKING},
     * {@link Environment#MEDIA_NOFS}, {@link Environment#MEDIA_MOUNTED},
     * {@link Environment#MEDIA_MOUNTED_READ_ONLY}, {@link Environment#MEDIA_SHARED},
     * {@link Environment#MEDIA_BAD_REMOVAL}, or {@link Environment#MEDIA_UNMOUNTABLE}.
     */
    public String getState() {
        final int api = Build.VERSION.SDK_INT;
        if (api >= Build.VERSION_CODES.N) {
            return ((StorageVolume) mVolume).getState();
        } else if (api >= Build.VERSION_CODES.KITKAT) {
            try {
                return (String) mVolume.getClass().getMethod("getState").invoke(mVolume);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    /**
     * Builds an intent to give access to a standard storage directory or entire volume after
     * obtaining the user's approval.
     * <p>
     * When invoked, the system will ask the user to grant access to the requested directory (and
     * its descendants). The result of the request will be returned to the activity through the
     * {@code onActivityResult} method.
     * <p>
     * To gain access to descendants (child, grandchild, etc) documents, use
     * {@link DocumentsContract#buildDocumentUriUsingTree(Uri, String)}, or
     * {@link DocumentsContract#buildChildDocumentsUriUsingTree(Uri, String)} with the returned URI.
     * <p>
     * If your application only needs to store internal data, consider using
     * {@link Context#getExternalFilesDirs(String) Context.getExternalFilesDirs},
     * {@link Context#getExternalCacheDirs()}, or {@link Context#getExternalMediaDirs()}, which
     * require no permissions to read or write.
     * <p>
     * Access to the entire volume is only available for non-primary volumes (for the primary
     * volume, apps can use the {@link android.Manifest.permission#READ_EXTERNAL_STORAGE} and
     * {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE} permissions) and should be used
     * with caution, since users are more likely to deny access when asked for entire volume access
     * rather than specific directories.
     *
     * @param directoryName must be one of {@link Environment#DIRECTORY_MUSIC},
     *                      {@link Environment#DIRECTORY_PODCASTS}, {@link Environment#DIRECTORY_RINGTONES},
     *                      {@link Environment#DIRECTORY_ALARMS}, {@link Environment#DIRECTORY_NOTIFICATIONS},
     *                      {@link Environment#DIRECTORY_PICTURES}, {@link Environment#DIRECTORY_MOVIES},
     *                      {@link Environment#DIRECTORY_DOWNLOADS}, {@link Environment#DIRECTORY_DCIM}, or
     *                      {@link Environment#DIRECTORY_DOCUMENTS}, or {@code null} to request access to the
     *                      entire volume.
     * @return intent to request access, or {@code null} if the requested directory is invalid for
     * that volume.
     * @see DocumentsContract
     * @deprecated Callers should migrate to using {@link Intent#ACTION_OPEN_DOCUMENT_TREE} instead.
     * Launching this {@link Intent} on devices running
     * {@link android.os.Build.VERSION_CODES#Q} or higher, will immediately finish
     * with a result code of {@link android.app.Activity#RESULT_CANCELED}.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Deprecated
    @Nullable
    public Intent createAccessIntent(String directoryName) {
        return ((StorageVolume) mVolume).createAccessIntent(directoryName);
    }

    /**
     * Builds an {@link Intent#ACTION_OPEN_DOCUMENT_TREE} to allow the user to grant access to any
     * directory subtree (or entire volume) from the {@link android.provider.DocumentsProvider}s
     * available on the device. The initial location of the document navigation will be the root of
     * this {@link StorageVolume}.
     * <p>
     * Note that the returned {@link Intent} simply suggests that the user picks this {@link
     * StorageVolume} by default, but the user may select a different location. Callers must respect
     * the user's chosen location, even if it is different from the originally requested location.
     *
     * @return intent to {@link Intent#ACTION_OPEN_DOCUMENT_TREE} initially showing the contents
     * of this {@link StorageVolume}
     * @see Intent#ACTION_OPEN_DOCUMENT_TREE
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @NonNull
    public Intent createOpenDocumentTreeIntent() {
        return ((StorageVolume) mVolume).createOpenDocumentTreeIntent();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeParcelable(this.mVolume, flags);
    }
}
