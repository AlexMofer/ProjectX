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
package am.project.support.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Intent数据传输工具
 * Created by Alex on 2019/3/18.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class IntentExtraUtils {

    private static final int TYPE_STRING = 0;
    private static final int TYPE_CHARS = 1;
    private static final int TYPE_PARCELABLE = 2;// 若对象未同时实现Serializable，则超限时不会写入文件
    private static final int TYPE_SERIALIZABLE = 3;
    private static final int TYPE_BUNDLE = 4;// 不完全支持，大小超限时不支持写入文件
    private static final int TYPE_ARRAY_BOOLEAN = 10;
    private static final int TYPE_ARRAY_BYTE = 11;
    private static final int TYPE_ARRAY_CHAR = 12;
    private static final int TYPE_ARRAY_INT = 13;
    private static final int TYPE_ARRAY_LONG = 14;
    private static final int TYPE_ARRAY_FLOAT = 15;
    private static final int TYPE_ARRAY_DOUBLE = 16;
    private static final int TYPE_ARRAY_STRING = 17;
    private static final int TYPE_ARRAY_CHARS = 18;
    private static final int TYPE_ARRAY_PARCELABLE = 19;
    private static final int TYPE_LIST_INTEGER = 20;
    private static final int TYPE_LIST_STRING = 21;
    private static final int TYPE_LIST_CHARS = 22;
    private static final int TYPE_LIST_PARCELABLE = 23;
    private static final String KEY_DATA = "key_data_index_";
    private static final String KEY_DATA_TYPE = "key_data_type_index_";
    private static final String KEY_LARGE_MODE = "key_large_mode_index_";
    private static final String KEY_LARGE_MODE_FILE_NAME = "key_large_mode_file_name_index_";

    private IntentExtraUtils() {
        //no instance
    }

    /**
     * 存放附件
     * 基础类型不支持，Bundle数据大小超限时不支持写入文件
     *
     * @param context              Context
     * @param intent               Intent
     * @param data                 附件
     * @param keyData              附件Key
     * @param keyDataType          附件类型Key
     * @param keyLargeMode         超大附件Key
     * @param keyLargeModeFileName 超大附件文件名
     * @return 是否成功
     */
    public static boolean putExtra(Context context, Intent intent,
                                   Object data, String keyData, String keyDataType,
                                   String keyLargeMode, String keyLargeModeFileName) {
        if (data == null)
            return true;
        if (data instanceof String) {
            try {
                intent.putExtra(keyData, (String) data);
                intent.putExtra(keyDataType, TYPE_STRING);
                return true;
            } catch (Exception e) {
                return writeIntoFile(context, intent, data, keyLargeMode, keyLargeModeFileName);
            }
        }
        if (data instanceof CharSequence) {
            try {
                intent.putExtra(keyData, (CharSequence) data);
                intent.putExtra(keyDataType, TYPE_CHARS);
                return true;
            } catch (Exception e) {
                return writeIntoFile(context, intent, data, keyLargeMode, keyLargeModeFileName);
            }
        }
        if (data instanceof Bundle) {
            try {
                intent.putExtra(keyData, (Bundle) data);
                intent.putExtra(keyDataType, TYPE_BUNDLE);
                return true;
            } catch (Exception e) {
                return writeIntoFile(context, intent, data, keyLargeMode, keyLargeModeFileName);
            }
        }
        if (data instanceof Parcelable) {
            try {
                intent.putExtra(keyData, (Parcelable) data);
                intent.putExtra(keyDataType, TYPE_PARCELABLE);
                return true;
            } catch (Exception e) {
                return writeIntoFile(context, intent, data, keyLargeMode, keyLargeModeFileName);
            }
        }
        if (data instanceof ArrayList) {
            final ArrayList list = (ArrayList) data;
            if (list.isEmpty())
                return true;
            final Object item = list.get(0);
            if (item instanceof Integer) {
                try {
                    //noinspection unchecked
                    intent.putIntegerArrayListExtra(keyData, (ArrayList<Integer>) data);
                    intent.putExtra(keyDataType, TYPE_LIST_INTEGER);
                    return true;
                } catch (Exception e) {
                    return writeIntoFile(context, intent, data, keyLargeMode, keyLargeModeFileName);
                }
            }
            if (item instanceof String) {
                try {
                    //noinspection unchecked
                    intent.putStringArrayListExtra(keyData, (ArrayList<String>) data);
                    intent.putExtra(keyDataType, TYPE_LIST_STRING);
                    return true;
                } catch (Exception e) {
                    return writeIntoFile(context, intent, data, keyLargeMode, keyLargeModeFileName);
                }
            }
            if (item instanceof CharSequence) {
                try {
                    //noinspection unchecked
                    intent.putCharSequenceArrayListExtra(keyData, (ArrayList<CharSequence>) data);
                    intent.putExtra(keyDataType, TYPE_LIST_CHARS);
                    return true;
                } catch (Exception e) {
                    return writeIntoFile(context, intent, data, keyLargeMode, keyLargeModeFileName);
                }
            }
            if (item instanceof Parcelable) {
                try {
                    //noinspection unchecked
                    intent.putParcelableArrayListExtra(keyData, (ArrayList<Parcelable>) data);
                    intent.putExtra(keyDataType, TYPE_LIST_PARCELABLE);
                    return true;
                } catch (Exception e) {
                    return writeIntoFile(context, intent, data, keyLargeMode, keyLargeModeFileName);
                }
            }
        }
        if (data instanceof Serializable) {
            try {
                intent.putExtra(keyData, (Serializable) data);
                intent.putExtra(keyDataType, TYPE_SERIALIZABLE);
                return true;
            } catch (Exception e) {
                return writeIntoFile(context, intent, data, keyLargeMode, keyLargeModeFileName);
            }
        }
        return putArrayExtra(context, intent, data, keyData, keyDataType, keyLargeMode,
                keyLargeModeFileName);
    }

    private static boolean writeIntoFile(Context context, Intent intent, Object data,
                                         String keyLargeMode, String keyLargeModeFileName) {
        final FileOutputStream fos;
        try {
            fos = context.openFileOutput(keyLargeModeFileName, Context.MODE_PRIVATE);
        } catch (Exception e) {
            return false;
        }
        final ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(fos);
        } catch (Exception e) {
            try {
                fos.close();
            } catch (Exception e1) {
                // ignore
            }
            return false;
        }
        try {
            oos.writeObject(data);
        } catch (Exception e) {
            try {
                oos.close();
            } catch (Exception e1) {
                // ignore
            }
            try {
                fos.close();
            } catch (Exception e1) {
                // ignore
            }
            return false;
        }
        try {
            oos.close();
        } catch (Exception e1) {
            return false;
        }
        try {
            fos.close();
        } catch (Exception e1) {
            return false;
        }
        intent.putExtra(keyLargeMode, true);
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    private static boolean putArrayExtra(Context context, Intent intent,
                                         Object data, String keyData, String keyDataType,
                                         String keyLargeMode, String keyLargeModeFileName) {
        try {
            final boolean[] array = (boolean[]) data;
            try {
                intent.putExtra(keyData, array);
                intent.putExtra(keyDataType, TYPE_ARRAY_BOOLEAN);
                return true;
            } catch (Exception e) {
                return writeIntoFile(context, intent, new Primitives().setBooleans(array),
                        keyLargeMode, keyLargeModeFileName);
            }
        } catch (Exception e) {
            // ignore
        }
        try {
            final byte[] array = (byte[]) data;
            try {
                intent.putExtra(keyData, array);
                intent.putExtra(keyDataType, TYPE_ARRAY_BYTE);
                return true;
            } catch (Exception e) {
                return writeIntoFile(context, intent, new Primitives().setBytes(array),
                        keyLargeMode, keyLargeModeFileName);
            }
        } catch (Exception e) {
            // ignore
        }
        try {
            final char[] array = (char[]) data;
            try {
                intent.putExtra(keyData, array);
                intent.putExtra(keyDataType, TYPE_ARRAY_CHAR);
                return true;
            } catch (Exception e) {
                return writeIntoFile(context, intent, new Primitives().setChars(array),
                        keyLargeMode, keyLargeModeFileName);
            }
        } catch (Exception e) {
            // ignore
        }
        try {
            final int[] array = (int[]) data;
            try {
                intent.putExtra(keyData, array);
                intent.putExtra(keyDataType, TYPE_ARRAY_INT);
                return true;
            } catch (Exception e) {
                return writeIntoFile(context, intent, new Primitives().setInts(array),
                        keyLargeMode, keyLargeModeFileName);
            }
        } catch (Exception e) {
            // ignore
        }
        try {
            final long[] array = (long[]) data;
            try {
                intent.putExtra(keyData, array);
                intent.putExtra(keyDataType, TYPE_ARRAY_LONG);
                return true;
            } catch (Exception e) {
                return writeIntoFile(context, intent, new Primitives().setLongs(array),
                        keyLargeMode, keyLargeModeFileName);
            }
        } catch (Exception e) {
            // ignore
        }
        try {
            final float[] array = (float[]) data;
            try {
                intent.putExtra(keyData, array);
                intent.putExtra(keyDataType, TYPE_ARRAY_FLOAT);
                return true;
            } catch (Exception e) {
                return writeIntoFile(context, intent, new Primitives().setFloats(array),
                        keyLargeMode, keyLargeModeFileName);
            }
        } catch (Exception e) {
            // ignore
        }
        try {
            final double[] array = (double[]) data;
            try {
                intent.putExtra(keyData, array);
                intent.putExtra(keyDataType, TYPE_ARRAY_DOUBLE);
                return true;
            } catch (Exception e) {
                return writeIntoFile(context, intent, new Primitives().setDoubles(array),
                        keyLargeMode, keyLargeModeFileName);
            }
        } catch (Exception e) {
            // ignore
        }
        return false;
    }

    /**
     * 存放附件
     * 基础类型不支持，Bundle数据大小超限时不支持写入文件
     *
     * @param context Context
     * @param intent  Intent
     * @param data    附件
     * @param index   第几个附件
     * @return 是否成功
     */
    public static boolean putExtra(Context context, Intent intent,
                                   Object data, int index) {
        final String keyData = KEY_DATA + index;
        final String keyDataType = KEY_DATA_TYPE + index;
        final String keyLargeMode = KEY_LARGE_MODE + index;
        final String keyLargeModeFileName = KEY_LARGE_MODE_FILE_NAME + index;
        return putExtra(context, intent, data, keyData, keyDataType, keyLargeMode,
                keyLargeModeFileName);
    }

    /**
     * 获取附件
     *
     * @param context           Context
     * @param intent            Intent
     * @param dataKey           附件Key
     * @param dataTypeKey       附件类型Key
     * @param largeModeKey      超大附件Key
     * @param largeModeFileName 超大附件名
     * @return 附件
     */
    public static <T> T getExtra(Context context, Intent intent,
                                 String dataKey, String dataTypeKey,
                                 String largeModeKey, String largeModeFileName) {
        final Object result = getExtraObject(context, intent, dataKey, dataTypeKey,
                largeModeKey, largeModeFileName);
        try {
            //noinspection unchecked
            return (T) result;
        } catch (Exception e) {
            return null;
        }
    }

    private static Object getExtraObject(Context context, Intent intent,
                                         String dataKey, String dataTypeKey,
                                         String largeModeKey, String largeModeFileName) {
        final Object result = readFromFile(context, intent, largeModeKey, largeModeFileName);
        if (result instanceof Primitives) {
            final Primitives primitives = (Primitives) result;
            switch (primitives.type) {
                default:
                    return null;
                case TYPE_ARRAY_BOOLEAN:
                    return primitives.booleans;
                case TYPE_ARRAY_BYTE:
                    return primitives.bytes;
                case TYPE_ARRAY_CHAR:
                    return primitives.chars;
                case TYPE_ARRAY_INT:
                    return primitives.ints;
                case TYPE_ARRAY_LONG:
                    return primitives.longs;
                case TYPE_ARRAY_FLOAT:
                    return primitives.floats;
                case TYPE_ARRAY_DOUBLE:
                    return primitives.doubles;
            }
        }
        if (result != null)
            return result;
        if (!intent.hasExtra(dataKey))
            return null;
        final int type = intent.getIntExtra(dataTypeKey, -1);
        switch (type) {
            default:
                return null;
            case TYPE_STRING:
                return intent.getStringExtra(dataKey);
            case TYPE_CHARS:
                return intent.getCharSequenceExtra(dataKey);
            case TYPE_PARCELABLE:
                return intent.getParcelableExtra(dataKey);
            case TYPE_SERIALIZABLE:
                return intent.getSerializableExtra(dataKey);
            case TYPE_BUNDLE:
                return intent.getBundleExtra(dataKey);
            case TYPE_ARRAY_BOOLEAN:
                return intent.getBooleanArrayExtra(dataKey);
            case TYPE_ARRAY_BYTE:
                return intent.getByteArrayExtra(dataKey);
            case TYPE_ARRAY_CHAR:
                return intent.getCharArrayExtra(dataKey);
            case TYPE_ARRAY_INT:
                return intent.getIntArrayExtra(dataKey);
            case TYPE_ARRAY_LONG:
                return intent.getLongArrayExtra(dataKey);
            case TYPE_ARRAY_FLOAT:
                return intent.getFloatArrayExtra(dataKey);
            case TYPE_ARRAY_DOUBLE:
                return intent.getDoubleArrayExtra(dataKey);
            case TYPE_ARRAY_STRING:
                return intent.getStringArrayExtra(dataKey);
            case TYPE_ARRAY_CHARS:
                return intent.getCharSequenceArrayExtra(dataKey);
            case TYPE_ARRAY_PARCELABLE:
                return intent.getParcelableArrayExtra(dataKey);
            case TYPE_LIST_INTEGER:
                return intent.getIntegerArrayListExtra(dataKey);
            case TYPE_LIST_STRING:
                return intent.getStringArrayListExtra(dataKey);
            case TYPE_LIST_CHARS:
                return intent.getCharSequenceArrayListExtra(dataKey);
            case TYPE_LIST_PARCELABLE:
                return intent.getParcelableArrayListExtra(dataKey);
        }
    }

    private static Object readFromFile(Context context, Intent intent,
                                       String largeModeKey, String largeModeFileName) {
        if (intent.getBooleanExtra(largeModeKey, false)) {
            final FileInputStream fis;
            try {
                fis = context.openFileInput(largeModeFileName);
            } catch (Exception e) {
                return null;
            }
            final ObjectInputStream ois;
            try {
                ois = new ObjectInputStream(fis);
            } catch (Exception e) {
                try {
                    fis.close();
                } catch (Exception e1) {
                    // ignore
                }
                return null;
            }
            final Object data;
            try {
                data = ois.readObject();
            } catch (Exception e) {
                try {
                    ois.close();
                } catch (Exception e1) {
                    // ignore
                }
                try {
                    fis.close();
                } catch (Exception e1) {
                    // ignore
                }
                return null;
            }
            try {
                ois.close();
            } catch (Exception e1) {
                return null;
            }
            try {
                fis.close();
            } catch (Exception e1) {
                return null;
            }
            return data;
        }
        return null;
    }

    /**
     * 获取附件
     *
     * @param context Context
     * @param intent  Intent
     * @param index   第几个附件
     * @return 附件
     */
    public static <T> T getExtra(Context context, Intent intent, int index) {
        final String keyData = KEY_DATA + index;
        final String keyDataType = KEY_DATA_TYPE + index;
        final String keyLargeMode = KEY_LARGE_MODE + index;
        final String keyLargeModeFileName = KEY_LARGE_MODE_FILE_NAME + index;
        return getExtra(context, intent, keyData, keyDataType, keyLargeMode, keyLargeModeFileName);
    }

    private static class Primitives implements Serializable {

        private int type = -1;
        private boolean[] booleans;
        private byte[] bytes;
        private char[] chars;
        private int[] ints;
        private long[] longs;
        private float[] floats;
        private double[] doubles;

        Primitives setBooleans(boolean[] booleans) {
            this.booleans = booleans;
            type = TYPE_ARRAY_BOOLEAN;
            return this;
        }

        Primitives setBytes(byte[] bytes) {
            this.bytes = bytes;
            type = TYPE_ARRAY_BYTE;
            return this;
        }

        Primitives setChars(char[] chars) {
            this.chars = chars;
            type = TYPE_ARRAY_CHAR;
            return this;
        }

        Primitives setInts(int[] ints) {
            this.ints = ints;
            type = TYPE_ARRAY_INT;
            return this;
        }

        Primitives setLongs(long[] longs) {
            this.longs = longs;
            type = TYPE_ARRAY_LONG;
            return this;
        }

        Primitives setFloats(float[] floats) {
            this.floats = floats;
            type = TYPE_ARRAY_FLOAT;
            return this;
        }

        Primitives setDoubles(double[] doubles) {
            this.doubles = doubles;
            type = TYPE_ARRAY_DOUBLE;
            return this;
        }
    }
}
