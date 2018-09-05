/*
 * Copyright (C) 2018 AlexMofer
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
package am.project.support.font.truetype;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * 字体全名工具
 * Created by Alex on 2018/9/5.
 */
public class TrueTypeFullNameUtils {

    private TrueTypeFullNameUtils() {
        //no instance
    }

    @SuppressWarnings("all")
    private static String readString(RandomAccessFile reader, int len, String charsetName)
            throws IOException {
        if ((len + reader.getFilePointer()) > reader.length())
            throw new EOFException();
        final byte[] tmp = new byte[len];
        reader.read(tmp);
        return new String(tmp, charsetName);
    }

    private static String readString(RandomAccessFile reader, int len) throws IOException {
        if ((len + reader.getFilePointer()) > reader.length())
            throw new EOFException();
        final byte[] tmp = new byte[len];
        reader.read(tmp);
        final String charsetName;
        if ((tmp.length > 0) && (tmp[0] == 0)) {
            charsetName = "UTF-16BE";
        } else {
            charsetName = "ISO-8859-1";
        }
        return new String(tmp, charsetName);
    }

    private static String getName(RandomAccessFile reader) throws IOException {
        reader.skipBytes(2);
        long i = reader.getFilePointer();
        int n = reader.readUnsignedShort();
        long j = reader.readUnsignedShort() + i - 2;
        i += 2 * 2;
        String fullName = "";
        while (n-- > 0) {
            reader.seek(i);
            final int platformID = reader.readUnsignedShort();
            final int encodingID = reader.readUnsignedShort();
            final int languageID = reader.readUnsignedShort();

            int k = reader.readUnsignedShort();
            int l = reader.readUnsignedShort();

            if (((platformID == 1 || platformID == 3)
                    && (encodingID == 0 || encodingID == 1))) {
                reader.seek(j + reader.readUnsignedShort());
                String txt;
                if (platformID == 3) {
                    txt = readString(reader, l, "UTF-16BE");
                } else {
                    txt = readString(reader, l);
                }

                switch (k) {
                    case 0:
                        break;
                    case 1: //Font Family Name
                    case 16: //Preferred Family
                        break;
                    case 2:
                        break;
                    case 4:
                        if (platformID == 3 && languageID == 1033) {
                            fullName = txt;
                        }
                        break;
                    case 6:
                        break;
                    default:
                        break;
                }
            }
            i += 6 * 2;
        }
        return fullName;
    }

    /**
     * 获取字体全名集合
     *
     * @param font 字体文件
     * @return 全名集合
     */
    public static List<String> getFullNames(File font) {
        if (font == null)
            return null;
        final ArrayList<String> names = new ArrayList<>();
        RandomAccessFile reader = null;
        try {
            reader = new RandomAccessFile(font, "r");
            final int begin = reader.readInt();
            int size;
            int[] positions;
            switch (begin) {
                default:
                    return null;
                case 0x00010000:
                    // OpenType fonts that contain TrueType outlines
                case 0x4F54544F:
                    // OpenType fonts containing CFF data (version 1 or 2)
                    size = 1;
                    positions = new int[1];
                    positions[0] = 0;
                    break;
                case 0x74746366:
                    // An OpenType Font Collection (formerly known as TrueType Collection)
                    reader.skipBytes(2);// majorVersion
                    reader.skipBytes(2);// minorVersion
                    size = reader.readInt();
                    positions = new int[size];
                    for (int i = 0; i < size; i++) {
                        positions[i] = reader.readInt();
                    }
                    break;
            }
            for (int i = 0; i < size; i++) {
                reader.seek(positions[i]);
                reader.skipBytes(4);// sfntVersion
                final int numTables = reader.readUnsignedShort();
                reader.skipBytes(2);// searchRange
                reader.skipBytes(2);// entrySelector
                reader.skipBytes(2);// rangeShift
                int nameTableOffset = 0;
                for (int j = 0; j < numTables; j++) {
                    final int tableTag = reader.readInt();
                    reader.skipBytes(4);// checkSum
                    final int offset = reader.readInt();
                    reader.skipBytes(4);// length
                    if (tableTag == 0x6e616d65) {
                        // Naming Table
                        nameTableOffset = offset;
                    }
                }
                if (nameTableOffset <= 0) {
                    return null;
                }
                reader.seek(nameTableOffset);
                names.add(getName(reader));
            }
            return names.isEmpty() ? null : names;
        } catch (Exception e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }
}
