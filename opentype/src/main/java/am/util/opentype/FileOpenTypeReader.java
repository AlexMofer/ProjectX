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
package am.util.opentype;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Reads a TrueType font file into a RandomAccessFile.
 * Created by Alex on 2018/9/5.
 */
public class FileOpenTypeReader implements OpenTypeReader {

    public static final String CHARSET_UTF_16BE = "UTF-16BE";
    public static final String CHARSET_ISO_8859_15 = "ISO-8859-15";
    private final RandomAccessFile mFile;
    private byte[] BUFFER;

    public FileOpenTypeReader(File font) throws IOException {
        mFile = new RandomAccessFile(font, "r");
    }

    @Override
    public void seek(long pos) throws IOException {
        mFile.seek(pos);
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        final long pos = mFile.getFilePointer();
        final long len = mFile.length();
        long np = pos + n;
        if (np > len) {
            np = len;
        }
        mFile.seek(np);
        /* return the actual number of bytes skipped */
        return np - pos;
    }

    @Override
    public long getPointer() throws IOException {
        return mFile.getFilePointer();
    }

    @Override
    public long length() throws IOException {
        return mFile.length();
    }

    @Override
    public int read() throws IOException {
        return mFile.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return mFile.read(b, off, len);
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return mFile.readUnsignedByte();
    }

    @Override
    public int readShort() throws IOException {
        return mFile.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return mFile.readUnsignedShort();
    }

    @Override
    public int readUnsignedInt24() throws IOException {
        final int ch1 = read();
        final int ch2 = read();
        final int ch3 = read();
        if ((ch1 | ch2 | ch3) < 0)
            throw new EOFException();
        return ((ch1 << 16) + (ch2 << 8) + ch3);
    }

    @Override
    public int readInt() throws IOException {
        return mFile.readInt();
    }

    @Override
    public int readUnsignedInt() throws IOException {
        return Math.abs(mFile.readInt());
    }

    @Override
    public float readFixed() throws IOException {
        final int integer = mFile.readShort();
        final int decimal = mFile.readShort();
        try {
            return integer + Integer.parseInt(Integer.toHexString(decimal)) * 0.0001f;
        } catch (Exception e) {
            return integer;
        }
    }

    @Override
    public float readFixed2Dot14() throws IOException {
        final int ch1 = read();
        final int ch2 = read();
        final int f2 = (ch1 >> 6) & 0x3;
        final int dot14 = (((ch1 << 2) & 0xff) << 6) + ch2;
        if (f2 == 3)
            // 11
            return -1 + dot14 / 16384.0f;
        else if (f2 == 2)
            // 10
            return -2 + dot14 / 16384.0f;
        else
            // 01 or 00
            return f2 + dot14 / 16384.0f;
    }

    @Override
    public long readLong() throws IOException {
        return mFile.readLong();
    }

    @Override
    public String readString(long length, String charsetName) throws IOException {
        if (BUFFER == null) {
            BUFFER = new byte[1024];
        }
        long i = length;
        final StringBuilder builder = new StringBuilder();
        while (i > 1024) {
            final int len = mFile.read(BUFFER);
            builder.append(new String(BUFFER, 0, len, charsetName));
            i -= 1024;
        }
        if (i > 0) {
            final int len = mFile.read(BUFFER, 0, (int) i);
            builder.append(new String(BUFFER, 0, len, charsetName));
        }
        return builder.toString();
    }

    @Override
    public void close() throws IOException {
        mFile.close();
    }
}
