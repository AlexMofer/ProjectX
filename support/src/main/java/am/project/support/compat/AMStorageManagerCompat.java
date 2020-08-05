/*
 * Copyright (C) 2015 AlexMofer
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

package am.project.support.compat;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import java.util.ArrayList;
import java.util.List;

/**
 * StorageManager兼容器
 * Created by Alex on 2017/9/3.
 *
 * @see am.project.support.storage.StorageManagerCompat 使用该对象替代，下一版本将移除
 */
@SuppressWarnings("ALL")
@Deprecated
public final class AMStorageManagerCompat {
    private static final StorageManagerCompatImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= 24) {
            IMPL = new Api24StorageManagerCompatImpl();
        } else {
            IMPL = new BaseStorageManagerCompatImpl();
        }
    }

    private AMStorageManagerCompat() {
        //no instance
    }

    /**
     * 获取所有存储设备
     *
     * @param manager 存储设备管理器
     * @return 所有存储设备
     */
    public static List<StorageVolumeImpl> getStorageVolumes(StorageManager manager) {
        return IMPL.getStorageVolumes(manager);
    }

    /**
     * 获取所有已挂载的存储设备
     *
     * @param manager 存储设备管理器
     * @return 所有已挂载的存储设备
     */
    public static List<StorageVolumeImpl> getEmulatedStorageVolumes(StorageManager manager) {
        List<StorageVolumeImpl> storageVolumes = getStorageVolumes(manager);
        if (storageVolumes != null) {
            for (int i = 0; i < storageVolumes.size(); ) {
                if (storageVolumes.get(i).isEmulated()) {
                    i++;
                    continue;
                }
                storageVolumes.remove(i);
            }
        }
        return storageVolumes;
    }

    public interface StorageVolumeImpl extends Parcelable {

        String getPath();

        boolean isEmulated();
    }

    private interface StorageManagerCompatImpl {
        List<StorageVolumeImpl> getStorageVolumes(StorageManager manager);
    }

    private static class BaseStorageVolumeImpl implements StorageVolumeImpl {
        public static final Creator<BaseStorageVolumeImpl> CREATOR =
                new Creator<BaseStorageVolumeImpl>() {
                    @Override
                    public BaseStorageVolumeImpl createFromParcel(Parcel source) {
                        return new BaseStorageVolumeImpl(source);
                    }

                    @Override
                    public BaseStorageVolumeImpl[] newArray(int size) {
                        return new BaseStorageVolumeImpl[size];
                    }
                };
        private final String mPath;
        private final boolean mEmulated;

        private BaseStorageVolumeImpl(String path, boolean emulated) {
            mPath = path;
            mEmulated = emulated;
        }

        private BaseStorageVolumeImpl(Parcel in) {
            this.mPath = in.readString();
            this.mEmulated = in.readByte() != 0;
        }

        @Override
        public String getPath() {
            return mPath;
        }

        @Override
        public boolean isEmulated() {
            return mEmulated;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mPath);
            dest.writeByte(this.mEmulated ? (byte) 1 : (byte) 0);
        }
    }

    @TargetApi(24)
    private static class Api24StorageVolumeImpl implements StorageVolumeImpl {
        public static final Creator<Api24StorageVolumeImpl> CREATOR =
                new Parcelable.Creator<Api24StorageVolumeImpl>() {
                    @Override
                    public Api24StorageVolumeImpl createFromParcel(Parcel source) {
                        return new Api24StorageVolumeImpl(source);
                    }

                    @Override
                    public Api24StorageVolumeImpl[] newArray(int size) {
                        return new Api24StorageVolumeImpl[size];
                    }
                };
        private final StorageVolume mStorageVolume;
        private final String mPath;

        private Api24StorageVolumeImpl(String path, StorageVolume volume) {
            mPath = path;
            mStorageVolume = volume;
        }

        private Api24StorageVolumeImpl(Parcel in) {
            this.mStorageVolume = in.readParcelable(StorageVolume.class.getClassLoader());
            this.mPath = in.readString();
        }

        @Override
        public String getPath() {
            return mPath;
        }

        @Override
        public boolean isEmulated() {
            return mStorageVolume.isEmulated();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.mStorageVolume, flags);
            dest.writeString(this.mPath);
        }
    }

    private static class BaseStorageManagerCompatImpl implements StorageManagerCompatImpl {

        @Override
        public List<StorageVolumeImpl> getStorageVolumes(StorageManager manager) {
            ArrayList<StorageVolumeImpl> storageVolumes = new ArrayList<>();
            Object[] volumes;
            try {
                volumes = (Object[]) manager.getClass().getMethod("getVolumeList").invoke(manager);
            } catch (Exception e) {
                volumes = null;
            }
            if (volumes == null) {
                final String path = Environment.getExternalStorageDirectory().getPath();
                storageVolumes.add(new BaseStorageVolumeImpl(path, true));
                return storageVolumes;
            }
            for (Object volume : volumes) {
                String path;
                String state;
                try {
                    path = (String) volume.getClass().getMethod("getPath").invoke(volume);
                } catch (Exception e) {
                    continue;
                }
                try {
                    state = (String) manager.getClass().getMethod("getVolumeState", String.class)
                            .invoke(manager, path);
                } catch (Exception e) {
                    state = null;
                }
                if (path == null || "".equals(path)) {
                    continue;
                }
                final boolean emulated = Environment.MEDIA_MOUNTED.equals(state);
                storageVolumes.add(new BaseStorageVolumeImpl(path, emulated));
            }
            if (storageVolumes.isEmpty()) {
                final String path = Environment.getExternalStorageDirectory().getPath();
                storageVolumes.add(new BaseStorageVolumeImpl(path, true));
            }
            return storageVolumes;
        }
    }

    @TargetApi(24)
    private static class Api24StorageManagerCompatImpl implements StorageManagerCompatImpl {
        @Override
        public List<StorageVolumeImpl> getStorageVolumes(StorageManager manager) {
            final ArrayList<StorageVolumeImpl> storageVolumes = new ArrayList<>();
            List<StorageVolume> volumes = manager.getStorageVolumes();
            for (StorageVolume volume : volumes) {
                String path;
                try {
                    //noinspection JavaReflectionMemberAccess
                    path = (String) volume.getClass().getMethod("getPath").invoke(volume);
                } catch (Exception e) {
                    path = null;
                }
                if (path == null || "".equals(path)) {
                    continue;
                }
                storageVolumes.add(new Api24StorageVolumeImpl(path, volume));
            }
            if (storageVolumes.isEmpty()) {
                final String path = Environment.getExternalStorageDirectory().getPath();
                storageVolumes.add(new BaseStorageVolumeImpl(path, true));
            }
            return storageVolumes;
        }
    }
}
