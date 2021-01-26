package am.util.clipboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClipboardProvider extends ContentProvider {

    static final String PATH_COPY = "copy";
    static final String PATH_CLEAR = "clear";
    static final String PATH_CHECK = "check";
    private static final int CODE_COPY = 1;
    private static final int CODE_CLEAR = 2;
    private static final int CODE_CHECK = 3;
    private static final String META_DATA_CLIPBOARD_PROVIDER_PREFIX = "am.util.clipboard.CLIPBOARD_PROVIDER_PREFIX";
    private static final String META_DATA_CLIPBOARD_PROVIDER_PREFERENCES = "am.util.clipboard.CLIPBOARD_PROVIDER_PREFERENCES";
    private static final String META_DATA_CLIPBOARD_PROVIDER_PERMANENT = "am.util.clipboard.CLIPBOARD_PROVIDER_PERMANENT";
    private static final String KEY_FILES = "files";
    private static final String PREFIX = "clipboard_provider_data_";
    private static final String PREFERENCES = "clipboard_provider_preferences";
    private static String sAuthority;
    private final UriMatcher mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private final Set<String> mFiles = new HashSet<>();
    private String mPrefix;
    private boolean mPermanent;// 决定使用cache目录还是使用files目录
    private SharedPreferences mBackup;
    private ClipboardManager mManager;

    private static void checkAuthority(Context context) {
        if (sAuthority != null)
            return;
        if (context == null)
            return;
        final PackageInfo info;
        try {
            info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS);
        } catch (Exception e) {
            return;
        }
        if (info == null || info.providers == null)
            return;
        final ProviderInfo[] providers = info.providers;
        final String name = ClipboardProvider.class.getName();
        for (ProviderInfo provider : providers) {
            if (TextUtils.equals(provider.name, name)) {
                sAuthority = provider.authority;
                break;
            }
        }
    }

    static String getAuthority(Context context) {
        checkAuthority(context);
        return sAuthority;
    }

    protected String getAuthority() {
        return sAuthority;
    }

    static void setAuthority(String authority) {
        sAuthority = authority;
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        checkAuthority(context);
        final String authority = getAuthority();
        if (TextUtils.isEmpty(authority))
            return false;
        String preferences = null;
        final ProviderInfo info = context.getPackageManager()
                .resolveContentProvider(authority, PackageManager.GET_META_DATA);
        if (info != null && info.metaData != null) {
            mPrefix = info.metaData.getString(META_DATA_CLIPBOARD_PROVIDER_PREFIX);
            preferences = info.metaData.getString(META_DATA_CLIPBOARD_PROVIDER_PREFERENCES);
            mPermanent = info.metaData.getBoolean(META_DATA_CLIPBOARD_PROVIDER_PERMANENT);
        }
        if (TextUtils.isEmpty(mPrefix)) {
            mPrefix = PREFIX;
        }
        if (TextUtils.isEmpty(preferences)) {
            preferences = PREFERENCES;
        }
        mMatcher.addURI(authority, PATH_COPY + "/*", CODE_COPY);
        mMatcher.addURI(authority, PATH_CLEAR, CODE_CLEAR);
        mMatcher.addURI(authority, PATH_CHECK, CODE_CHECK);

        mManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        mManager.addPrimaryClipChangedListener(this::check);

        mBackup = context.getSharedPreferences(preferences, Context.MODE_PRIVATE);
        final Set<String> files = mBackup.getStringSet(KEY_FILES, null);
        if (files != null) {
            mFiles.addAll(files);
        }
        check();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        return mMatcher.match(uri) != CODE_CHECK ? new ClipboardCursor(!mFiles.isEmpty()) : null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mMatcher.match(uri) == CODE_CLEAR ? clear() : 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (mMatcher.match(uri) != CODE_COPY) {
            return null;
        }
        final List<String> pss = uri.getPathSegments();
        if (pss == null || pss.size() != 2 || !PATH_COPY.equals(pss.get(0))) {
            return null;
        }
        final String name = pss.get(1);
        final int modeBits;
        if ("r".equals(mode)) {
            synchronized (mFiles) {
                if (mFiles.contains(name)) {
                    return ParcelFileDescriptor.open(getFile(name),
                            ParcelFileDescriptor.MODE_READ_ONLY);
                }
            }
            return null;
        } else if ("w".equals(mode) || "wt".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_WRITE_ONLY
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_TRUNCATE;
        } else if ("wa".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_WRITE_ONLY
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_APPEND;
        } else if ("rw".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_WRITE
                    | ParcelFileDescriptor.MODE_CREATE;
        } else if ("rwt".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_WRITE
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_TRUNCATE;
        } else {
            return null;
        }
        synchronized (mFiles) {
            mFiles.add(name);
            mBackup.edit().putStringSet(KEY_FILES, mFiles).apply();
        }
        return ParcelFileDescriptor.open(getFile(name), modeBits);
    }

    private File getFile(String name) {
        if (mPermanent) {
            return getContext().getFileStreamPath(mPrefix + name);
        }
        return new File(getContext().getCacheDir(), mPrefix + name);
    }

    private int clear() {
        synchronized (mFiles) {
            mBackup.edit().remove(KEY_FILES).apply();
            if (mFiles.isEmpty()) {
                return 0;
            }
            for (String name : mFiles) {
                //noinspection ResultOfMethodCallIgnored
                getFile(name).delete();
            }
            final int size = mFiles.size();
            mFiles.clear();
            return size;
        }
    }

    private void check() {
        if (mManager.hasPrimaryClip()) {
            final ClipData clip = mManager.getPrimaryClip();
            if (clip != null && clip.getItemCount() > 0) {
                Uri uri = null;
                final int count = clip.getItemCount();
                for (int i = 0; i < count; i++) {
                    final ClipData.Item item = clip.getItemAt(i);
                    final Uri u = item.getUri();
                    if (u != null) {
                        uri = u;
                        break;
                    }
                }
                if (uri != null && mMatcher.match(uri) == CODE_COPY) {
                    return;
                }
            }
            clear();
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                // Android 10及以上，仅默认输入法或者应用已获取到焦点，否则无法访问剪切板。
                clear();
            }
        }
    }

    private static class ClipboardCursor extends AbstractCursor {

        private static final String NAME_DATA = "data";
        private final byte[] mBlob;

        ClipboardCursor(boolean value) {
            mBlob = value ? new byte[]{1} : new byte[]{0};
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public String[] getColumnNames() {
            return new String[]{NAME_DATA};
        }

        @Override
        public byte[] getBlob(int column) {
            return column == 0 ? mBlob : null;
        }

        @Override
        public String getString(int column) {
            throw new UnsupportedOperationException("getString is not supported");
        }

        @Override
        public short getShort(int column) {
            throw new UnsupportedOperationException("getShort is not supported");
        }

        @Override
        public int getInt(int column) {
            throw new UnsupportedOperationException("getInt is not supported");
        }

        @Override
        public long getLong(int column) {
            throw new UnsupportedOperationException("getLong is not supported");
        }

        @Override
        public float getFloat(int column) {
            throw new UnsupportedOperationException("getFloat is not supported");
        }

        @Override
        public double getDouble(int column) {
            throw new UnsupportedOperationException("getDouble is not supported");
        }

        @Override
        public boolean isNull(int column) {
            return column != 0;
        }
    }
}
