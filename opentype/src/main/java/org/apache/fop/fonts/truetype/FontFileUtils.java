package org.apache.fop.fonts.truetype;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Xiang Zhicheng on 2018/9/3.
 */
public class FontFileUtils {

    private static final String TAG_TTCF = "ttcf";
    private static final String TAG_OTTO = "OTTO";
    private static final String TAG_TTF = "\u0000\u0001\u0000\u0000";

    public static void printFontInfo(File file) {
        if (file == null)
            return;
        FontFileReader reader = null;
        try {
            reader = new FontFileReader(file);
            final String tag = reader.readString(4, FontFileReader.CHARSET_ISO_8859_1);
            reader.seek(0);
            if (TAG_TTCF.equals(tag)) {
                final List<String> names = readFullNames(reader);
                if (names != null) {
                    for (String name : names) {
                        reader.seek(4);
                        try {
                            final TTFFile font = new TTFFile();
                            font.readFont(reader, tag, name);
                            font.printStuff();
                        } catch (OutOfMemoryError e) {
                            // 中文OOM，字体文件过大
                            e.printStackTrace();// TODO
                        }
                    }
                }
            } else {
                // ttf otf
                final TTFFile font = new TTFFile();
                font.readFont(reader, tag);
                font.printStuff();
            }
        } catch (Exception e) {
            e.printStackTrace();// TODO
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

    private static List<String> readFullNames(FontFileReader reader) throws IOException {
        reader.skip(4);// TTF_FIXED_SIZE (4 bytes)
        reader.skip(4);
        final int size = reader.readInt();
        final int[] offsets = new int[size];
        for (int i = 0; i < size; i++) {
            offsets[i] = reader.readInt();
        }
        final ArrayList<String> names = new ArrayList<>();
        for (int i = 0; (i < size); i++) {
            reader.seek(offsets[i]);
            names.add(readFullName(reader));
        }
        return names;
    }

    private static String readFullName(FontFileReader reader) throws IOException {
        reader.skip(4);
        final int tabs = reader.readUnsignedShort();
        reader.skip(6);    // 3xTTF_USHORT_SIZE
        long position = -1;
        for (int j = 0; j < tabs; j++) {
            final String tabName = reader.readString(4, FontFileReader.CHARSET_ISO_8859_1);
            reader.skip(4);
            final int offset = reader.readInt();
            reader.skip(4);
            if ("name".equals(tabName)) {
                position = offset;
                break;
            }
        }
        if (position == -1)
            return null;
        reader.seek(position + 2);
        return readName(reader);
    }

    private static String readName(FontFileReader fontFile) throws IOException {
        long i = fontFile.getPointer();
        int n = fontFile.readUnsignedShort();
        long j = fontFile.readUnsignedShort() + i - 2;
        i += 2 * 2;
        String fullName = "";
        while (n-- > 0) {
            // getLogger().debug("Iteration: " + n);
            fontFile.seek(i);
            final int platformID = fontFile.readUnsignedShort();
            final int encodingID = fontFile.readUnsignedShort();
            final int languageID = fontFile.readUnsignedShort();

            int k = fontFile.readUnsignedShort();
            int l = fontFile.readUnsignedShort();

            if (((platformID == 1 || platformID == 3)
                    && (encodingID == 0 || encodingID == 1))) {
                fontFile.seek(j + fontFile.readUnsignedShort());
                String txt;
                if (platformID == 3) {
                    txt = fontFile.readString(l, FontFileReader.CHARSET_UTF_16BE);
                } else {
                    txt = fontFile.readString(l);
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

    public static List<String> getFullName(File font) {
        if (font == null)
            return null;
        FontFileReader reader = null;
        try {
            reader = new FontFileReader(font);
            final String header = reader.readString(4); // TTF_FIXED_SIZE (4 bytes)
            reader.seek(0);
            if (TAG_TTCF.equals(header)) {
                // TrueType Collection
                return readFullNames(reader);
            } else {
                // Others
                return Collections.singletonList(readFullName(reader));
            }
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
