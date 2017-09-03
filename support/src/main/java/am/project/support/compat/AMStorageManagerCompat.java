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
 */
@SuppressWarnings("all")
public class AMStorageManagerCompat {
    private AMStorageManagerCompat() {
        //no instance
    }

    public interface StorageVolumeImpl extends Parcelable {

        String getPath();

        boolean isEmulated();
    }

    private static class BaseStorageVolumeImpl implements StorageVolumeImpl {
        private final String mPath;
        private final boolean mEmulated;

        private BaseStorageVolumeImpl(String path, boolean emulated) {
            mPath = path;
            mEmulated = emulated;
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

        private BaseStorageVolumeImpl(Parcel in) {
            this.mPath = in.readString();
            this.mEmulated = in.readByte() != 0;
        }

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
    }

    @TargetApi(24)
    private static class Api24StorageVolumeImpl implements StorageVolumeImpl {
        private final StorageVolume mStorageVolume;
        private final String mPath;

        private Api24StorageVolumeImpl(String path, StorageVolume volume) {
            mPath = path;
            mStorageVolume = volume;
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

        private Api24StorageVolumeImpl(Parcel in) {
            this.mStorageVolume = in.readParcelable(StorageVolume.class.getClassLoader());
            this.mPath = in.readString();
        }

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
    }

    private interface StorageManagerCompatImpl {
        List<StorageVolumeImpl> getStorageVolumes(StorageManager manager);
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

    private static final StorageManagerCompatImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= 24) {
            IMPL = new Api24StorageManagerCompatImpl();
        } else {
            IMPL = new BaseStorageManagerCompatImpl();
        }
    }

    public static List<StorageVolumeImpl> getStorageVolumes(StorageManager manager) {
        return IMPL.getStorageVolumes(manager);
    }
}
